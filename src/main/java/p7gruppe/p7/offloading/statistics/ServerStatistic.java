package p7gruppe.p7.offloading.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerStatistic {

    private static HashMap<String, List<DataPoint<Long>>> userToCPUTimeDataPoints = new HashMap<>();

    public static void reset(){
        userToCPUTimeDataPoints = new HashMap<>();
    }

    public static void addCPUTimeDataPoint(long newValue, String username){
        userToCPUTimeDataPoints.putIfAbsent(username, new ArrayList<>());
        List<DataPoint<Long>> userDataPoints = userToCPUTimeDataPoints.get(username);
        long timeStamp = System.currentTimeMillis();
        if(!userDataPoints.isEmpty()) {
            // Add padding point, showing that until one now, the value has remained unchanged since the last datapoint
            long previousValue = userDataPoints.get(userDataPoints.size() - 1).value;
            userDataPoints.add(new DataPoint<>(timeStamp - 1, previousValue));
        }
        userDataPoints.add(new DataPoint<>(timeStamp, newValue));
    }


    public static List<DataPoint<Long>> getCPUTimeDataPoints(String username) {
        return userToCPUTimeDataPoints.get(username);
    }
}
