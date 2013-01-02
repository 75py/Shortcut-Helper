package com.nagopy.shortcut.helper.data;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.Log;

import com.nagopy.shortcut.helper.InstallShortcutReceiver;

public class ShortcutStatusHolder {

	private Context mContext;

	public ShortcutStatusHolder(Context context) {
		mContext = context;
	}

	/**
	 * ショートカットを保存するメソッド
	 * @param data
	 *           保存するintent
	 */
	public void save(Intent data) {
		String saveId = String.valueOf(System.currentTimeMillis());
		String iconFileNaem = "icon_" + String.valueOf(saveId);

		// データベースの更新
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(mContext);
		databaseAdapter.open();
		databaseAdapter.saveShortcut(saveId, data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME),
				((Intent) data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT)).toUri(0), iconFileNaem);
		databaseAdapter.close();

		Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
		if (extra != null && extra instanceof ShortcutIconResource) {
			try {
				ShortcutIconResource iconResource = (ShortcutIconResource) extra;
				final PackageManager packageManager = mContext.getPackageManager();
				Resources resources = packageManager.getResourcesForApplication(iconResource.packageName);
				final int id = resources.getIdentifier(iconResource.resourceName, null, null);
				Drawable icon = resources.getDrawable(id);
				saveBitmap(((BitmapDrawable) icon).getBitmap(), iconFileNaem);
			} catch (Exception e) {
				Log.w("debug", "Could not load shortcut icon: " + extra);
			}
		} else {
			Parcelable extra2 = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
			if (extra2 != null && extra2 instanceof Bitmap) {
				saveBitmap((Bitmap) extra2, iconFileNaem);
			}
		}
	}

	/**
	 * ショートカットのintentを復元する
	 * @param key
	 *           保存に使ったキー
	 * @return setResultするだけのintent
	 */
	public Intent restoreShortcutIntent(Shortcut shortcut) {
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcut.getLabel());
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut.getIntent());
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, restoreIcon(shortcut.getIconFileName()));
		intent.setAction(InstallShortcutReceiver.ACTION_INSTALL_SHORTCUT);
		return intent;
	}

	/**
	 * Bitmapの保存
	 * @param bitmap
	 *           保存するbitmap
	 * @param filename
	 *           ファイル名
	 */
	private void saveBitmap(Bitmap bitmap, String filename) {
		try {
			OutputStream out = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存したアイコンの復元
	 */
	public Bitmap restoreIcon(String filename) {
		Bitmap bitmap = null;
		try {
			FileInputStream in = mContext.openFileInput(filename);
			BufferedInputStream binput = new BufferedInputStream(in);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] w = new byte[1024];
			while (binput.read(w) >= 0) {
				out.write(w, 0, 1024);
			}
			byte[] byteData = out.toByteArray();
			bitmap = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
			in.close();
			binput.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (bitmap == null) {
			return null;
		}
		return bitmap;
	}
}
