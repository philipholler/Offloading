package p7gruppe.p7.offloading.database;

public class QueryManager {
    public static String connectionString = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=password";

    public static String selectAllFromTable(){
        return "SELECT * FROM \"Clients\"";
    }


    public static String insertJob(String jobName, String jobPath){
        return "INSERT INTO \"Job\" (jobname, \"Jobpath\") VALUES ('"+jobName+"', '"+jobPath+"')";
    }
}
