package br.net.silvioluizsilva.pluginexample.repository;

import br.net.silvioluizsilva.pluginbase.api.DatabaseAccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.UUID;

/**
 * Persiste e consulta as visitas de jogadores.
 */
public final class PlayerVisitRepository {

    private final DatabaseAccess database;
    private final String playersTable;

    /**
     * Cria o repositório.
     *
     * @param database banco compartilhado
     */
    public PlayerVisitRepository(DatabaseAccess database) {
        this.database = Objects.requireNonNull(database, "database");
        this.playersTable = database.table("players");
    }

    /**
     * Incrementa atomicamente o número de visitas.
     *
     * @param playerId UUID do jogador
     * @param playerName nome atual
     * @return total atualizado
     */
    public long increment(UUID playerId, String playerName) {
        return database.transaction(connection -> {
            String upsertSql = """
                    INSERT INTO %s (player_uuid, player_name, visits)
                    VALUES (?, ?, 1)
                    ON DUPLICATE KEY UPDATE player_name = VALUES(player_name), visits = visits + 1
                    """.formatted(playersTable);
            try (PreparedStatement upsert = connection.prepareStatement(upsertSql)) {
                upsert.setString(1, playerId.toString());
                upsert.setString(2, playerName);
                upsert.executeUpdate();
            }
            String querySql = "SELECT visits FROM " + playersTable + " WHERE player_uuid = ?";
            try (PreparedStatement query = connection.prepareStatement(querySql)) {
                query.setString(1, playerId.toString());
                try (ResultSet result = query.executeQuery()) {
                    if (!result.next()) {
                        throw new IllegalStateException("A visita persistida não foi encontrada.");
                    }
                    return result.getLong("visits");
                }
            }
        });
    }
}
