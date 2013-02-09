package com.nagopy.shortcut.helper;

import java.util.ArrayList;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nagopy.lib.base.BaseActivity;
import com.nagopy.lib.image.ImageUtils;
import com.nagopy.shortcut.helper.data.DatabaseAdapter;
import com.nagopy.shortcut.helper.data.Shortcut;
import com.nagopy.shortcut.helper.data.ShortcutStatusHolder;
import com.nagopy.shortcut.helper.dialog.EditTextDialogFragment;
import com.nagopy.shortcut.helper.dialog.EditTextDialogFragment.OnEditTextDialogListener;
import com.nagopy.shortcut.helper.dialog.ListDialogFragment;
import com.nagopy.shortcut.helper.dialog.ListDialogFragment.OnListDialogItemClickListener;

/**
 * 保存したショートカットの一覧を表示するアクティビティ
 */
public class SavedShortcutListActivity extends BaseActivity {

	private DatabaseAdapter mDatabaseAdapter;
	private ShortcutListAdapter listAdapter;
	private ArrayList<Shortcut> shortcutList = new ArrayList<Shortcut>();
	private ListView mListView;

	@SuppressWarnings("serial")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDatabaseAdapter = new DatabaseAdapter(getApplicationContext());
		listAdapter = new ShortcutListAdapter();
		loadDatabase();

		if (listAdapter.getCount() < 1) {
			// インテントが保存されていなければアプリを終了する
			showToast(getString(R.string.message_empty));
			finish();
			return;
		}

		setContentView(R.layout.shortcut_list);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(listAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Shortcut shortcut = (Shortcut) adapter.getItemAtPosition(position);
				ShortcutStatusHolder holder = new ShortcutStatusHolder(getApplicationContext());
				if (Intent.ACTION_CREATE_SHORTCUT.equals(getIntent().getAction())) {
					setResult(RESULT_OK, holder.restoreShortcutIntent(shortcut));
				} else {
					try {
						startActivity(shortcut.getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
					} catch (ActivityNotFoundException e) {
						// アプリが削除された場合など
						showToast(getString(R.string.message_error_activity_not_found));
					}
				}
				finish();
			}
		});

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, final View longClickedView, int position,
					long id) {
				final Shortcut shortcut = (Shortcut) adapter.getItemAtPosition(position);

				final ListDialogFragment listDialogFragment = new ListDialogFragment();
				listDialogFragment.init(getString(R.string.dialog_title_longclick_list), R.array.list_longclick,
						new OnListDialogItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
								switch (position) {
								case 0:
									EditTextDialogFragment editTextDialogFragment = new EditTextDialogFragment();
									String label = ((TextView) longClickedView.findViewById(R.id.list_textview_label))
											.getText().toString();
									editTextDialogFragment.init(getString(R.string.dialog_title_edit_label), label,
											new OnEditTextDialogListener() {
												@Override
												public void onClickPositiveButton(String text) {
													mDatabaseAdapter.open();
													if (mDatabaseAdapter.updateLabel(shortcut.getId(), text)) {
														loadDatabase();
													} else {
														// 書き換えに失敗した場合
														showToast(getString(R.string.message_error_rewrite_database));
													}
													mDatabaseAdapter.close();
												}

												@Override
												public void onClickNegativeButton(String text) {}
											});
									editTextDialogFragment.show(getSupportFragmentManager(), "");
									break;
								case 1:
									mDatabaseAdapter.open();

									// データベースの更新
									if (mDatabaseAdapter.deleteShortcut(shortcut.getId())) {
										loadDatabase();
									} else {
										// 書き換えに失敗した場合
										showToast(getString(R.string.message_error_rewrite_database));
									}
									mDatabaseAdapter.close();

									// アイコンファイルの削除
									if (!deleteFile(shortcut.getIconFileName())) {
										showToast(getString(R.string.message_error_delete_icon_file));
									}
									break;
								}
								listDialogFragment.dismiss();
							}
						});
				listDialogFragment.show(getSupportFragmentManager(), "");
				return true;
			}
		});
	}

	private void loadDatabase() {
		shortcutList.clear();

		mDatabaseAdapter.open();
		Cursor c = mDatabaseAdapter.getAllShortcuts();

		if (c.moveToFirst()) {
			do {
				Shortcut shortcut = new Shortcut(c.getString(c.getColumnIndex(DatabaseAdapter.COL_ID)),
						c.getString(c.getColumnIndex(DatabaseAdapter.COL_LABEL_STRING)), c.getString(c
								.getColumnIndex(DatabaseAdapter.COL_INTENT_STRING)), c.getString(c
								.getColumnIndex(DatabaseAdapter.COL_ICON_FILENAME)));
				shortcutList.add(shortcut);
			} while (c.moveToNext());
		}

		mDatabaseAdapter.close();

		listAdapter.notifyDataSetChanged();
	}

	private class ShortcutListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return shortcutList.size();
		}

		@Override
		public Object getItem(int position) {
			return shortcutList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.database_list_row, null);
				holder = new ViewHolder();
				holder.labelTextView = (TextView) convertView.findViewById(R.id.list_textview_label);
				holder.intentTextView = (TextView) convertView.findViewById(R.id.list_textview_intent);
				convertView.setTag(R.string.app_name, holder);
			} else {
				holder = (ViewHolder) convertView.getTag(R.string.app_name);
			}

			Shortcut shortcut = (Shortcut) getItem(position);
			if (shortcut != null) {
				holder.labelTextView.setText(shortcut.getLabel());
				holder.intentTextView.setText(shortcut.getIntentString());
				convertView.setTag(shortcut.getId());

				ShortcutStatusHolder shortcutHolder = new ShortcutStatusHolder(getApplicationContext());

				Drawable icon = ImageUtils.getDrawable(getResources(),
						shortcutHolder.restoreIcon(shortcut.getIconFileName()));

				holder.labelTextView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
				icon.setCallback(null);
			}
			return convertView;
		}

	}

	/**
	 * ビューホルダー
	 */
	static class ViewHolder {
		/**
		 * ラベル名を表示するテキストビュー
		 */
		TextView labelTextView;

		/**
		 * インテントの中身を表示するテキストビュー
		 */
		TextView intentTextView;
	}
}
