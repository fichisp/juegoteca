package com.juegoteca.util;

import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;

public class CheckLaunchDatesService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        final SharedPreferences settings = getApplicationContext().getSharedPreferences("UserInfo",
                0);
        if(settings.contains("show_launched_notification") && settings.getBoolean("show_launched_notification", true)) {
            Utilidades utilidades = new Utilidades(this);
            utilidades.checkLaunchDates();
            SharedPreferences.Editor editor = settings.edit();
            //clean old
            for (String key : settings.getAll().keySet()) {
                if (key.startsWith(String.valueOf(jobParameters.getJobId()))) {
                    editor.remove(key);
                }
            }

            editor.putBoolean(jobParameters.getJobId() +"_"+ jobParameters.getExtras().getString("currentDay"), true);
            editor.commit();
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
