package br.net.silvioluizsilva.astexample;

import br.net.silvioluizsilva.astatinecore.api.AstatineCoreApi;
import br.net.silvioluizsilva.astatinecore.api.AstatineCoreProvider;
import br.net.silvioluizsilva.astatinecore.api.DatabaseAccess;
import br.net.silvioluizsilva.astexample.command.ExampleCommand;
import br.net.silvioluizsilva.astexample.config.ExampleConfig;
import br.net.silvioluizsilva.astexample.database.ExampleMigration;
import br.net.silvioluizsilva.astexample.language.ExampleMessages;
import br.net.silvioluizsilva.astexample.listener.PlayerJoinListener;
import br.net.silvioluizsilva.astexample.repository.PlayerVisitRepository;
import br.net.silvioluizsilva.astexample.service.GreetingService;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Demonstra como um plugin independente consome os recursos do AstatineCore.
 *
 * @author Sílvio Luiz da Silva
 * @version 0.0.1
 */
public final class AstExample extends JavaPlugin {

    private GreetingService greetingService;

    /**
     * Registra o comando moderno durante o ciclo de vida apropriado.
     */
    @Override
    public void onLoad() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(ExampleCommand.create(this::requireGreetingService).build());
        });
    }

    /**
     * Carrega os componentes e conecta o plugin à API compartilhada.
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveBundledResource("languages/pt_BR.yml");
        saveBundledResource("languages/en_US.yml");

        ExampleConfig config = ExampleConfig.from(getConfig());
        ExampleMessages messages = new ExampleMessages(this, config.language());
        AstatineCoreApi api = AstatineCoreProvider.get();
        if (!AstatineCoreApi.API_VERSION.equals(api.apiVersion())) {
            throw new IllegalStateException("Versão incompatível da API AstatineCore: " + api.apiVersion());
        }

        DatabaseAccess database = api.database(this);
        PlayerVisitRepository repository = new PlayerVisitRepository(database);
        ExampleMigration migration = new ExampleMigration(this, database);
        if (api.settings().databaseEnabled()) {
            database.whenConnected().thenRun(() -> api.scheduler().runAsync(this, migration::migrate)).exceptionally(failure -> {
                getSLF4JLogger().error("Falha ao aplicar as migrações iniciais do AstExample.", failure);
                return null;
            });
        }
        greetingService = new GreetingService(this, api, repository, messages, migration);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(greetingService, config), this);
        getSLF4JLogger().info("AstExample habilitado com AstatineCore API {}.", api.apiVersion());
    }

    /**
     * Cancela as tarefas pertencentes a este plugin consumidor.
     */
    @Override
    public void onDisable() {
        AstatineCoreProvider.find().ifPresent(api -> api.scheduler().cancelAll(this));
    }

    private void saveBundledResource(String path) {
        if (!new File(getDataFolder(), path).isFile()) {
            saveResource(path, false);
        }
    }

    private GreetingService requireGreetingService() {
        if (greetingService == null) {
            throw new IllegalStateException("O serviço de saudações ainda não foi inicializado.");
        }
        return greetingService;
    }
}
