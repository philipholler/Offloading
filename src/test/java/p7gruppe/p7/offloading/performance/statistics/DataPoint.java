package p7gruppe.p7.offloading.performance.statistics;

public class DataPoint<T> {

    public final long timestamp;
    public final T value;

    public DataPoint(long timestamp, T value) {
        this.timestamp = timestamp;
        this.value = value;
    }

}
