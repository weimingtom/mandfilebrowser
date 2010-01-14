package com.marcnuri.MAndFileBrowser;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcnuri.android.widget.ListAdapter;


public class FileListAdapter extends ListAdapter<FileListAdapterEntry, FileListAdapterViewHolder> {
	
	public FileListAdapter(Context context, int viewid, ArrayList<FileListAdapterEntry> listItems) {
		super(context, viewid, listItems);
	}

	@Override
	protected void bindHolder(FileListAdapterViewHolder h) {
		FileListAdapterEntry data = h.data;
		File f = data.file;
		h.icon.setImageResource(data.iconResource);
		
		if(f == null){
			h.fileName.setText("..");
			h.fileSize.setText("");
		} else {
			h.fileName.setText(f.getName());
			h.fileSize.setText(""+f.length());
		}
	}

	@Override
	protected FileListAdapterViewHolder createHolder(View v) {
		ImageView imageIcon = (ImageView)v.findViewById(R.id.imageIcon);
		TextView textFileName = (TextView)v.findViewById(R.id.textFileName);
		TextView textFileSize= (TextView)v.findViewById(R.id.textFileSize);
		FileListAdapterViewHolder vh = new FileListAdapterViewHolder(imageIcon, textFileName, textFileSize);
		return vh;
	}

}
