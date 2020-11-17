package p7gruppe.p7.offloading.database;

import p7gruppe.p7.offloading.model.Job;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataManager {
    static ResultSet resultSet;


    public static List getJobsBelongingToUser(String userName) throws SQLException {
        resultSet = ConnectionManager.selectSQL(QueryManager.selectJobsWithUSer(userName));
        List<Job> listOfJobs = new ArrayList<>();
        while (resultSet.next()) {
            Job job = new Job();
            job.setId(resultSet.getInt(1));
            job.setJobpath(resultSet.getString(2));
            job.setStatus(resultSet.getString(3));
            //job.setTimestamp(resultSet.getTimestamp(4));
            job.setAssignedUser(resultSet.getString(5));
            listOfJobs.add(job);
        }
        return listOfJobs;
    }

    public static List getCurrentJobs(String userName) throws SQLException {
        resultSet = ConnectionManager.selectSQL(QueryManager.selectAllJobs(userName));
        ArrayList<String> list = new ArrayList();
        while (resultSet.next()) {
            String name = resultSet.getString(2);
        }
        return list;
    }

    public static String getJobResult(long jobID, String userName) throws SQLException {
        resultSet = ConnectionManager.selectSQL(QueryManager.selectJobResult(jobID, userName));
        while (resultSet.next()) {
            return resultSet.getString(1);
        }
        return "no result found";
    }

    public static String getJobPath(long jobID) throws SQLException {
        resultSet = ConnectionManager.selectSQL(QueryManager.selectJobPath(jobID));
        while (resultSet.next()) {
            return resultSet.getString(1);
        }
        return "no result found";

    }

    public static String getFIFOScheduler() throws SQLException {
        resultSet = ConnectionManager.selectSQL(QueryManager.selectFIFOJob());
        while (resultSet.next()) {
            return resultSet.getString(1);
        }
        return "no result found";

    }

    public static void insertJobInDB(String jobName, String jobPath, String username) {
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        long pk = ConnectionManager.updateSqlWithGeneratedKey(QueryManager.insertJob(jobName, jobPath, ts), "jobid");
        ConnectionManager.updateSql(QueryManager.insertJobRelations(pk, username));
    }

    public static void updateJobStatus(String status, long jobID) {
        ConnectionManager.updateSql(QueryManager.updateJobStatus(status, jobID));
    }


    public static void createUser(String userName, String pass) {
        ConnectionManager.updateSql(QueryManager.createUser(userName, pass));
    }


    public static void removeJob(long jobID) {
        ConnectionManager.updateSql(QueryManager.deleteJob(jobID));
    }


}
