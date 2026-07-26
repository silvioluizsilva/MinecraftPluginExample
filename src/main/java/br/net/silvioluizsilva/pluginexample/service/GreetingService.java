package br.net.silvioluizsilva.pluginexample.service;

import br.net.silvioluizsilva.pluginbase.api.PluginBaseApi;
import br.net.silvioluizsilva.pluginbase.api.DatabaseAccess;
import br.net.silvioluizsilva.pluginexample.PluginExample;
import br.net.silvioluizsilva.pluginexample.language.ExampleMessages;
import br.net.silvioluizsilva.pluginexample.database.ExampleMigration;
import br.net.silvioluizsilva.pluginexample.repository.PlayerVisitRepository;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mantém a regra de negócio das saudações e visitas.
 */
public final class GreetingService {

    private final PluginExample plugin;
    private final PluginBaseApi api;
    private final PlayerVisitRepository repository;
    private final ExampleMessages messages;
    private final DatabaseAccess database;
    private final ExampleMigration migration;
    private final Map<UUID, Long> visits = new ConcurrentHashMap<>();

    /**
     * Cria o serviço de saudações.
     *
     * @param plugin plugin consumidor
     * @param api API do PluginBase
     * @param repository repositório de visitas
     * @param messages mensagens traduzidas
     * @param migration migração recuperável do consumidor
     */
    public GreetingService(
            PluginExample plugin,
            PluginBaseApi api,
            PlayerVisitRepository repository,
            ExampleMessages messages,
            ExampleMigration migration
    ) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.api = Objects.requireNonNull(api, "api");
        this.repository = Objects.requireNonNull(repository, "repository");
        this.messages = Objects.requireNonNull(messages, "messages");
        this.database = api.database(plugin);
        this.migration = Objects.requireNonNull(migration, "migration");
    }

    /**
     * Registra a entrada e envia a saudação sem bloquear a thread principal.
     *
     * @param player jogador conectado
     * @param persist indica se a visita deve ser persistida
     */
    public void handleJoin(Player player, boolean persist) {
        if (!persist || !database.isConnected()) {
            messages.send(player, "database-disabled");
            greet(player, visits.getOrDefault(player.getUniqueId(), 0L));
            return;
        }
        UUID playerId = player.getUniqueId();
        String playerName = player.getName();
        api.scheduler().runAsync(plugin, () -> recordAndRespond(playerId, playerName));
    }

    /**
     * Envia a saudação usando o último total conhecido.
     *
     * @param player destinatário
     */
    public void greet(Player player) {
        greet(player, visits.getOrDefault(player.getUniqueId(), 0L));
    }

    private void recordAndRespond(UUID playerId, String playerName) {
        try {
            migration.migrate();
            long total = repository.increment(playerId, playerName);
            visits.put(playerId, total);
            api.scheduler().runSync(plugin, () -> {
                Player online = plugin.getServer().getPlayer(playerId);
                if (online != null) {
                    greet(online, total);
                }
            });
        } catch (RuntimeException exception) {
            plugin.getSLF4JLogger().error("Falha ao registrar a visita do jogador {}.", playerId, exception);
        }
    }

    private void greet(Player player, long total) {
        messages.send(player, "hello", "name", player.getName(), "visits", Long.toString(total));
    }
}
