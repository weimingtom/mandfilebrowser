package com.marcnuri.MAndFileBrowser;

import android.app.Dialog;
import android.content.Context;
import android.text.util.Linkify;
import android.view.Display;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class InfoDialog extends Dialog {
	public TextView textAboutContent;
	public InfoDialog(Context context, int titleResourceId, int textContentResourceId){
		super(context);
		setContentView(R.layout.infodialog);
		setTitle(titleResourceId);
		LayoutParams paramsDialog = getWindow().getAttributes();
		WindowManager wm =(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		paramsDialog.width = (int)((double)display.getWidth()*0.9);
		
		textAboutContent = (TextView)findViewById(R.id.infoDialogTextContent);
		textAboutContent.setText(textContentResourceId);
		Linkify.addLinks(textAboutContent, Linkify.WEB_URLS);
		
	}
}
