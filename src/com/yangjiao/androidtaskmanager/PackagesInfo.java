package com.yangjiao.androidtaskmanager;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class PackagesInfo {
    private List<ApplicationInfo> mAppList;

    public PackagesInfo(Context ctx) {
        PackageManager pm = ctx.getApplicationContext().getPackageManager();
        mAppList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
    }

    public ApplicationInfo getInfo(String name) {
        if (name == null) {
            return null;
        }
        for (ApplicationInfo appinfo : mAppList) {
            if (name.equals(appinfo.processName)) {
                return appinfo;
            }
        }
        return null;
    }

}
