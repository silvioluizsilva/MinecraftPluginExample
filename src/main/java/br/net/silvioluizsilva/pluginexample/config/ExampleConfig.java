package br.net.silvioluizsilva.pluginexample.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

/**
 * Configuração tipada do plugin consumidor.
 *
 * @param language idioma ativo
 * @param recordPlayerJoins indica se entradas devem ser persistidas
 */
public record ExampleConfig(String language, boolean recordPlayerJoins) {

    /**
     * Cria e valida a configuração a partir do YAML.
     *
     * @param source configuração do Paper
     * @return configuração tipada
     */
    public static ExampleConfig from(FileConfiguration source) {
        Objects.requireNonNull(source, "source");
        String language = Objects.requireNonNullElse(source.getString("language"), "pt_BR");
        if (!language.matches("[a-z]{2}_[A-Z]{2}")) {
            throw new IllegalArgumentException("Idioma inválido: " + language);
        }
        return new ExampleConfig(language, source.getBoolean("greeting.record-player-joins", true));
    }
}
