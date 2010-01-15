package com.marcnuri.MAndFileBrowser;

import java.util.Currency;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * @author Marc Nuri San Félix
 * 
 */
public class MAndFileBrowser extends Activity {
	private final static int MENU_ITEM_EXIT = 1;
	private final static int MENU_CREATE_DIRECTORY = 2;
	private FileDataProvider provider;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Dialog);
		setContentView(R.layout.main);
		initComponents();
	}

	private void initComponents() {
		provider = new FileDataProvider(this);
		((FileListView) findViewById(R.id.fileListView)).setAdapter(provider
				.getAdapter());
		((FileListView) findViewById(R.id.fileListView))
				.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> adapterView,
							View view, int position, long id) {
						provider.navigateTo(position);
					}
				});
		((FileListView) findViewById(R.id.fileListView))
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					public boolean onItemLongClick(AdapterView<?> adapterView,
							View view, int position, long id) {
						provider.selectFile(position);
						return true;
					}
				});
		provider.root();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int NONE = Menu.NONE;
		menu.add(NONE, MENU_CREATE_DIRECTORY, NONE, R.string.create_directory);
		menu.add(NONE, MENU_ITEM_EXIT, NONE, R.string.exit);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_EXIT:
			quit();
			return true;
		case MENU_CREATE_DIRECTORY:
			createDirectory();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			provider.up();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void createDirectory() {
		if (provider.canWrite()) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("New Folder");
			dialog.setMessage("Folder name");
			final EditText input = new EditText(this);
			dialog.setView(input);
			dialog.setPositiveButton("Ok", new OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					provider.createDirectory(input.getText().toString());
					provider.refresh();
				}
			});
			dialog.setNegativeButton("Cancel", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			dialog.show();
		} else {
			Toast toast = Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT);
			toast.show();
		}

	}

	private void quit() {
		finish();
	}
}