package p7gruppe.p7.offloading.database;

import java.sql.Timestamp;

public class QueryManager {
    public static String connectionString = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=password";

    public static String selectAllFromTable() {
        return "SELECT * FROM \"clients\"";
    }


    public static String insertJob(String jobName, String jobPath, Timestamp ts) {
        return "INSERT INTO \"jobs\" (name,\"jobpath\",status, \"resultpath\",created) VALUES ('" + jobName + "', '" + jobPath + "','waiting','" + jobPath + "','" + ts + "')";
    }

    public static String insertJobRelations(long jobID, String userName) {
        return "INSERT INTO jobrelations (jobid, clientid)  SELECT " + jobID + " ,clientid FROM clients WHERE username = '" + userName + "' ";
    }

    public static String selectJobsWithUSer(String username){
        return "SELECT j.jobid,jobpath,status,created,username FROM jobs INNER JOIN jobrelations j on jobs.jobid = j.jobid\n" +
                "INNER JOIN clients c on c.clientid = j.clientid WHERE username = '"+username+"'";
    }

    public static String selectAllJobs(String username) {
        return "SELECT jobid, name, jobpath, status, username FROM \"jobs\"\n" +
                "INNER JOIN \"Device\" D on D.deviceid = \"Jobs\".deviceid\n" +
                "INNER JOIN \"Clients\" C on C.clientid = D.clientid\n" +
                "WHERE username = '" + username + "'";
    }

    public static String createUser(String username, String pass) {
        return "INSERT INTO \"Clients\" (username, password) VALUES ('" + username + "','" + pass + "')";
    }

    public static String selectJobResult(long jobID, String userName) {
        return "SELECT resultpath FROM \"jobs\" INNER JOIN \"Device\" D on D.deviceid = \"Jobs\".deviceid\n" +
                "INNER JOIN \"Clients\" C on C.clientid = D.clientid\n" +
                "WHERE jobid = " + jobID + " AND username = '" + userName + "'";
    }

    public static String deleteJob(long jobID) {
        return "DELETE FROM \"jobs\" WHERE jobid = " + jobID + "";
    }

    public static String selectJobPath(long jobID) {
        return "SELECT jobpath\n" +
                "    FROM \"jobs\"\n" +
                "    INNER JOIN \"Device\" D on D.deviceid = \"Jobs\".deviceid\n" +
                "    INNER JOIN \"Clients\" C on C.clientid = D.clientid\n" +
                "    WHERE jobid = " + jobID + "LIMIT 1";
    }

    public static String updateJobStatus(String status, long jobID) {
        return "UPDATE \"Jobs\" SET status = '" + status + "' WHERE jobid =" + jobID + "";
    }
}
