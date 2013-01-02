package com.nagopy.shortcut.helper.dialog;

import java.io.Serializable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

import com.nagopy.shortcut.helper.R;

public class EditTextDialogFragment extends DialogFragment {

	/**
	 * リスナーを保存するためのキー
	 */
	public static final String KEY_ON_CLICK_LISTENER = "KEY_ON_CLICK_LISTENER";

	/**
	 * 入力欄にセットしておく文字列を保存するためのキー
	 */
	public static final String KEY_TEXT = "KEY_TEXT";

	/**
	 * ダイアログのタイトルを保存するためのキー
	 */
	private static final String KEY_DIALOG_TITLE = "KEY_DIALOG_TITLE";

	private View mRootView;
	private EditText mEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mRootView = View.inflate(getActivity(), R.layout.edittext_dialog, null);
		mEditText = (EditText) mRootView.findViewById(R.id.edittext_dialog_editText);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mEditText.setText(getArguments().getString(KEY_TEXT));
		return new AlertDialog.Builder(getActivity()).setView(mRootView).setTitle(getDialogTitle())
				.setPositiveButton(android.R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						getListener().onClickPositiveButton(getInputText());
					}
				}).setNegativeButton(android.R.string.cancel, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						getListener().onClickNegativeButton(getInputText());
					}
				}).create();
	}

	/**
	 * 入力欄の文字列を取得
	 * @return
	 */
	private String getInputText() {
		return mEditText.getText().toString();
	}

	/**
	 * リスナーを保存する
	 * @param listener
	 */
	public void init(String dialogTitle, String text, OnEditTextDialogListener listener) {
		Bundle bundle = new Bundle();
		bundle.putString(KEY_DIALOG_TITLE, dialogTitle);
		bundle.putSerializable(KEY_ON_CLICK_LISTENER, listener);
		bundle.putString(KEY_TEXT, text);
		setArguments(bundle);
	}

	/**
	 * 保存しておいたリスナーを取得
	 */
	private OnEditTextDialogListener getListener() {
		return (OnEditTextDialogListener) getArguments().get(KEY_ON_CLICK_LISTENER);
	}

	private String getDialogTitle() {
		return getArguments().getString(KEY_DIALOG_TITLE);
	}

	@SuppressWarnings("serial")
	public static abstract class OnEditTextDialogListener implements Serializable {
		/**
		 * OKボタンを押したとき
		 * @param text
		 *           入力中の文字列
		 */
		public abstract void onClickPositiveButton(String text);

		/**
		 * キャンセルボタンを押したとき
		 * @param text
		 *           入力中の文字列
		 */
		public abstract void onClickNegativeButton(String text);
	}
}
