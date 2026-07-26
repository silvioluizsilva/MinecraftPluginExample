package br.net.silvioluizsilva.pluginexample;

import br.net.silvioluizsilva.pluginbase.api.PluginBaseApi;
import br.net.silvioluizsilva.pluginbase.api.PluginBaseProvider;
import br.net.silvioluizsilva.pluginbase.api.DatabaseAccess;
import br.net.silvioluizsilva.pluginexample.command.ExampleCommand;
import br.net.silvioluizsilva.pluginexample.config.ExampleConfig;
import br.net.silvioluizsilva.pluginexample.database.ExampleMigration;
import br.net.silvioluizsilva.pluginexample.language.ExampleMessages;
import br.net.silvioluizsilva.pluginexample.listener.PlayerJoinListener;
import br.net.silvioluizsilva.pluginexample.repository.PlayerVisitRepository;
import br.net.silvioluizsilva.pluginexample.service.GreetingService;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Demonstra como um plugin independente consome os recursos do PluginBase.
 *
 * @author Sílvio Luiz da Silva
 * @version 0.0.1
 */
public final class PluginExample extends JavaPlugin {

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
        PluginBaseApi api = PluginBaseProvider.get();
        if (!PluginBaseApi.API_VERSION.equals(api.apiVersion())) {
            throw new IllegalStateException("Versão incompatível da API PluginBase: " + api.apiVersion());
        }

        DatabaseAccess database = api.database(this);
        PlayerVisitRepository repository = new PlayerVisitRepository(database);
        ExampleMigration migration = new ExampleMigration(this, database);
        if (api.settings().databaseEnabled()) {
            database.whenConnected().thenRun(() -> api.scheduler().runAsync(this, migration::migrate)).exceptionally(failure -> {
                getSLF4JLogger().error("Falha ao aplicar as migrações iniciais do PluginExample.", failure);
                return null;
            });
        }
        greetingService = new GreetingService(this, api, repository, messages, migration);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(greetingService, config), this);
        getSLF4JLogger().info("PluginExample habilitado com PluginBase API {}.", api.apiVersion());
    }

    /**
     * Cancela as tarefas pertencentes a este plugin consumidor.
     */
    @Override
    public void onDisable() {
        PluginBaseProvider.find().ifPresent(api -> api.scheduler().cancelAll(this));
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
