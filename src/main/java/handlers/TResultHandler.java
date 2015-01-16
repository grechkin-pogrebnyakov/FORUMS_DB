package handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by serg on 22.11.14.
 */

public interface TResultHandler<T> {
    T handle(ResultSet resultSet) throws SQLException;
}