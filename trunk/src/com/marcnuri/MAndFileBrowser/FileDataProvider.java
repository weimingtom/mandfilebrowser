package com.marcnuri.MAndFileBrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Context;
import android.widget.SimpleAdapter;

public class FileDataProvider {

	private final static String MAP_KEY_ICON = "MAP_KEY_ICON";
	private final static String MAP_KEY_FILE_NAME = "MAP_KEY_FILE_NAME";
	private final static String MAP_ABSOLUTE_PATH = "MAP_ABSOLUTE_PATH";
	private final static String MAP_KEY_FILE = "MAP_KEY_FILE";
	
	private File currentDirectory; 
	private Comparator<File> comparator;
	private ArrayList<HashMap<String, Object>> list;
	private SimpleAdapter adapter;
	
	/**
	 * @author Marc Nuri San Félix
	 *
	 */
	public FileDataProvider(Context context) {
		System.out.println("ADAPTER CREATED");
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
		if(!f.isDirectory()){
			return;
		}
		list.clear();
		currentDirectory = f;
		File[] files = f.listFiles();
		Arrays.sort(files, comparator);
		int length = files.length;
		int folder = R.drawable.folder;
		int text = R.drawable.text;
		for(int it = 0; it < length; it++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			File fTemp = files[it];
			map.put(MAP_KEY_ICON, getIconForFile(fTemp));
			if(fTemp.isDirectory()){
				map.put(MAP_KEY_ICON, folder);
			} else {
				map.put(MAP_KEY_ICON, text);
			}
			map.put(MAP_KEY_FILE_NAME, fTemp.getName());
			map.put(MAP_ABSOLUTE_PATH, fTemp.getAbsolutePath());
			map.put(MAP_KEY_FILE, fTemp);
			list.add(map);
		}
		adapter.notifyDataSetChanged();
	}
	public int getIconForFile(File f){
		int ret = -1;
		if(f.isDirectory()){
			ret = R.drawable.folder;
		} else {
			 ret = R.drawable.text;
			 
		}
		return ret;
	}
	public SimpleAdapter getAdapter() {
		return adapter;
	}
}
