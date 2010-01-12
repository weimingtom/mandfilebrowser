package com.marcnuri.MAndFileBrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Context;
import android.widget.SimpleAdapter;

public class FileDataProvider {

	private final static String ICON = "ICON";
	private final static String LABEL = "LABEL";
	private final static String PATH = "PATH";
	private final static String FILE = "FILE";
	
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
				ICON, LABEL, PATH }, new int[] { R.id.imageIcon, R.id.textLabel, R.id.textFile });

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
		navigateTo((File)list.get(position).get(FILE));
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
			if(fTemp.isDirectory()){
				map.put(ICON, folder);
			} else {
				map.put(ICON, text);
			}
			map.put(LABEL, fTemp.getName());
			map.put(PATH, fTemp.getAbsolutePath());
			map.put(FILE, fTemp);
			list.add(map);
		}
		adapter.notifyDataSetChanged();
	}
	public SimpleAdapter getAdapter() {
		return adapter;
	}
}
