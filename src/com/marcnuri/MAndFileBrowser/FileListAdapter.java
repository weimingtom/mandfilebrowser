package com.marcnuri.MAndFileBrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcnuri.android.widget.ListAdapter;
/**
 * @author Marc Nuri San Félix
 * 
 */
public class FileListAdapter extends
		ListAdapter<FileListAdapterEntry, FileListAdapterViewHolder> {

	private HashMap<String, Integer> mimeTypes;

	public FileListAdapter(Context context, int viewid,
			ArrayList<FileListAdapterEntry> listItems) {
		super(context, viewid, listItems);
		// PERFORMANCE GAIN WHEN RETREIVING ICONS
		initMimeTypes();
	}

	private void initMimeTypes() {
		mimeTypes = new HashMap<String, Integer>();
		Resources resources = context.getResources();
		for (String extension : resources.getStringArray(R.array.audio)) {
			mimeTypes.put(extension, R.drawable.iconaudio);
			mimeTypes
					.put(extension + ".selected", R.drawable.iconselectedaudio);
		}
		for (String extension : resources.getStringArray(R.array.image)) {
			mimeTypes.put(extension, R.drawable.iconimage);
			mimeTypes.put(extension + ".selected", R.drawable.iconimage);
		}
	}

	@Override
	protected void bindHolder(FileListAdapterViewHolder h) {
		FileListAdapterEntry data = h.data;
		File f = data.file;
		Theme theme = context.getTheme();
		if (data.iconResource == null) {
			data.iconResource = getIcon(data);
		}
		h.icon.setImageResource(data.iconResource);
		if (data.selected) {
			TypedValue tv = new TypedValue();
			if (theme.resolveAttribute(android.R.attr.textColorSecondary, tv,
					true)) {
				h.fileName.setTextColor(context.getResources().getColor(
						tv.resourceId));
			}
		} else {

			TypedValue tv = new TypedValue();
			tv = new TypedValue();
			if (theme.resolveAttribute(android.R.attr.textColorPrimary, tv,
					true)) {
				h.fileName.setTextColor(context.getResources().getColor(
						tv.resourceId));
				// h.fileName.setSelected(true);
			}

		}
		if (f == null) {
			h.fileName.setText("..");
			h.fileSize.setText("");
		} else {
			h.fileName.setText(f.getName());
			h.fileSize.setText("" + f.length());

		}
	}

	@Override
	protected FileListAdapterViewHolder createHolder(View v) {
		ImageView imageIcon = (ImageView) v.findViewById(R.id.imageIcon);
		TextView textFileName = (TextView) v.findViewById(R.id.textFileName);
		TextView textFileSize = (TextView) v.findViewById(R.id.textFileSize);
		FileListAdapterViewHolder vh = new FileListAdapterViewHolder(imageIcon,
				textFileName, textFileSize);
		return vh;
	}

	private int getIcon(FileListAdapterEntry flae) {
		int ret = -1;
		File f = flae.file;
		boolean selected = flae.selected;
		if (f == null) {
			ret = R.drawable.iconup;
		} else {
			String[] fileName = f.getName().split("\\.");
			int arrayLength = fileName.length;
			if (selected) {
				if (f.isDirectory()) {
					ret = R.drawable.iconselectedfolder;
				} else {
					ret = R.drawable.iconselectedtext;
					if (arrayLength > 1) {
						Integer value = mimeTypes.get(fileName[arrayLength - 1]+ ".selected");
						if (value != null) {
							ret = value.intValue();
						}
					}
				}

			} else {
				if (f.isDirectory()) {
					ret = R.drawable.iconfolder;
				} else {
					ret = R.drawable.icontext;
					if (arrayLength > 1) {
						Integer value = mimeTypes.get(fileName[arrayLength - 1]);
						if (value != null) {
							ret = value.intValue();
						}
					}
				}
			}
		}
		return ret;
	}
}
