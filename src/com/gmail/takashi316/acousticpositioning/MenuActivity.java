package com.gmail.takashi316.acousticpositioning;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

public class MenuActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}// onCreateOptionsMenu

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itemAcousticPositioning) {
			Intent intent = new Intent();
			intent.setClass(this, SandboxActivity.class);
			startActivity(intent);
			return true;
		}
		if (item.getItemId() == R.id.itemSpreadsheet) {
			Intent intent = new Intent();
			intent.setClass(this, SpreadsheetActivity.class);
			startActivity(intent);
			return true;
		}
		return false;
	}// onOptionsItemSelected
}// MenuActivity
