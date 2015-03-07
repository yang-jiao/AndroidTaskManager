package com.yangjiao.androidtaskmanager;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class IntentList {
    private static List<ResolveInfo> mInfoList = null;

    public static synchronized List<ResolveInfo> getRunableList(PackageManager pm, boolean reload) {
        if (mInfoList == null || reload == true) {
            Intent baseIntent = new Intent(Intent.ACTION_MAIN);
            baseIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            mInfoList = pm.queryIntentActivities(baseIntent, 0);
        }
        return mInfoList;
    }

    public static Intent getIntent(String packageName, PackageManager pm) {
        List<ResolveInfo> list = getRunableList(pm, false);
        for (ResolveInfo info : list) {
            if (packageName.equals(info.activityInfo.packageName)) {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                i.setClassName(packageName, info.activityInfo.name);
                return i;
            }
        }
        return null;
    }

}
