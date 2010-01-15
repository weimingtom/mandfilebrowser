package com.marcnuri.MAndFileBrowser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.widget.TextView;

public class FileDataProvider {

	private Activity context;
	private File currentDirectory;
	private Comparator<File> comparator;
	private ArrayList<FileListAdapterEntry> list;
	private FileListAdapter listAdapter;

	/**
	 * @author Marc Nuri San Félix
	 * 
	 */
	public FileDataProvider(Activity context) {
		this.context = context;
		currentDirectory = new File("/");
		comparator = new FileComparator();
		list = new ArrayList<FileListAdapterEntry>();
		listAdapter = new FileListAdapter(context, R.layout.row, list);
	}

	public void root() {
		navigateTo(new File("/"));
	}

	public void up() {
		if (currentDirectory.getParentFile() != null) {
			navigateTo(currentDirectory.getParentFile());
		}
	}

	public void refresh() {
		navigateTo(currentDirectory);
	}

	public boolean canWrite() {
		return currentDirectory.canWrite();
	}
	
	public boolean canRename(){
		boolean ret = false;
		File f = getToRenameFile();
		if(f != null && f.canWrite()){
			ret = true;
		}
		return ret;
	}

	public File getToRenameFile(){
		int totalFiles = 0;
		File toRename = null;
		for(FileListAdapterEntry entry : list){
			if(entry.selected){
				toRename = entry.file;
				totalFiles++;
			}
		}
		if(totalFiles != 1){
			toRename = null;
		}
		return toRename;
	}
	public void selectFile(int position) {
		FileListAdapterEntry entry = listAdapter.getItem(position);
		if (entry.file != null) {
			entry.iconResource = null;
			entry.selected = !entry.selected;
			listAdapter.notifyDataSetChanged();
		}
	}

	public void createDirectory(String directoryName) throws IOException {
		if (!canWrite()) {
			throw new IOException("Permission denied!");
		}
		File newFile = new File(currentDirectory.getAbsolutePath() + "/"
				+ directoryName);
		newFile.mkdir();
	}

	public void rename(String newName) throws Exception{
		File toRename = getToRenameFile();
		if(toRename == null){
			throw new Exception("You have to select ONE file");
		}
		toRename.renameTo(new File(currentDirectory.getAbsolutePath()+"/"
				+ newName));
	}

	public void navigateTo(int position) {
		navigateTo(listAdapter.getItem(position).file);
	}

	private void navigateTo(File f) {
		if (f == null) {
			up();
		} else if (!f.isDirectory()) {
			// TODO: Code to open Files
			return;
		} else {
			list.clear();
			currentDirectory = f;
			String path = currentDirectory.getAbsolutePath();
			if (path.length() > 1) {
				path = path + "/";
			}
			((TextView) context.findViewById(R.id.textPath)).setText(path);
			// Put up icon
			if (!f.getAbsolutePath().equals("/")) {
				list.add(getFileListAdapterEntryForFile(null));
			}
			File[] files = f.listFiles();
			Arrays.sort(files, comparator);
			int length = files.length;
			for (int it = 0; it < length; it++) {
				list.add(getFileListAdapterEntryForFile(files[it]));
			}
			listAdapter.notifyDataSetChanged();
		}
	}

	private FileListAdapterEntry getFileListAdapterEntryForFile(File file) {
		FileListAdapterEntry ret = new FileListAdapterEntry(file, false, null);
		return ret;
	}

	public FileListAdapter getAdapter() {
		return listAdapter;
	}
}
