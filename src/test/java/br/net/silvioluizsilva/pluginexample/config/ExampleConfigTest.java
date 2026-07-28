package br.net.silvioluizsilva.pluginexample.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica a configuração tipada do plugin consumidor.
 */
final class ExampleConfigTest {

    @Test
    void shouldLoadValidConfiguration() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("language", "en_US");
        yaml.set("greeting.record-player-joins", true);

        ExampleConfig config = ExampleConfig.from(yaml);

        assertEquals("en_US", config.language());
        assertTrue(config.recordPlayerJoins());
    }

    @Test
    void shouldRejectInvalidLocale() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("language", "../../invalid");

        assertThrows(IllegalArgumentException.class, () -> ExampleConfig.from(yaml));
    }
}
