package br.net.silvioluizsilva.pluginexample.listener;

import br.net.silvioluizsilva.pluginexample.config.ExampleConfig;
import br.net.silvioluizsilva.pluginexample.service.GreetingService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

/**
 * Traduz eventos do Paper em chamadas da camada de serviço.
 */
public final class PlayerJoinListener implements Listener {

    private final GreetingService greetingService;
    private final ExampleConfig config;

    /**
     * Cria o listener.
     *
     * @param greetingService regra de negócio
     * @param config configuração ativa
     */
    public PlayerJoinListener(GreetingService greetingService, ExampleConfig config) {
        this.greetingService = Objects.requireNonNull(greetingService, "greetingService");
        this.config = Objects.requireNonNull(config, "config");
    }

    /**
     * Encaminha uma entrada de jogador ao serviço.
     *
     * @param event evento do Paper
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        greetingService.handleJoin(event.getPlayer(), config.recordPlayerJoins());
    }
}
