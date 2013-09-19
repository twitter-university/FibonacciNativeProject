package com.twitter.android.fibonaccinative;

import android.util.Log;

public class FibLib {
    private static final String TAG = "FibLib";

    private static long fib(long n) {
        return n <= 0 ? 0 : n == 1 ? 1 : fib(n - 1) + fib(n - 2);
    }

    // Recursive Java implementation of the Fibonacci algorithm
    public static long fibJR(long n) {
        Log.d(TAG, "fibJR(" + n + ")");
        return fib(n);
    }

    // Iterative Java implementation of the Fibonacci algorithm
    public static long fibJI(long n) {
        Log.d(TAG, "fibJI(" + n + ")");
        long previous = -1;
        long result = 1;
        for (long i = 0; i <= n; i++) {
            long sum = result + previous;
            previous = result;
            result = sum;
        }
        return result;
    }

    // Recursive Native implementation of the Fibonacci algorithm
    public native static long fibNR(long n);

    // Iterative Native implementation of the Fibonacci algorithm
    public native static long fibNI(long n);

    static {
        System.loadLibrary("com_twitter_android_fibonaccinative_FibLib");
    }
}
