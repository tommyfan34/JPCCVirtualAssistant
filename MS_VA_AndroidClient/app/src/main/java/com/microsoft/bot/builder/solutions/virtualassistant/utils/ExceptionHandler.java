package com.microsoft.bot.builder.solutions.virtualassistant.utils;

import android.util.Log;
import java.lang.Thread.UncaughtExceptionHandler;

public class ExceptionHandler implements UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        System.out.println(throwable.getMessage());
    }
}
