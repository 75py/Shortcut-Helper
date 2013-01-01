package com.nagopy.shortcut.helper;

import android.os.Bundle;
import android.view.View;

import com.nagopy.lib.base.BaseActivity;

public class StartActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_reset:
			clearSaveContents();
			break;
		}
	}

	/**
	 * 保存したインテントの削除を実行するメソッド
	 */
	private void clearSaveContents() {
		// SharedPreferencesをクリア
		if (!getSP().edit().clear().commit()) {
			// 保存できなかった場合
			showToast(getString(R.string.message_reset_error_clear_sharedpreferences));
			return;
		}

		// アイコンファイルの一覧を取得
		String[] filelist = this.fileList();
		for (String file : filelist) {
			if (!this.deleteFile(file)) {
				// ファイル削除に失敗したら
				showToast(getString(R.string.message_reset_error_delete_files));
				return;
			}
		}

		// SP、アイコン両方が削除できた場合
		showToast(getString(R.string.message_reset_complete));
	}
}
