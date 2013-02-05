package com.nagopy.shortcut.helper.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nagopy.lib.image.ResourceDrawables;
import com.nagopy.shortcut.helper.InstallShortcutReceiver;

public class ShortcutStatusHolder {

	private SharedPreferences mSharedPreferences;
	private Context mContext;

	private static final String ICON = "_icon";
	private static final String INTENT = "_intent";
	private static final String KEYS_SEPARATOR = "<>";

	public ShortcutStatusHolder(Context context) {
		mContext = context;
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * ショートカットを保存するメソッド
	 * @param key
	 *           保存に使うキー。被らない何か（時間とか）が良い
	 * @param data
	 *           保存するintent
	 */
	public void save(String key, Intent data) {
		Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
		mSharedPreferences.edit().putString(createIntentKey(key), intent.toUri(0)).commit();

		String name = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
		// mSharedPreferences.edit().putString(key + "_label", name).commit();
		mSharedPreferences.edit().putString(key, name).commit();

		Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
		if (extra != null && extra instanceof ShortcutIconResource) {
			try {
				ShortcutIconResource iconResource = (ShortcutIconResource) extra;
				final PackageManager packageManager = mContext.getPackageManager();
				Resources resources = packageManager.getResourcesForApplication(iconResource.packageName);
				final int id = resources.getIdentifier(iconResource.resourceName, null, null);

				ResourceDrawables resourceDrawables = ResourceDrawables.getInstance(mContext, resources);
				Drawable icon = resourceDrawables.getDrawable(id);

				saveBitmap(((BitmapDrawable) icon).getBitmap(), createIconKey(key));
			} catch (Exception e) {
				Log.w("debug", "Could not load shortcut icon: " + extra);
			}
		} else {
			Parcelable extra2 = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
			if (extra2 != null && extra2 instanceof Bitmap) {
				saveBitmap((Bitmap) extra2, createIconKey(key));
			}
		}

		saveKey(key);
	}

	/**
	 * 保存済みキー一覧にキーを追加する
	 */
	private void saveKey(String key) {
		String keys = mSharedPreferences.getString("keys", "");
		// log("keys:" + keys);
		keys = keys + key + KEYS_SEPARATOR;
		mSharedPreferences.edit().putString("keys", keys).commit();
		// log("savedkeys:" + keys);
	}

	/**
	 * 保存してあるキーの一覧を配列で取得する
	 */
	public String[] getKeys() {
		String savedKeysString = mSharedPreferences.getString("keys", null);
		if (savedKeysString == null) {
			return null;
		}
		return savedKeysString.split(KEYS_SEPARATOR);
	}

	/**
	 * ショートカットのintentを復元する
	 * @param key
	 *           保存に使ったキー
	 * @return setResultするだけのintent
	 */
	public Intent restoreShortcutIntent(String key) {
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, restoreLabel(key));
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, restoreIntent(key));
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, restoreIcon(key));
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
	 * @param key
	 *           保存に使ったキー
	 */
	private Bitmap restoreIcon(String key) {
		String filename = createIconKey(key);
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

	/**
	 * 保存したショートカットのラベルを取得する
	 * @param key
	 *           保存に使ったキー
	 */
	private CharSequence restoreLabel(String key) {
		return mSharedPreferences.getString(key, null);
	}

	/**
	 * 保存したショートカットのintentを取得する
	 * @param key
	 *           保存に使ったキー
	 */
	private Intent restoreIntent(String key) {
		String targetKey = createIntentKey(key);
		String uri = mSharedPreferences.getString(targetKey, null);
		if (uri != null) {
			try {
				// Intent i = Intent.getIntent(uri).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Intent i = Intent.parseUri(uri, 0);
				return i;
			} catch (ActivityNotFoundException e) {
				// showToast("Activity not found");
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// showToast("ERROR: URISyntaxException");
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * デバッグ用ログ出力メソッド
	 * @param object
	 *           何でもおｋ。nullでもおｋ
	 */
	@SuppressWarnings("unused")
	private void log(Object object) {
		if (object == null) {
			object = "null";
		}
		Log.d("debug", object.toString());
	}

	/**
	 * ラベルのキーを元にインテント保存用のキーを作成
	 */
	private String createIntentKey(String baseKey) {
		return baseKey + INTENT;
	}

	/**
	 * ラベルのキーを元にアイコン保存用のキーを作成
	 */
	private String createIconKey(String baseKey) {
		return baseKey + ICON;
	}
}
