package br.net.silvioluizsilva.pluginexample.database;

import br.net.silvioluizsilva.pluginbase.api.DatabaseAccess;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class ExampleMigrationTest {

    @Test
    void appliesItsCatalogOnlyOnce() {
        Plugin plugin = mock(Plugin.class);
        DatabaseAccess database = mock(DatabaseAccess.class);
        when(plugin.getResource("sql/001_initial.sql")).thenReturn(new ByteArrayInputStream(
                "CREATE TABLE example_players (id INT);".getBytes(StandardCharsets.UTF_8)));

        ExampleMigration migration = new ExampleMigration(plugin, database);
        migration.migrate();
        migration.migrate();

        verify(database, times(1)).migrate(anyList());
    }

    @Test
    void rejectsMissingMigrationResource() {
        Plugin plugin = mock(Plugin.class);
        DatabaseAccess database = mock(DatabaseAccess.class);

        ExampleMigration migration = new ExampleMigration(plugin, database);

        IllegalStateException failure = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class, migration::migrate);
        assertEquals("Recurso SQL ausente: sql/001_initial.sql", failure.getMessage());
    }
}
