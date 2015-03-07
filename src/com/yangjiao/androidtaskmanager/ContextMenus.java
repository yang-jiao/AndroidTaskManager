package com.yangjiao.androidtaskmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.widget.Toast;

public class ContextMenus {

	public static final int MENU_SWITCH = 0;
	public static final int MENU_KILL = 1;
	public static final int MENU_DETAIL = 2;
	public static final int MENU_UNINSTALL = 3;

	private static AndroidTaskManager mContext;
	private static ContextMenus instance;

	public static ContextMenus getInstance(AndroidTaskManager ctx) {
		if (instance == null) {
			instance = new ContextMenus(ctx);
		}

		return instance;
	}

	public static ContextMenus getInstance() {
		if (instance == null) {
			try {
				throw new Exception("ContextMenus has no activity");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		} else {
			return instance;
		}
	}

	private ContextMenus(AndroidTaskManager ctx) {
		mContext = ctx;
	}
	
	public void release(){
		mContext = null;
		instance = null;
	}

	public PackageInfo getPackageInfo(String name) {
		PackageInfo ret = null;
		try {
			ret = mContext.getPackageManager().getPackageInfo(name, PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public Dialog getTaskMenuDialog(final DetailProcess dp) {

		return new AlertDialog.Builder(mContext)
				.setTitle(dp.getTitle())
				.setItems(R.array.menu_task_operation,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case MENU_KILL: {
									mContext.mActivityMgr.restartPackage(dp
											.getPackageName());
									if (dp.getPackageName().equals(
											mContext.getPackageName()))
										return;
									mContext.refresh();
									return;
								}
								case MENU_SWITCH: {
									if (dp.getPackageName().equals(
											mContext.getPackageName()))
										return;
									Intent i = dp.getIntent();
									if (i == null) {
										Toast.makeText(mContext,
												R.string.message_switch_fail,
												Toast.LENGTH_LONG).show();
										return;
									}
									try {
										mContext.startActivity(i);
									} catch (Exception ee) {
										Toast.makeText(mContext, ee.getMessage(),
												Toast.LENGTH_LONG).show();
									}
									return;
								}
								case MENU_UNINSTALL: {
									Uri uri = Uri.fromParts("package",
											dp.getPackageName(), null);
									Intent it = new Intent(
											Intent.ACTION_DELETE, uri);
									try {
										mContext.startActivity(it);
									} catch (Exception e) {
										Toast.makeText(mContext, e.getMessage(),
												Toast.LENGTH_LONG).show();
									}
									return;
								}
								case MENU_DETAIL: {
									try {
										Intent detailsIntent = new Intent();
										detailsIntent
												.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
										detailsIntent
												.addCategory(Intent.CATEGORY_DEFAULT);
										detailsIntent.setData(Uri
												.parse("package:"
														+ dp.getPackageName()));
										detailsIntent
												.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										mContext.startActivity(detailsIntent);

									} catch (ActivityNotFoundException e) {
										e.printStackTrace();
									}
									return;
								}
								}
							}
						}).create();
	}
}
