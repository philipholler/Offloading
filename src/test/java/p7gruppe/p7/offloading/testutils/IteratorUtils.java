package p7gruppe.p7.offloading.testutils;

import p7gruppe.p7.offloading.data.enitity.UserEntity;

public class IteratorUtils {

    public static <T> int countLength(Iterable<T> all) {
        int count = 0;
        for (T t : all) {
            count += 1;
        }
        return count;
    }
}
