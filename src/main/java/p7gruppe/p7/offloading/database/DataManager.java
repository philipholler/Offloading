package p7gruppe.p7.offloading.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataManager {
    static ResultSet resultSet;

    public static void insertJobInDB(String jobName, String jobPath) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        ConnectionManager.updateSql(QueryManager.insertJob(jobName, jobPath, ts));
    }

    public static void updateJobStatus(String status, long jobID) {
        ConnectionManager.updateSql(QueryManager.updateJobStatus(status,jobID));

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

    public static String getJobResult(long jobID, String userName) throws SQLException {
        resultSet = ConnectionManager.selectSQL(QueryManager.selectJobResult(jobID, userName));
        while (resultSet.next()) {
            return resultSet.getString(1);
        }
        return "no result found";
    }

    public static void removeJob(long jobID) {
        ConnectionManager.updateSql(QueryManager.deleteJob(jobID));
    }

    public static String getJobPath(long jobID) throws SQLException {
        resultSet = ConnectionManager.selectSQL(QueryManager.selectJobPath(jobID));
        while (resultSet.next()) {
            return resultSet.getString(1);
        }
        return "no result found";

    }
}
