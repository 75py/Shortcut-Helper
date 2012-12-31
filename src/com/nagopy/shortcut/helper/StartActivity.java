package com.nagopy.shortcut.helper;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.preference.*;

public class StartActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
	}

	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.button_reset:
				// SharedPreferencesをクリア
				PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
					.edit().clear().commit();


				// ファイル削除
				String[] filelist = this.fileList();
				for (String file : filelist)
				{
					this.deleteFile(file);
				}

				Toast.makeText(getApplicationContext(), "reset", Toast.LENGTH_LONG).show();
				break;
		}
	}
}
