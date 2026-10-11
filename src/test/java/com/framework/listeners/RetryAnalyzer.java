package com.framework.listeners;

import com.framework.config.ConfigManager;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/** Re-runs a failed test up to test.retry.count times (Allure marks retried tests). */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int attempts = 0;

    @Override
    public boolean retry(ITestResult result) {
        return attempts++ < ConfigManager.get().testRetryCount();
    }
}
