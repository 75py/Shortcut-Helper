package com.nagopy.shortcut.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Parcelable;

import com.nagopy.shortcut.helper.data.ShortcutStatusHolder;

/**
 * ショートカットをインストールするブロードキャストを受信するレシーバー
 */
public class InstallShortcutReceiver extends BroadcastReceiver {

	/**
	 * ショートカットをインストールする際のACTION
	 */
	public static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";

	@Override
	public void onReceive(Context context, Intent data) {
		if (!ACTION_INSTALL_SHORTCUT.equals(data.getAction())) {
			return;
		}

		Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
		if (intent == null) {
			return;
		}

		String name = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
		if (name == null) {
			try {
				// ラベルがない場合はアプリ名を取得してみる
				PackageManager packageManager = context.getPackageManager();
				ActivityInfo activityInfo = packageManager.getActivityInfo(intent.getComponent(), 0);
				name = activityInfo.loadLabel(packageManager).toString();
			} catch (PackageManager.NameNotFoundException e) {
				return;
			}
		}

		Intent mainIntent = new Intent();
		mainIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		mainIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

		Parcelable icon = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
		if (icon != null) {
			mainIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
					data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE));
		} else {
			mainIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON));
		}

		mainIntent.setAction(ACTION_INSTALL_SHORTCUT);

		ShortcutStatusHolder holder = new ShortcutStatusHolder(context);
		holder.save(mainIntent);
	}
}
