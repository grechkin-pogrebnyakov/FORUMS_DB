package executor;

import handlers.TResultHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by serg on 22.11.14.
 */

public class PreparedTExecutor {
    public <T> T execQuery(Connection connection,
                           String query, ArrayList params,
                           TResultHandler<T> handler)
            throws SQLException {
        T value;
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try(ResultSet result = stmt.executeQuery()) {
                value = handler.handle(result);
            }
        }
        return value;
    }

    public int execInsert(Connection connection, String update,
                          ArrayList params)
                throws SQLException {
        int id = -1;
        try(PreparedStatement stmt = connection.prepareStatement(update)) {
            connection.setAutoCommit(false);
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            stmt.executeUpdate();
            stmt.execute("SELECT LAST_INSERT_ID()");
            try (ResultSet result = stmt.getResultSet()) {
                if (result.next()) {
                    id = result.getInt(1);
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
        }
        return id;
    }

    public int execUpdate(Connection connection, String update,
                          ArrayList params)
            throws SQLException {
        try(PreparedStatement stmt = connection.prepareStatement(update)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            return stmt.executeUpdate();
        }
    }
}
