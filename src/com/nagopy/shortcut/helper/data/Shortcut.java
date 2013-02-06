package com.nagopy.shortcut.helper.data;

import java.net.URISyntaxException;

import android.content.Intent;

/**
 * ショートカットの内容を保持するデータクラス
 */
public class Shortcut {
	/**
	 * ID
	 */
	private String id;

	/**
	 * ラベル名
	 */
	private String label;

	/**
	 * Intentを文字列に変換したもの
	 */
	private String intentString;

	/**
	 * アイコンのファイル名
	 */
	private String iconFileName;

	/**
	 * コンストラクタ
	 * @param id
	 *           ID
	 * @param label
	 *           ラベル名
	 * @param intentString
	 *           インテントを文字列に変換したやつ
	 * @param iconFileName
	 *           アイコンのファイル名
	 */
	public Shortcut(String id, String label, String intentString, String iconFileName) {
		setId(id);
		setLabel(label);
		setIntentString(intentString);
		setIconFileName(iconFileName);
	}

	/**
	 * ラベル名を取得
	 * @return ラベル名
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * ラベル名をセットする
	 * @param label
	 *           ラベル名
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * インテントの文字列形式のものを取得する
	 * @return インテントを文字列に変換したもの
	 */
	public String getIntentString() {
		return intentString;
	}

	/**
	 * 文字列に変換したインテントをセットする
	 * @param intentString
	 *           文字列に変換したインテント
	 */
	public void setIntentString(String intentString) {
		this.intentString = intentString;
	}

	/**
	 * インテントを保存。このメソッド内で文字列に変換する
	 * @param intent
	 *           保存したいインテント
	 */
	public void setIntent(Intent intent) {
		this.intentString = intent.toUri(0);
	}

	/**
	 * インテントを取得する
	 * @return ショートカットのインテント
	 */
	public Intent getIntent() {
		try {
			return Intent.parseUri(intentString, 0);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * アイコンのファイル名を取得する
	 * @return アイコンのファイル名
	 */
	public String getIconFileName() {
		return iconFileName;
	}

	/**
	 * アイコンのファイル名をセットする
	 * @param iconFileName
	 *           アイコンのファイル名
	 */
	public void setIconFileName(String iconFileName) {
		this.iconFileName = iconFileName;
	}

	/**
	 * IDを取得する
	 * @return ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * IDをセットする
	 * @param id
	 *           ID
	 */
	public void setId(String id) {
		this.id = id;
	}
}
