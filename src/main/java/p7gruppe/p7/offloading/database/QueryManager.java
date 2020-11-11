package p7gruppe.p7.offloading.database;

public class QueryManager {
    public static String connectionString = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=password";

    public static String selectAllFromTable() {
        return "SELECT * FROM \"Clients\"";
    }


    public static String insertJob(String jobName, String jobPath) {
        return "INSERT INTO \"Job\" (jobname, \"Jobpath\") VALUES ('" + jobName + "', '" + jobPath + "')";
    }

    public static String selectAllJobs(String username) {
        return "SELECT jobid, name, jobpath, status, username FROM \"Jobs\"\n" +
                "INNER JOIN \"Device\" D on D.deviceid = \"Jobs\".deviceid\n" +
                "INNER JOIN \"Clients\" C on C.clientid = D.clientid\n" +
                "WHERE username = '" + username + "'";
    }

    public static String createUser(String username, String pass) {
        return "INSERT INTO \"Clients\" (username, password) VALUES ('" + username + "','" + pass + "')";
    }

    public static String selectJobResult(int jobID, String userName) {
        return "SELECT resultpath FROM \"Jobs\" INNER JOIN \"Device\" D on D.deviceid = \"Jobs\".deviceid\n" +
                "INNER JOIN \"Clients\" C on C.clientid = D.clientid\n" +
                "WHERE jobid = " + jobID + " AND username = '" + userName + "'";
    }

    public static String deleteJob(int jobID) {
        return "DELETE FROM \"Jobs\" WHERE jobid = " + jobID + "";
    }


}
