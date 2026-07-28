package br.net.silvioluizsilva.pluginexample.language;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Objects;

/**
 * Carrega e envia mensagens do plugin consumidor.
 */
public final class ExampleMessages {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final YamlConfiguration messages;

    /**
     * Carrega o idioma escolhido, usando português como fallback.
     *
     * @param plugin plugin consumidor
     * @param locale idioma configurado
     */
    public ExampleMessages(Plugin plugin, String locale) {
        Objects.requireNonNull(plugin, "plugin");
        File file = new File(plugin.getDataFolder(), "languages/" + locale + ".yml");
        if (!file.isFile()) {
            file = new File(plugin.getDataFolder(), "languages/pt_BR.yml");
        }
        messages = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Envia uma mensagem com valores tratados como texto literal.
     *
     * @param sender destinatário
     * @param key chave da mensagem
     * @param replacements pares de marcador e valor
     */
    public void send(CommandSender sender, String key, String... replacements) {
        if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("Os marcadores devem ser informados em pares.");
        }
        TagResolver.Builder resolver = TagResolver.builder();
        for (int index = 0; index < replacements.length; index += 2) {
            resolver.resolver(Placeholder.unparsed(replacements[index], replacements[index + 1]));
        }
        String prefix = messages.getString("prefix", "");
        String message = messages.getString(key, "<red>Missing message: " + key + "</red>");
        sender.sendMessage(miniMessage.deserialize(prefix + message, resolver.build()));
    }
}
