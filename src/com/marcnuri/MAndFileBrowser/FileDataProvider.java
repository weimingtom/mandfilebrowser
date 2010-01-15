package com.marcnuri.MAndFileBrowser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.database.DataSetObserver;
import android.widget.TextView;

/**
 * @author Marc Nuri San Félix
 * 
 */
public class FileDataProvider {

	private Activity context;
	private File currentDirectory;
	private Comparator<File> comparator;
	private ArrayList<FileListAdapterEntry> list;
	private FileListAdapter listAdapter;
	private ArrayList<File> listClipBoard;
	public boolean canRename;
	public boolean canWrite;
	public boolean canPaste;
	public boolean canDelete;
	public int selectedFiles;
	public File selectedFile;

	/**
	 * @author Marc Nuri San Félix
	 * 
	 */
	public FileDataProvider(Activity context) {
		this.context = context;
		currentDirectory = new File("/");
		canRename = false;
		canWrite = false;
		canPaste = false;
		canDelete = false;
		selectedFiles = 0;
		selectedFile = null;
		comparator = new FileComparator();
		list = new ArrayList<FileListAdapterEntry>();
		listAdapter = new FileListAdapter(context, R.layout.row, list);
		listClipBoard = new ArrayList<File>();

		listAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				updateProperties();
			}
		});
	}

	public FileListAdapter getAdapter() {
		return listAdapter;
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

	public void selectFile(int position) {
		FileListAdapterEntry entry = listAdapter.getItem(position);
		if (entry.file != null) {
			entry.iconResource = null;
			entry.selected = !entry.selected;
			listAdapter.notifyDataSetChanged();
		}
	}

	public void createDirectory(String directoryName) throws IOException {
		if (!canWrite) {
			throw new IOException("Permission denied!");
		}
		File newFile = new File(currentDirectory.getAbsolutePath() + "/"
				+ directoryName);
		newFile.mkdir();
	}

	public void rename(String newName) throws Exception {
		if (!canRename) {
			throw new IOException("Can't rename!");
		}
		if (selectedFile == null) {
			throw new Exception("You have to select ONE file");
		}
		selectedFile.renameTo(new File(currentDirectory.getAbsolutePath() + "/"
				+ newName));
	}

	public void delete() {
		if (canDelete) {
			for (FileListAdapterEntry entry : list) {
				if (entry.selected) {
					delete(entry.file);
				}
			}
		}
	}

	private void delete(File file) {
		if (file.isDirectory()) {
			for (File temp : file.listFiles()) {
				delete(temp);
			}
			file.delete();
		} else {
			file.delete();
		}
	}

	public void copy() {
		listClipBoard.clear();
		// Performance, only traverse if necessary.
		if (selectedFiles > 0) {
			for (FileListAdapterEntry entry : list) {
				if (entry.selected) {
					listClipBoard.add(entry.file);
				}
			}
			listAdapter.notifyDataSetChanged();
		}
	}

	public void paste() throws IOException {
		if (canPaste) {
			for (File origin : listClipBoard) {
				File destination = new File(currentDirectory.getAbsolutePath()
						+ "/" + origin.getName());
				while (destination.exists()) {
					destination = new File(destination.getAbsolutePath()
							+ "-copy");
				}
				copy(origin, destination);
			}
		}
	}

	private void copy(File source, File destination) throws IOException {
		if (source.isDirectory()) {
			destination.mkdir();
			for (String children : source.list()) {
				copy(new File(source, children),
						new File(destination, children));
			}

		} else {
			if (source.canRead()) {
				try {
					FileChannel in = new FileInputStream(source).getChannel();
					FileChannel out = new FileOutputStream(destination)
							.getChannel();
					try {
						if (source.length() == 0l) {
							destination.createNewFile();
						} else {
							// IF File is of size 0, this throws Exception
							// http://issues.apache.org/jira/browse/HARMONY-6315
							MappedByteBuffer buf = in.map(MapMode.READ_ONLY, 0,
									in.size());
							out.write(buf);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (in != null)
						in.close();
					if (out != null)
						out.close();
				} catch (FileNotFoundException e) {
				}
			}
		}

	}

	public void navigateTo(int position) {
		navigateTo(listAdapter.getItem(position).file);
	}

	private void updateProperties() {
		canRename = false;
		canWrite = currentDirectory.canWrite();
		canPaste = false;
		canDelete = false;
		selectedFiles = 0;
		selectedFile = null;
		for (FileListAdapterEntry entry : list) {
			if (entry.selected) {
				selectedFiles++;
				selectedFile = entry.file;
				if (selectedFile.canWrite()) {
					canDelete = true;
				} else {
					canDelete = false;
				}
			}
		}
		if (selectedFiles != 1) {
			selectedFile = null;
		} else {
			/*
			 * int perm = context.checkCallingUriPermission(Uri
			 * .fromFile(selectedFile), Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
			 * if (perm == PackageManager.PERMISSION_GRANTED) { canRename =
			 * true; }
			 */
			if (selectedFile.canWrite()) {
				canRename = true;
			}
		}
		if (listClipBoard.size() > 0) {
			if (canWrite) {
				canPaste = true;
			}
		}
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

}
