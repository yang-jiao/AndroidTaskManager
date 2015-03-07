package com.yangjiao.androidtaskmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.yangjiao.androidtaskmanager.ItemListAdapters.ProcessListAdapter;
import com.yangjiao.androidtaskmanager.ItemListAdapters.TasksListAdapter;

public class AndroidTaskManager extends Activity {
    private ProcessInfo mPsInfo = null;
    ActivityManager mActivityMgr = null;
    private PackagesInfo mPackageInfo = null;


    private static final int STAT_TASK = 0;
    protected static final String ACTION_LOAD_FINISH = "com.yangjiao.androidtaskmanager.ACTION_LOAD_FINISH";

    private int mCurrentStat = STAT_TASK;
    private ProcessListAdapter mAdapter;
    private BroadcastReceiver mProcessReceiver = new ProcessesLoadedReceiver();
    private ArrayList<DetailProcess> mListdp;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.main);
        mActivityMgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        mPackageInfo = new PackagesInfo(this);
        ContextMenus.getInstance(this);
                
    }

    private ListView getListView() {
        return (ListView) this.findViewById(R.id.listbody);
    }
    
    void refresh() {
        setProgressBarIndeterminateVisibility(true);
        if (mCurrentStat == STAT_TASK) {

            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mPsInfo = new ProcessInfo();
                    getRunningProcess();
                    
                    Intent in = new Intent(ACTION_LOAD_FINISH);
                    AndroidTaskManager.this.sendBroadcast(in);
                }

            });
            t.start();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ACTION_LOAD_FINISH);
        this.registerReceiver(mProcessReceiver, filter);
        mPackageInfo = new PackagesInfo(this);
        refresh();
    }
    
    

    @Override
	protected void onDestroy() {
		super.onDestroy();
		ContextMenus.getInstance().release();
	}

	private void getRunningProcess() {
        List<RunningAppProcessInfo> list2 = mActivityMgr.getRunningAppProcesses();
        mListdp = new ArrayList<DetailProcess>();
        for (RunningAppProcessInfo ti : list2) {
            if (ti.processName.equals("system") || ti.processName.equals("com.android.phone")) {
                continue;
            }
            DetailProcess dp = new DetailProcess(this, ti);
            dp.fetchApplicationInfo(mPackageInfo);
            dp.fetchPackageInfo();
            dp.fetchPsRow(mPsInfo);
            if (dp.isGoodProcess()) {
                mListdp.add(dp);
                
            }
        }
        Collections.sort(mListdp);
        mAdapter = new ProcessListAdapter(this, mListdp);
    }


    private class ProcessesLoadedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context ctx, Intent intent) {
            AndroidTaskManager.this.setProgressBarIndeterminateVisibility(false);
            AndroidTaskManager.this.getListView().setAdapter(mAdapter);
            AndroidTaskManager.this.getListView().setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    if (mCurrentStat == STAT_TASK) {
                        DetailProcess dp = mListdp.get(arg2);
                        ContextMenus.getInstance().getTaskMenuDialog(dp).show();
                    }
                }
                
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mProcessReceiver);
    }

    
}
