package p7gruppe.p7.offloading.database;
import java.sql.*;
public class ConnectionManager {

    public static void updateSql(String query) {
        try  {
            Connection conn = DriverManager.getConnection(QueryManager.connectionString);
            Statement statement = conn.createStatement();
            statement.executeUpdate(query);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet selectSQL(String query) throws SQLException {
        Connection conn = DriverManager.getConnection(QueryManager.connectionString);
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(query);
        return rs;
    }

    public static int updateSqlWithGeneratedKey(String query, String primaryKeyName) {
        String[] id_col = {primaryKeyName};
        try (Connection conn = DriverManager.getConnection(QueryManager.connectionString)) {
            Statement statement = conn.createStatement();
            statement.executeUpdate(query, id_col);
            ResultSet rs = statement.getGeneratedKeys();
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
