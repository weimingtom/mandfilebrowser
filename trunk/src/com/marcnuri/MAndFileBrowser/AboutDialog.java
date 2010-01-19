package com.marcnuri.MAndFileBrowser;

import android.app.Dialog;
import android.content.Context;
import android.text.util.Linkify;
import android.view.Display;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class AboutDialog extends Dialog {
	public TextView textAboutContent;
	public AboutDialog(Context context){
		super(context);
		setContentView(R.layout.aboutdialog);
		setTitle(R.string.about);
		LayoutParams paramsDialog = getWindow().getAttributes();
		WindowManager wm =(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		paramsDialog.width = (int)((double)display.getWidth()*0.9);
		
		textAboutContent = (TextView)findViewById(R.id.aboutDialogTextAboutContent);
		Linkify.addLinks(textAboutContent, Linkify.WEB_URLS);
		
	}
}
