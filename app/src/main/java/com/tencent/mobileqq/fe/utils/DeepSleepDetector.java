package com.tencent.mobileqq.fe.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

public class DeepSleepDetector {
    private static final String TAG = "fekit_DeepSleepDetector";
    private static final Object lock = new Object();
    private static Handler checkHandler;
    private static CheckRunnable checkRunnable;

    public static String getCheckResult() {
        CheckRunnable checkRunnable2 = checkRunnable;
        return checkRunnable2 != null ? String.valueOf(checkRunnable2.e()) : "-1";
    }

    public static void startCheck() {
        Log.d("DeepSleepDetector", "startCheck");
        if (checkHandler == null) {
            checkHandler = new Handler(Looper.getMainLooper());
            CheckRunnable checkRunnable2 = new CheckRunnable();
            checkRunnable = checkRunnable2;
            checkHandler.postDelayed(checkRunnable2, 1000L);
        }
    }

    private static void stopCheck() {
        CheckRunnable checkRunnable2;
        synchronized (lock) {
            Log.d("DeepSleepDetector", "stop check");
            Handler handler = checkHandler;
            if (handler != null && (checkRunnable2 = checkRunnable) != null) {
                handler.removeCallbacks(checkRunnable2);
            }
            checkHandler = null;
            checkRunnable = null;
        }
    }

    static class CheckRunnable implements Runnable {

        /* renamed from: e  reason: collision with root package name */
        private long f170345e = SystemClock.elapsedRealtime();

        /* renamed from: f  reason: collision with root package name */
        private long f170346f;

        CheckRunnable() {
        }

        public float e() {
            long elapsedRealtime = SystemClock.elapsedRealtime() - this.f170345e;
            Log.d("DeepSleepDetector", "check result count " + this.f170346f + " cost time " + elapsedRealtime);
            return (((float) elapsedRealtime) / 1000.0f) - ((float) this.f170346f);
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (DeepSleepDetector.lock) {
                this.f170346f++;
                if (DeepSleepDetector.checkHandler != null) {
                    DeepSleepDetector.checkHandler.postDelayed(this, 1000L);
                }
            }
        }
    }
}
