package br.net.silvioluizsilva.pluginexample.language;

import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class ExampleMessagesTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void usesPortugueseFallbackAndSendsResolvedMessage() throws IOException {
        Path languages = Files.createDirectories(temporaryDirectory.resolve("languages"));
        Files.writeString(languages.resolve("pt_BR.yml"), "prefix: \"<green>[Example]</green> \"\nhello: \"Olá <name>\"\n");
        Plugin plugin = mock(Plugin.class);
        when(plugin.getDataFolder()).thenReturn(temporaryDirectory.toFile());
        CommandSender sender = mock(CommandSender.class);

        try (var bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getLogger).thenReturn(Logger.getLogger("ExampleMessagesTest"));
            ExampleMessages messages = new ExampleMessages(plugin, "en_US");
            messages.send(sender, "hello", "name", "Silvio");
        }

        verify(sender).sendMessage(any(net.kyori.adventure.text.Component.class));
    }

    @Test
    void rejectsAnOddNumberOfReplacements() {
        Plugin plugin = mock(Plugin.class);
        when(plugin.getDataFolder()).thenReturn(new File(temporaryDirectory.toFile(), "empty"));
        ExampleMessages messages = new ExampleMessages(plugin, "pt_BR");

        assertThrows(IllegalArgumentException.class, () -> messages.send(mock(CommandSender.class), "hello", "name"));
    }
}
