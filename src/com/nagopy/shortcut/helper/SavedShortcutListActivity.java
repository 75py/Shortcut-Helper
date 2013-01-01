package com.nagopy.shortcut.helper;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nagopy.lib.base.BaseListActivity;
import com.nagopy.shortcut.helper.util.ShortcutStatusHolder;

public class SavedShortcutListActivity extends BaseListActivity {

	private String[] keys;
	private SharedPreferences mSharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shortcut_list);

		mSharedPreferences = getSP();

		ShortcutStatusHolder holder = new ShortcutStatusHolder(getApplicationContext());
		keys = holder.getKeys();
		if (keys == null) {
			showToast(getString(R.string.message_empty));
			finish();
			return;
		}
		List<String> listStrings = new ArrayList<String>();
		for (int i = 0; i < keys.length; i++) {
			listStrings.add(mSharedPreferences.getString(keys[i], "null"));
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
				listStrings);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		String key = keys[position];

		ShortcutStatusHolder holder = new ShortcutStatusHolder(getApplicationContext());
		setResult(RESULT_OK, holder.restoreShortcutIntent(key));
		finish();
	}
}
