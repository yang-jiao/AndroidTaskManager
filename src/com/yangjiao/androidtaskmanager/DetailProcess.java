package com.yangjiao.androidtaskmanager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class DetailProcess implements Comparable<DetailProcess> {
    private ProcessInfo.PsRow mPsrow = null;
    private ApplicationInfo mAppInfo = null;
    private PackageInfo mPkgInfo = null;
    private ActivityManager.RunningAppProcessInfo mRunningInfo = null;
    private String mTitle = null;
    private PackageManager mPkgMgr;
    private Intent mIntent = null;

    public DetailProcess(Context ctx, ActivityManager.RunningAppProcessInfo runinfo) {
        this.mRunningInfo = runinfo;
        mPkgMgr = ctx.getApplicationContext().getPackageManager();
    }

    public ProcessInfo.PsRow getPsrow() {
        return mPsrow;
    }

    public void setPsrow(ProcessInfo.PsRow psrow) {
        this.mPsrow = psrow;
    }

    public ApplicationInfo getAppinfo() {
        return mAppInfo;
    }

    public void setAppinfo(ApplicationInfo appinfo) {
        this.mAppInfo = appinfo;
    }

    public PackageInfo getPkginfo() {
        return mPkgInfo;
    }

    public void setPkginfo(PackageInfo pkginfo) {
        this.mPkgInfo = pkginfo;
    }

    public ActivityManager.RunningAppProcessInfo getRuninfo() {
        return mRunningInfo;
    }

    public void setRuninfo(ActivityManager.RunningAppProcessInfo runinfo) {
        this.mRunningInfo = runinfo;
    }

    public void fetchApplicationInfo(PackagesInfo pkg) {
        if (mAppInfo == null) mAppInfo = pkg.getInfo(mRunningInfo.processName);
    }

    public void fetchPackageInfo() {
        if (mPkgInfo == null && mAppInfo != null) mPkgInfo = ContextMenus.getInstance().getPackageInfo(mAppInfo.packageName);
    }

    public void fetchPsRow(ProcessInfo pi) {
        if (mPsrow == null) mPsrow = pi.getPsRow(mRunningInfo.processName);
    }


    public boolean isGoodProcess() {
        return mRunningInfo != null && mAppInfo != null && mPkgInfo != null && mPkgInfo.activities != null
                && (mPkgInfo.activities.length > 0);
    }

    public String getPackageName() {
        return mAppInfo.packageName;
    }

    public String getBaseActivity() {
        return mPkgInfo.activities[0].name;
    }

    public Intent getIntent() {
        if (mIntent != null) return mIntent;
        mIntent = null;
        try {
            mIntent = mPkgMgr.getLaunchIntentForPackage(mPkgInfo.packageName);
            if (mIntent != null) {
                mIntent = mIntent.cloneFilter();
                mIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                return mIntent;
            }
            if (mPkgInfo.activities.length == 1) {
                mIntent = new Intent(Intent.ACTION_MAIN);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                mIntent.setClassName(mPkgInfo.packageName, mPkgInfo.activities[0].name);
                return mIntent;
            }
            mIntent = IntentList.getIntent(mPkgInfo.packageName, mPkgMgr);
            if (mIntent != null) {
                mIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                return mIntent;
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
    }

    public String getTitle() {
        if (mTitle == null) mTitle = mAppInfo.loadLabel(mPkgMgr).toString();
        return mTitle;
    }

    @Override
    public int compareTo(DetailProcess another) {
        if (another != null) {
            return this.getTitle().compareTo(another.getTitle());
        }
        return -1;
    }

    
}
