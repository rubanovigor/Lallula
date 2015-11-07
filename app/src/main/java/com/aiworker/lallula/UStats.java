package com.aiworker.lallula;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by User on 3/2/15.
 */
public class UStats {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    public static final String TAG = UStats.class.getSimpleName();
    public static long endTime, startTime;

    @SuppressWarnings("ResourceType")
   /* public static void getStats(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        int interval = UsageStatsManager.INTERVAL_YEARLY;
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + dateFormat.format(startTime) );
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

        UsageEvents uEvents = usm.queryEvents(startTime,endTime);
        while (uEvents.hasNextEvent()){
            UsageEvents.Event e = new UsageEvents.Event();
            uEvents.getNextEvent(e);

            if (e != null){
                Log.d(TAG, "Event: " + e.getPackageName() + "\t" +  e.getTimeStamp());
            }
        }
    }*/

    public static List<UsageStats> getUsageStatsList(Context context){
        UsageStatsManager usm = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -0);
        endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, +0);

        calendar.add(Calendar.YEAR, -2);
//        calendar.add(Calendar.MONTH, -1);
//        calendar.add(Calendar.HOUR_OF_DAY, -24);
//        calendar.add(Calendar.HOUR_OF_DAY, -1);
        startTime = calendar.getTimeInMillis();

        Log.d(TAG, "Range start:" + dateFormat.format(startTime) );
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));

//        final List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST,startTime,endTime);
//        final List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);
//                List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY,startTime,endTime);
//        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY,startTime,endTime);
         List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY,startTime,endTime);
//        List<UsageStats> usageStatsList = usm.queryAndAggregateUsageStats(startTime,endTime);
        return usageStatsList;
    }

    public static void printUsageStats(List<UsageStats> usageStatsList){
        String app_stats_info = "period: " + dateFormat.format(startTime) + " - " + dateFormat.format(endTime) + "\n" + "app's foreground time in ms/min\n";
        for (UsageStats u : usageStatsList){
            Log.d(TAG, "Pkg: " + u.getPackageName() + "\t" + "ForegroundTime: "
                    + u.getTotalTimeInForeground()) ;

            if(u.getPackageName().contentEquals("com.facebook.orca") || u.getPackageName().contentEquals("com.facebook.katana")) {
//            if(u.getPackageName().contentEquals("com.augmentra.viewranger.android")) {
                app_stats_info = app_stats_info +
                        "Pkg: " + u.getPackageName() + "\t" +
                        ": " + u.getTotalTimeInForeground() + " s/" + Math.round(u.getTotalTimeInForeground() / 60000) + " m\n";
            }
        }

        MainActivity.tv_AppStat.setText(app_stats_info);
    }


    public static void printCurrentUsageStatus(Context context){
        printUsageStats(getUsageStatsList(context));
    }

    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }

}

