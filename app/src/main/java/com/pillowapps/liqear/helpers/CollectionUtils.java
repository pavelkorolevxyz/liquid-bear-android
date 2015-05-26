package com.pillowapps.liqear.helpers;

import java.util.List;

public class CollectionUtils {
    public static <T> T last(List<T> collection) {
        if (collection == null) return null;
        int size = collection.size();
        if (size > 0) {
            return collection.get(size - 1);
        } else {
            return null;
        }
    }
}
