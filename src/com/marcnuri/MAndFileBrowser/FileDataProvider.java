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
import java.util.LinkedHashMap;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.database.DataSetObserver;
import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;

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
	public void selectAll(){
		setSelect(true);
	}
	public void selectNone(){
		setSelect(false);
	}
	private void setSelect(boolean selected){
		for(FileListAdapterEntry entry : list){
			if (entry.file != null) {
				entry.iconResource = null;
				entry.selected = selected;
			}
		}
		listAdapter.notifyDataSetChanged();
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


	public FileWorker delete(FileActionDialog progressDialog) {
		FileWorker ret = null;
		if (canDelete) {
			ret = new FileWorker(progressDialog) {

				@Override
				protected void done(Exception exception) {
					if (exception != null) {
						exception.printStackTrace();
						Toast toast = Toast.makeText(context,
								"Error when deleting", Toast.LENGTH_SHORT);
						toast.show();
					}
					refresh();

				}

				@Override
				protected void doInBackGround() throws Exception {
					int absoluteProgress = 0;
					int absoluteMax = selectedFiles;
					;
					/*
					 * In order to prevent a stack overflow its better to create
					 * a list of pointers to files and then delete them.
					 */
					ArrayList<File> toDeleteCollection = new ArrayList<File>();
					for (FileListAdapterEntry entry : list) {
						if (actionCancelled) {
							break;
						}
						if (entry.selected) {
							toDeleteCollection.clear();
							StringBuilder absoluteMessage = new StringBuilder(
									"Deleting selected ");
							absoluteMessage.append(absoluteProgress+1);
							absoluteMessage.append(" of ");
							absoluteMessage.append(absoluteMax);
							publish("Caching files", 0, 1, absoluteMessage.toString(),
									absoluteProgress, absoluteMax);
							absoluteProgress++;
							gather(entry.file, toDeleteCollection, this);
							
							int partialProgress = 0;
							int partialMax = toDeleteCollection.size();
							for (File deleteFile : toDeleteCollection) {
								if (actionCancelled) {
									break;
								}
								partialProgress++;
								String name = deleteFile.getName();
								StringBuilder partialMessage = new StringBuilder(
										"Deleting ");
								partialMessage.append(name);
								publish(partialMessage.toString(),
										partialProgress, partialMax,
										null,absoluteProgress, absoluteMax);
								deleteFile.delete();
							}
						}
					}
				}
			};
		}
		return ret;
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

	public FileWorker paste(FileActionDialog progressDialog) {
		FileWorker ret = null;
		if (canPaste) {
			ret = new FileWorker(progressDialog) {

				@Override
				protected void done(Exception exception) {
					if (exception != null) {
						exception.printStackTrace();
						Toast toast = Toast.makeText(context,
								"Error when pasting", Toast.LENGTH_SHORT);
						toast.show();
					}
					if (actionCancelled) {
						Toast toast = Toast
								.makeText(context, "Pasting cancelled by user",
										Toast.LENGTH_SHORT);
						toast.show();
					}
					refresh();
				}

				@Override
				protected void doInBackGround() throws Exception {
					int absoluteProgress = 0;
					int absoluteMax = listClipBoard.size();
					/*
					 * In order to prevent a stack overflow its better to create
					 * a list of pointers to files and then delete them.
					 */
					LinkedHashMap<File, File> toPasteCollection = new LinkedHashMap<File, File>();
					for (File clipboardOrigin : listClipBoard) {
						if (actionCancelled) {
							break;
						}
						toPasteCollection.clear();
						StringBuilder absoluteMessage = new StringBuilder(
								"Pasting clipboard ");
						absoluteMessage.append(absoluteProgress+1);
						absoluteMessage.append(" of ");
						absoluteMessage.append(absoluteMax);
						publish("Caching files", 0, 1,
								absoluteMessage.toString(),
								absoluteProgress, absoluteMax);
						absoluteProgress++;
						// APPEND -copy if exists
						String clipboardOriginName = clipboardOrigin.getName();
						File clipBoardDestination = new File(currentDirectory
								.getAbsolutePath()
								+ "/" + clipboardOriginName);
						while (clipBoardDestination.exists()) {
							clipBoardDestination = new File(
									clipBoardDestination.getAbsolutePath()
											+ "-copy");
						}
						gather(clipboardOrigin, clipBoardDestination,
								toPasteCollection, this);
						// Created pointer for current clipboard file
						// Begin paste
						int partialProgress = 0;
						int partialMax = toPasteCollection.size();
						for (File keyFile : toPasteCollection.keySet()) {
							if (actionCancelled) {
								break;
							}
							partialProgress++;
							File destination = toPasteCollection.get(keyFile);
							String name = destination.getName();
							StringBuilder partialMessage = new StringBuilder(
									"Pasting ");
							partialMessage.append(name);
							publish(partialMessage.toString(), partialProgress,
									partialMax,
									null,absoluteProgress, absoluteMax);
							copy(keyFile, destination);
						}
					}
				}
			};
		}
		return ret;
	}

	private void copy(File source, File destination) throws IOException,
			FileNotFoundException {
		if (source.isDirectory()) {
			destination.mkdirs();
		} else {
			FileChannel in = new FileInputStream(source).getChannel();
			destination.createNewFile();
			FileChannel out = new FileOutputStream(destination).getChannel();
			if (source.length() != 0l) {
				// IF File is of size 0, this throws
				// Exception
				// http://issues.apache.org/jira/browse/HARMONY-6315
				MappedByteBuffer buf = in.map(MapMode.READ_ONLY, 0, in.size());
				out.write(buf);
			}
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}

	private void gather(File source, File destination,
			LinkedHashMap<File, File> cached, FileWorker worker) {
		if (worker.actionCancelled) {
			return;
		}
		cached.put(source, destination);
		if (source.isDirectory()) {
			for (String child : source.list()) {
				gather(new File(source, child), new File(destination,
						child), cached, worker);
			}

		}
	}
	private void gather(File source, ArrayList<File> cached, FileWorker worker){
		if(worker.actionCancelled){
			return;
		}
		if(source.isDirectory()){
			for(File child : source.listFiles()){
				gather(child,cached, worker);
			}
		}
		//IMPORTANT TO ADD IT AFTER ITS CHILDREN.
		//TO DELETE MUST DELETE CHILDREN FIRST.
		cached.add(source);
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
			if (f.getName().endsWith(".apk")) {
				PackageManager pm = context.getPackageManager();
				PermissionInfo info = new PermissionInfo();
				pm.addPermission(info);
				pm.installPackage(Uri.fromFile(f));
			} else {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(f));
				try {
					context.startActivity(intent);

				} catch (ActivityNotFoundException ex) {
				}
			}
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
