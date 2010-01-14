package com.marcnuri.MAndFileBrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.widget.TextView;

public class FileDataProvider {

	
	private Activity context;
	private File currentDirectory;
	private Comparator<File> comparator;
	private ArrayList<FileListAdapterEntry> list;
	private FileListAdapter listAdapter;
	
	private HashMap<String, Integer> mimeTypes;
	
	/**
	 * @author Marc Nuri San F�lix
	 *
	 */
	public FileDataProvider(Activity context) {
		this.context = context;
		currentDirectory = new File("/");
		comparator = new FileComparator();
		list = new ArrayList<FileListAdapterEntry>();
		listAdapter = new FileListAdapter(context, R.layout.row,list);

		//PERFORMANCE GAIN WHEN RETREIVING ICONS
		initMimeTypes();
		
	}
	
	private void initMimeTypes(){
		mimeTypes = new HashMap<String, Integer>();
		Resources resources = context.getResources();
		for(String extension : resources.getStringArray(R.array.audio)){
			mimeTypes.put(extension, R.drawable.iconaudio);
		}
		for(String extension : resources.getStringArray(R.array.image)){
			mimeTypes.put(extension, R.drawable.iconimage);
		}
	}
	
	public void root(){
		navigateTo(new File("/"));
	}
	public void up(){
		if(currentDirectory.getParentFile() != null){
			navigateTo(currentDirectory.getParentFile());
		}
	}
	public void selectFile(int position){
		listAdapter.getItem(position).selected = true;
		listAdapter.notifyDataSetChanged();
	}
	public void navigateTo(int position){
		navigateTo(listAdapter.getItem(position).file);
	}
	private void navigateTo(File f){
		if(f == null){
			up();
		}
		else if(!f.isDirectory()){
			//TODO: Code to open Files
			return;
		} else {
			list.clear();
			currentDirectory = f;
			String path = currentDirectory.getAbsolutePath();
			if(path.length()>1){
				path = path+"/";
			}
			((TextView)context.findViewById(R.id.textPath)).setText(path);
			//Put up icon
			if(!f.getAbsolutePath().equals("/")){
				list.add(getFileListAdapterEntryForFile(null));
			}
			File[] files = f.listFiles();
			Arrays.sort(files, comparator);
			int length = files.length;
			for(int it = 0; it < length; it++){
				list.add(getFileListAdapterEntryForFile(files[it]));
			}
			listAdapter.notifyDataSetChanged();
		}
	}
	private FileListAdapterEntry getFileListAdapterEntryForFile(File file){
		FileListAdapterEntry ret =
			new FileListAdapterEntry(file, false, getIconForFile(file));
		return ret;		
	}
	private int getIconForFile(File f){
		int ret = -1;
		if(f ==null){
			ret = R.drawable.iconup;
		} else if(f.isDirectory()){
			ret = R.drawable.iconfolder;
		} else {
			 ret = R.drawable.icontext;
			 String[] fileName = f.getName().split("\\.");
			 int arrayLength = fileName.length;
			 if(arrayLength > 1){
				 Integer value = mimeTypes.get(fileName[arrayLength-1]);
				 if(value != null){
					 ret = value.intValue();
				 }
			 }
		}
		return ret;
	}
	public FileListAdapter getAdapter() {
		return listAdapter;
	}
}
