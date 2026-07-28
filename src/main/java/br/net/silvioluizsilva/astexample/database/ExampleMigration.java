package br.net.silvioluizsilva.astexample.database;

import br.net.silvioluizsilva.astatinecore.api.DatabaseAccess;
import br.net.silvioluizsilva.astatinecore.api.DatabaseMigration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Entrega as migrações do consumidor ao executor central do AstatineCore.
 */
public final class ExampleMigration {

    private final Plugin plugin;
    private final DatabaseAccess database;
    private final AtomicBoolean migrated = new AtomicBoolean();

    /**
     * Cria o catálogo de migrações do consumidor.
     *
     * @param plugin plugin consumidor
     * @param database acesso isolado ao banco
     */
    public ExampleMigration(Plugin plugin, DatabaseAccess database) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.database = Objects.requireNonNull(database, "database");
    }

    /**
     * Aplica o catálogo com bloqueio, checksum e histórico central.
     */
    public synchronized void migrate() {
        if (migrated.get()) {
            return;
        }
        database.migrate(List.of(new DatabaseMigration(
                1,
                "Initial player visits",
                readResource("sql/001_initial.sql")
        )));
        migrated.set(true);
    }

    private String readResource(String path) {
        try (InputStream input = plugin.getResource(path)) {
            if (input == null) {
                throw new IllegalStateException("Recurso SQL ausente: " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao carregar a migração " + path + ".", exception);
        }
    }
}
