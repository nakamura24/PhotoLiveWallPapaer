/*
 * Copyright (C) 2012 M.Nakamura
 *
 * This software is licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 2.1 Japan License.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 		http://creativecommons.org/licenses/by-nc-sa/2.1/jp/legalcode
 */

package jp.livewallpapaer.photo;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class LiveWallPaperPreference extends PreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {
	private static String Tag = "LiveWallPaperPreference";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);

		// 設定が変更された時に呼び出されるListenerを登録
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(sharedPreferences, null);
		getPath();
	}

	ArrayList<String> entries = new ArrayList<String>();
	ArrayList<String> entryValues = new ArrayList<String>();

	private void getPath() {
		ListPreference path = (ListPreference) getPreferenceScreen()
				.findPreference("path");

		// SDカードのFileを取得
		File file = Environment.getExternalStorageDirectory();
		getSubDir(file.getAbsolutePath());

		path.setEntries((String[]) entries.toArray(new String[0]));
		path.setEntryValues((String[]) entryValues.toArray(new String[0]));
	}

	private boolean image_enable = false;

	private void getSubDir(String subdir) {
		Log.i(Tag, "getSubDir - " + subdir);
		try {
			File subDir = new File(subdir);
			String subFileNames[] = subDir.list();
			for (String subFileName : subFileNames) {
				File subFile = new File(subDir + "/" + subFileName);
				if (subFile.isDirectory()) {
					image_enable = false;
					getSubDir(subDir + "/" + subFileName);
					if (image_enable) {
						entries.add(subFileName);
						entryValues.add(subDir + "/" + subFileName);
					}
				} else if (subFile.getName().toLowerCase(Locale.getDefault())
						.endsWith("jpg")
						|| subFile.getName().toLowerCase(Locale.getDefault())
								.endsWith("png")) {
					if (!subDir.getAbsolutePath().contains("/.")) {
						image_enable = true;
					}
				}
			}
		} catch (Exception e) {
			Log.e(Tag, "getSubDir = " + e.getMessage());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		ListPreference path = (ListPreference) getPreferenceScreen()
				.findPreference("path");
		path.setSummary(path.getEntry());
	}
}
