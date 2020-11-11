package p7gruppe.p7.offloading.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    static ResultSet resultSet;

    public static void insertJobInDB(String jobName, String jobPath) {
        ConnectionManager.updateSql(QueryManager.insertJob(jobName, jobPath));
    }

    public static List getCurrentJobs(String userName) throws SQLException {
        resultSet = ConnectionManager.selectSQL(QueryManager.selectAllJobs(userName));
        ArrayList<String> list = new ArrayList();
        while (resultSet.next()) {
            String name = resultSet.getString(2);
        }
        return list;
    }

    public static void createUser(String userName, String pass) {
        ConnectionManager.updateSql(QueryManager.createUser(userName, pass));
    }

    public String selectJobResult(int jobID, String userName) throws SQLException {
        resultSet = ConnectionManager.selectSQL(QueryManager.selectJobResult(jobID, userName));
        while (resultSet.next()) {
            return resultSet.getString(2);
        }
        return "no result found";
    }

    public void removeJob(int jobID) {
        ConnectionManager.updateSql(QueryManager.deleteJob(jobID));
    }

}
