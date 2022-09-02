package xyz.illuminate.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.IntDef;

import java.io.RandomAccessFile;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

public class SharedConfig {

    private static final Object localIdSync = new Object();
    public static boolean animationsEnabled = true;
    public static boolean debugLinesEnabled = false;
    public static boolean noStatusBar = false;
    public static boolean disableDrawerAnimation = false;
    public static boolean smoothKeyboard = true;
    public static boolean isGameVersionSelected;
    public static boolean disableDrawerAnimations = false;
    public static float fontSize = 16.0f;
    public static int mapPreviewType;
    public static int animationsEnterDuration;
    public static int animationsExitDuration;
    public static String storageCacheDir;
    public static String gameVersion;
    private static int lastLocalId = -210000;

    private static int devicePerformanceClass;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        if (ApplicationLoader.applicationContext != null) {
            SharedPreferences pref = getPreferences();
            animationsEnabled = pref.getBoolean("animations", true);
            debugLinesEnabled = pref.getBoolean("debugLines", false);
            animationsEnterDuration = pref.getInt("animationsEnterDuration", 300);
            animationsExitDuration = pref.getInt("animationsExitDuration", 275);
            disableDrawerAnimation = pref.getBoolean("disableDrawerAnimation", false);
            isGameVersionSelected = pref.getBoolean("isGameVersionSelected", false);

            lastLocalId = pref.getInt("lastLocalId", -210000);

            pref.edit().putInt("lastLocalId", lastLocalId).commit();
        }
    }

    public static void put(String key, String value) {
        getPreferences().edit().putString(key, value).commit();
        loadConfig();
    }

    public static SharedPreferences getPreferences() {
        return ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
    }

    public static int getLastLocalId() {
        int value;
        synchronized (localIdSync) {
            value = lastLocalId--;
        }
        return value;
    }

    public static void saveConfig() {
    }

    public static boolean chatBlurEnabled() {
        return false;
    }


    public final static int PERFORMANCE_CLASS_LOW = 0;
    public final static int PERFORMANCE_CLASS_AVERAGE = 1;
    public final static int PERFORMANCE_CLASS_HIGH = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            PERFORMANCE_CLASS_LOW,
            PERFORMANCE_CLASS_AVERAGE,
            PERFORMANCE_CLASS_HIGH
    })
    public @interface PerformanceClass {
    }

    @PerformanceClass
    public static int getDevicePerformanceClass() {
        if (devicePerformanceClass == -1) {
            int androidVersion = Build.VERSION.SDK_INT;
            int cpuCount = Runtime.getRuntime().availableProcessors();
            int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
            int totalCpuFreq = 0;
            int freqResolved = 0;
            for (int i = 0; i < cpuCount; i++) {
                try {
                    RandomAccessFile reader = new RandomAccessFile(String.format(Locale.ENGLISH, "/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq", i), "r");
                    String line = reader.readLine();
                    if (line != null) {
                        totalCpuFreq += Utilities.parseInt(line) / 1000;
                        freqResolved++;
                    }
                    reader.close();
                } catch (Throwable ignore) {
                }
            }
            int maxCpuFreq = freqResolved == 0 ? -1 : (int) Math.ceil(totalCpuFreq / (float) freqResolved);

            if (androidVersion < 21 || cpuCount <= 2 || memoryClass <= 100 || cpuCount <= 4 && maxCpuFreq != -1 && maxCpuFreq <= 1250 || cpuCount <= 4 && maxCpuFreq <= 1600 && memoryClass <= 128 && androidVersion <= 21 || cpuCount <= 4 && maxCpuFreq <= 1300 && memoryClass <= 128 && androidVersion <= 24) {
                devicePerformanceClass = PERFORMANCE_CLASS_LOW;
            } else if (cpuCount < 8 || memoryClass <= 160 || maxCpuFreq != -1 && maxCpuFreq <= 2050 || maxCpuFreq == -1 && cpuCount == 8 && androidVersion <= 23) {
                devicePerformanceClass = PERFORMANCE_CLASS_AVERAGE;
            } else {
                devicePerformanceClass = PERFORMANCE_CLASS_HIGH;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("device performance info selected_class = " + devicePerformanceClass + " (cpu_count = " + cpuCount + ", freq = " + maxCpuFreq + ", memoryClass = " + memoryClass + ", android version " + androidVersion + ")");
            }
        }

        return devicePerformanceClass;
    }
}
