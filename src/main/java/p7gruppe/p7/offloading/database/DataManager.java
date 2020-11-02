package p7gruppe.p7.offloading.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataManager {
    static ResultSet resultSet;

    public static String getFirstDbname() throws SQLException {
        resultSet = ConnectionManager.selectSQL(QueryManager.selectAllFromTable());
        while (resultSet.next()) {
            String name = resultSet.getString(2);
            return name;
        }
        return "";

    }
}
