package com.ex2.shenkar.todolist;

/**
 * Created by Meschiany on 16/12/2015.
 */
public class Utils {
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}
