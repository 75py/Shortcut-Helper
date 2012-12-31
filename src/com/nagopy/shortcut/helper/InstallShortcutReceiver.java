package com.nagopy.shortcut.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Parcelable;

public class InstallShortcutReceiver extends BroadcastReceiver {

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
				PackageManager pm = context.getPackageManager();
				ActivityInfo info = pm.getActivityInfo(intent.getComponent(), 0);
				name = info.loadLabel(pm).toString();
			} catch (PackageManager.NameNotFoundException nnfe) {
				return;
			}
		}

		Intent sendIntent = new Intent();
		sendIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		sendIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

		Parcelable icon = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
		if (icon != null) {
			sendIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
					data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE));
		} else {
			sendIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON));
		}

		sendIntent.setAction(ACTION_INSTALL_SHORTCUT);

		ShortcutStatusHolder holder = new ShortcutStatusHolder(context);
		holder.save(String.valueOf(System.currentTimeMillis()), sendIntent);
	}
}
