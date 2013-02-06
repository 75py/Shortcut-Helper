package com.nagopy.shortcut.helper.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * データベースの書き換えを行うためのクラス
 */
public class DatabaseAdapter {
	/**
	 * データベースのファイル名
	 */
	private static final String DATABASE_NAME = "shortcuts.db";

	/**
	 * データベースのバージョン
	 */
	private static final int DATABASE_VERSION = 1;

	/**
	 * デーブル名
	 */
	public static final String TABLE_NAME = "shortcuts";

	// CHECKSTYLE:OFF
	public static final String COL_ID = "_id";
	public static final String COL_LABEL_STRING = "label";
	public static final String COL_INTENT_STRING = "intent_string";
	public static final String COL_ICON_FILENAME = "icon_filename";

	@SuppressWarnings("unused")
	private final Context mContext;

	private DatabaseHelper mDatabaseHelper;
	private SQLiteDatabase mSQLiteDatabase;

	// CHECKSTYLE:ON

	/**
	 * コンストラクタ
	 * @param context
	 *           アプリケーションのコンストラクタ
	 */
	public DatabaseAdapter(Context context) {
		mContext = context;
		mDatabaseHelper = new DatabaseHelper(context);
	}

	/**
	 * データベースを管理するためのクラス
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		/**
		 * コンストラクタ
		 * @param context
		 *           アプリケーションのコンストラクタ
		 */
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " TEXT PRIMARY KEY," + COL_LABEL_STRING
					+ " TEXT NOT NULL," + COL_INTENT_STRING + " TEXT NOT NULL," + COL_ICON_FILENAME
					+ " TEXT NOT NULL);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}

	}

	/**
	 * データベースを開く
	 * @return this
	 */
	public DatabaseAdapter open() {
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
		return this;
	}

	/**
	 * データベースを閉じる
	 */
	public void close() {
		mDatabaseHelper.close();
	}

	// public boolean deleteAllShortcuts() {
	// return mSQLiteDatabase.delete(TABLE_NAME, null, null) > 0;
	// }

	/**
	 * IDを基に削除する
	 * @param id
	 *           ID
	 * @return 成功すればtrue
	 */
	public boolean deleteShortcut(String id) {
		return mSQLiteDatabase.delete(TABLE_NAME, COL_ID + "=" + id, null) > 0;
	}

	/**
	 * ラベル名の更新
	 * @param id
	 *           ID
	 * @param newLabel
	 *           新しいラベル名
	 * @return 書き換えが成功していればtrueを返す
	 */
	public boolean updateLabel(String id, String newLabel) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(COL_LABEL_STRING, newLabel);
		return mSQLiteDatabase.update(TABLE_NAME, contentValues, COL_ID + " = " + id, null) > 0;
	}

	/**
	 * Cursorを返して頑張る用
	 * @return cursor
	 */
	public Cursor getAllShortcuts() {
		return mSQLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
	}

	/**
	 * 保存したIDを基にアイコンのファイル名を検索して返す<br>
	 * @param id
	 * @return 見つからない場合はnullを返す
	 */
	// 作ったは良いけど使う必要なくなったメソッド
	// public String getIconFileName(String id) {
	// Cursor cursor = mSQLiteDatabase.query(TABLE_NAME, new String[] { COL_ID, COL_ICON_FILENAME },
	// COL_ID
	// + " = " + id, null, null, null, null);
	// if (!cursor.moveToFirst()) {
	// return null;
	// }
	// return cursor.getString(cursor.getColumnIndex(COL_ICON_FILENAME));
	// }

	/**
	 * ショートカットをうまいことデータベースに保存
	 * @param id
	 *           currentTimeMillisを文字列に変換して指定してね
	 * @param label
	 *           ラベル名
	 * @param intentString
	 *           intent.toUri(0)って感じで
	 * @param iconFileName
	 *           アイコンのファイル名
	 */
	public void saveShortcut(String id, String label, String intentString, String iconFileName) {
		ContentValues values = new ContentValues();
		values.put(COL_ID, id);
		values.put(COL_LABEL_STRING, label);
		values.put(COL_INTENT_STRING, intentString);
		values.put(COL_ICON_FILENAME, iconFileName);
		mSQLiteDatabase.insertOrThrow(TABLE_NAME, null, values);
	}
}
