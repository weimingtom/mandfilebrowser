package com.marcnuri.MAndFileBrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.widget.SimpleAdapter;

public class FileDataProvider {

	private final static String MAP_KEY_ICON = "MAP_KEY_ICON";
	private final static String MAP_KEY_FILE_NAME = "MAP_KEY_FILE_NAME";
	private final static String MAP_KEY_FILE_SIZE = "MAP_KEY_FILE_SIZE";
	private final static String MAP_ABSOLUTE_PATH = "MAP_ABSOLUTE_PATH";
	private final static String MAP_KEY_FILE = "MAP_KEY_FILE";
	
	private Context context;
	private File currentDirectory; 
	private Comparator<File> comparator;
	private ArrayList<HashMap<String, Object>> list;
	private SimpleAdapter adapter;
	
	/**
	 * @author Marc Nuri San Félix
	 *
	 */
	public FileDataProvider(Context context) {
		this.context = context;
		currentDirectory = new File("/");
		comparator = new FileComparator();
		list = new ArrayList<HashMap<String, Object>>();
		adapter = new SimpleAdapter(context, list, R.layout.row, new String[] {
				MAP_KEY_ICON, MAP_KEY_FILE_NAME, MAP_ABSOLUTE_PATH }, new int[] { R.id.imageIcon, R.id.textLabel, R.id.textFile });

	}
	public void root(){
		navigateTo(new File("/"));
	}
	public void up(){
		if(currentDirectory.getParentFile() != null){
			navigateTo(currentDirectory.getParentFile());
		}
	}
	public void navigateTo(int position){
		navigateTo((File)list.get(position).get(MAP_KEY_FILE));
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
			//Put up icon
			if(!f.getAbsolutePath().equals("/")){
				list.add(getHashMapForFile(null));
			}
			File[] files = f.listFiles();
			Arrays.sort(files, comparator);
			int length = files.length;
			for(int it = 0; it < length; it++){
				list.add(getHashMapForFile(files[it]));
			}
			adapter.notifyDataSetChanged();
		}
	}
	private HashMap<String, Object> getHashMapForFile(File file){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(MAP_KEY_FILE, file);
		map.put(MAP_KEY_ICON, getIconForFile(file));
			if(file != null){
			map.put(MAP_KEY_FILE_NAME, file.getName());
			map.put(MAP_KEY_FILE_SIZE, file.length());
			map.put(MAP_ABSOLUTE_PATH, file.getAbsolutePath());
		} else {
			map.put(MAP_KEY_FILE_NAME, "..");
			map.put(MAP_KEY_FILE_SIZE, 0);
			map.put(MAP_ABSOLUTE_PATH, "");
		}
		return map;		
	}
	private int getIconForFile(File f){
		int ret = -1;
		if(f ==null){
			ret = R.drawable.iconup;
		} else if(f.isDirectory()){
			ret = R.drawable.iconfolder;
		} else {
			 ret = R.drawable.icontext;
			 String fileName = f.getName();
			 Resources resources = context.getResources();
			 if(isExtensionInArray(fileName, resources.getStringArray(R.array.audio))){
				 ret = R.drawable.iconaudio;
			 } else if(isExtensionInArray(fileName, resources.getStringArray(R.array.image))){
				 ret = R.drawable.iconimage;
			 } 
		}
		return ret;
	}
	private boolean isExtensionInArray(String fileName, String[] array){
		boolean ret = false;
		for(String extension : array){
			if(fileName.endsWith("."+extension)){
				ret = true;
			}
		}
		return ret;
	}
	public SimpleAdapter getAdapter() {
		return adapter;
	}
}
