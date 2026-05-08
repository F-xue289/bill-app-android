package com.example.billapp;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DatabaseExecutor {
    private static final Executor executor = Executors.newSingleThreadExecutor();

    public static void execute(Runnable runnable) {
        executor.execute(runnable);
    }
}