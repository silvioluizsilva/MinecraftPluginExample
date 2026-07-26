package br.net.silvioluizsilva.pluginexample.command;

import br.net.silvioluizsilva.pluginexample.service.GreetingService;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Constrói o comando Brigadier do plugin consumidor.
 */
public final class ExampleCommand {

    private ExampleCommand() {
    }

    /**
     * Cria a árvore do comando {@code /pluginexample hello}.
     *
     * @param serviceSupplier fornecedor da regra de negócio inicializada
     * @return árvore Brigadier
     */
    public static LiteralArgumentBuilder<CommandSourceStack> create(Supplier<GreetingService> serviceSupplier) {
        Objects.requireNonNull(serviceSupplier, "serviceSupplier");
        return Commands.literal("pluginexample")
                .requires(source -> source.getSender().hasPermission("pluginexample.command.hello"))
                .then(Commands.literal("hello").executes(context -> {
                    if (context.getSource().getSender() instanceof Player player) {
                        serviceSupplier.get().greet(player);
                        return 1;
                    }
                    return 0;
                }));
    }
}
