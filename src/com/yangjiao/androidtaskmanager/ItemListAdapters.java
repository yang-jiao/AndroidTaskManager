package com.yangjiao.androidtaskmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yangjiao.androidtaskmanager.ProcessInfo.PsRow;

public class ItemListAdapters {

    public final static class TasksListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<RunningTaskInfo> mTaskList;
        private ProcessInfo mPsInfoList;

        public TasksListAdapter(Context context, List<RunningTaskInfo> list,ProcessInfo psInfo) {
            // Cache the LayoutInflate
            mInflater = LayoutInflater.from(context);
            mTaskList = list;
            mPsInfoList = psInfo;
        }

        public int getCount() {
            return mTaskList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_main, null);

                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.list_icon);
                holder.text_name = (TextView) convertView.findViewById(R.id.list_name);
                holder.text_size = (TextView) convertView.findViewById(R.id.list_size);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            RunningTaskInfo ti = mTaskList.get(position);
            convertView.setVisibility(View.VISIBLE);
            String cmd = ti.baseActivity.getPackageName();
            holder.text_name.setText(cmd);
            PsRow row = mPsInfoList.getPsRow(cmd);
            if (row == null) {
                holder.text_size.setText(R.string.memory_unknown);
            } else {
                holder.text_size.setText((int) Math.ceil(row.mem / 1024) + "K");
            }
            return convertView;
        }

    }

    public final static class ProcessListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<DetailProcess> list;
        private PackageManager pm;

        public ProcessListAdapter(Context context, ArrayList<DetailProcess> list) {
            // Cache the LayoutInflate
            mInflater = LayoutInflater.from(context);
            this.list = list;
            this.pm = context.getPackageManager();
        }

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_main, null);

                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.list_icon);
                holder.text_name = (TextView) convertView.findViewById(R.id.list_name);
                holder.text_size = (TextView) convertView.findViewById(R.id.list_size);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final DetailProcess dp = list.get(position);
            convertView.setVisibility(View.VISIBLE);
            holder.icon.setImageDrawable(dp.getAppinfo().loadIcon(pm));
            holder.text_name.setText(dp.getTitle());
            
            PsRow row = dp.getPsrow();
            if (row == null) {
                holder.text_size.setText(R.string.memory_unknown);
            } else {
                holder.text_size.setText((int) Math.ceil(row.mem / 1024) + "K");
            }
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    ContextMenus.getInstance().getTaskMenuDialog(dp).show();
                }
                
            });
            return convertView;
        }

    }

    private static class ViewHolder {
        ImageView icon;
        TextView text_name;
        TextView text_size;
    }

}
