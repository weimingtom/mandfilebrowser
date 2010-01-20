package com.marcnuri.MAndFileBrowser;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
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
	private final static int MENU_ITEM_SELECT_ALL = 2;
	private final static int MENU_ITEM_SELECT_NONE = 3;
	private final static int MENU_ITEM_CREATE_DIRECTORY = 4;
	private final static int MENU_ITEM_RENAME_FILE = 5;
	private final static int MENU_ITEM_COPY = 6;
	private final static int MENU_ITEM_PASTE = 7;
	private final static int MENU_ITEM_DELETE = 8;
	private final static int MENU_ITEM_ABOUT = 9;
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
		menu.add(NONE, MENU_ITEM_SELECT_ALL, NONE, R.string.select_all);
		menu.add(NONE, MENU_ITEM_SELECT_NONE, NONE, R.string.select_none);
		menu.add(NONE, MENU_ITEM_CREATE_DIRECTORY, NONE,
				R.string.create_directory);
		menu.add(NONE, MENU_ITEM_RENAME_FILE, NONE, R.string.rename_file);
		menu.add(NONE, MENU_ITEM_COPY, NONE, R.string.copy);
		menu.add(NONE, MENU_ITEM_PASTE, NONE, R.string.paste);
		menu.add(NONE, MENU_ITEM_DELETE, NONE, R.string.delete);
		menu.add(NONE, MENU_ITEM_ABOUT, NONE, R.string.about);
		menu.add(NONE, MENU_ITEM_EXIT, NONE, R.string.exit);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (provider.canWrite) {
			menu.findItem(MENU_ITEM_CREATE_DIRECTORY).setVisible(true);
		} else {
			menu.findItem(MENU_ITEM_CREATE_DIRECTORY).setVisible(false);
		}
		if (provider.canRename) {
			menu.findItem(MENU_ITEM_RENAME_FILE).setVisible(true);
		} else {
			menu.findItem(MENU_ITEM_RENAME_FILE).setVisible(false);
		}
		if (provider.selectedFiles > 0) {
			menu.findItem(MENU_ITEM_COPY).setVisible(true);
			menu.findItem(MENU_ITEM_SELECT_NONE).setVisible(true);
		} else {
			menu.findItem(MENU_ITEM_COPY).setVisible(false);
			menu.findItem(MENU_ITEM_SELECT_NONE).setVisible(false);
		}
		if (provider.canPaste) {
			menu.findItem(MENU_ITEM_PASTE).setVisible(true);
		} else {
			menu.findItem(MENU_ITEM_PASTE).setVisible(false);
		}
		if (provider.canDelete) {
			menu.findItem(MENU_ITEM_DELETE).setVisible(true);
		} else {
			menu.findItem(MENU_ITEM_DELETE).setVisible(false);
		}
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_EXIT:
			quit();
			return true;
		case MENU_ITEM_ABOUT:
			about();
			return true;
		case MENU_ITEM_CREATE_DIRECTORY:
			createDirectory();
			return true;
		case MENU_ITEM_RENAME_FILE:
			renameFile();
			return true;
		case MENU_ITEM_COPY:
			copy();
			return true;
		case MENU_ITEM_PASTE:
			paste();
			return true;
		case MENU_ITEM_DELETE:
			delete();
			return true;
		case MENU_ITEM_SELECT_ALL:
			selectAll();
			return true;
		case MENU_ITEM_SELECT_NONE:
			selectNone();
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


	private void renameFile() {
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Rename");
		dialog.setMessage("New name");
		final EditText input = new EditText(this);
		File toRename = provider.selectedFile;
		if (toRename != null) {
			input.setText(toRename.getName());
		}
		dialog.setView(input);
		dialog.setPositiveButton("Ok", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					provider.rename(input.getText().toString());
				} catch (Exception e) {
					Toast toast = Toast.makeText(MAndFileBrowser.this, e
							.getMessage(), Toast.LENGTH_SHORT);
					toast.show();
				}
				provider.refresh();
			}
		});
		dialog.setNegativeButton("Cancel", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		dialog.show();

	}

	private void createDirectory() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("New Folder");
		dialog.setMessage("Folder name");
		final EditText input = new EditText(this);
		dialog.setView(input);
		dialog.setPositiveButton("Ok", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					provider.createDirectory(input.getText().toString());
				} catch (IOException e) {
					Toast toast = Toast.makeText(MAndFileBrowser.this, e
							.getMessage(), Toast.LENGTH_SHORT);
					toast.show();
				}
				provider.refresh();
			}
		});
		dialog.setNegativeButton("Cancel", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		dialog.show();

	}
	private void selectAll(){
		provider.selectAll();
	}
	private void selectNone(){
		provider.selectNone();
	}

	private void delete() {
		FileActionDialog dialog = new FileActionDialog(this, "Deleting...");
		provider.delete(dialog).execute();
	}

	private void copy() {
		provider.copy();
	}

	private void paste() {
		FileActionDialog dialog = new FileActionDialog(this, "Pasting...");
		provider.paste(dialog).execute();
	}
	private void about(){
		new InfoDialog(this, R.string.about, R.string.aboutContent).show();
	}
	private void quit() {
		finish();
	}
}