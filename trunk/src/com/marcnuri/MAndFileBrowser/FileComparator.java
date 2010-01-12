package com.marcnuri.MAndFileBrowser;

import java.io.File;
import java.util.Comparator;
/**
 * @author Marc Nuri San Félix
 *
 */
public class FileComparator implements Comparator<File> {
	public static enum Types{
		NAME, SIZE;
	}
	private Types type;
	public FileComparator(){
		this(Types.NAME);
	}
	public FileComparator(Types type){
		this.type = type;
	}
	public int compare(File file, File compared) {
		switch(type){
		case SIZE:
			return new Long(file.length()).compareTo(new Long(compared.length()));
		default:
			if(file.isDirectory() && compared.isDirectory()){
				return file.getName().compareTo(compared.getName());
			} else if(file.isDirectory() && !compared.isDirectory()){ 
				return -1;
			}  else if(!file.isDirectory() && compared.isDirectory()){ 
				return 1;
			} else  {
				return file.getName().compareTo(compared.getName());
			}
		}
	}
	
}