package br.net.silvioluizsilva.pluginexample.service;

import br.net.silvioluizsilva.pluginbase.api.DatabaseAccess;
import br.net.silvioluizsilva.pluginbase.api.PluginBaseApi;
import br.net.silvioluizsilva.pluginexample.PluginExample;
import br.net.silvioluizsilva.pluginexample.database.ExampleMigration;
import br.net.silvioluizsilva.pluginexample.language.ExampleMessages;
import br.net.silvioluizsilva.pluginexample.repository.PlayerVisitRepository;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class GreetingServiceTest {

    @Test
    void greetsWithoutDatabaseWorkWhenPersistenceIsDisabled() {
        PluginExample plugin = mock(PluginExample.class);
        PluginBaseApi api = mock(PluginBaseApi.class);
        DatabaseAccess database = mock(DatabaseAccess.class);
        PlayerVisitRepository repository = mock(PlayerVisitRepository.class);
        ExampleMessages messages = mock(ExampleMessages.class);
        ExampleMigration migration = mock(ExampleMigration.class);
        Player player = mock(Player.class);
        when(api.database(plugin)).thenReturn(database);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.getName()).thenReturn("Silvio");

        GreetingService service = new GreetingService(plugin, api, repository, messages, migration);
        service.handleJoin(player, false);

        verify(messages).send(player, "database-disabled");
        verify(messages).send(player, "hello", "name", "Silvio", "visits", "0");
    }
}
