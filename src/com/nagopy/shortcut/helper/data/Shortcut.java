package com.nagopy.shortcut.helper.data;

import java.net.URISyntaxException;

import android.content.Intent;

public class Shortcut {
	private String id;
	private String label;
	private String intentString;
	private String iconFileName;

	public Shortcut(String id, String label, String intentString, String iconFileName) {
		setId(id);
		setLabel(label);
		setIntentString(intentString);
		setIconFileName(iconFileName);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getIntentString() {
		return intentString;
	}

	public void setIntentString(String intentString) {
		this.intentString = intentString;
	}

	public void setIntent(Intent intent) {
		this.intentString = intent.toUri(0);
	}

	public Intent getIntent() {
		try {
			return Intent.parseUri(intentString, 0);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getIconFileName() {
		return iconFileName;
	}

	public void setIconFileName(String iconFileName) {
		this.iconFileName = iconFileName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
