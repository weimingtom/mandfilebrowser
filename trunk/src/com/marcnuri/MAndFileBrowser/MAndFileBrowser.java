package com.marcnuri.MAndFileBrowser;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author Marc Nuri San Félix
 *
 */
public class MAndFileBrowser extends Activity {
	private final static int MENU_ITEM_EXIT = 1;
	private FileDataProvider provider;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initComponents();
    }
    private void initComponents(){
    	provider = new FileDataProvider((Context)this);
    	((FileListView)findViewById(R.id.fileListView)).setAdapter(provider.getAdapter());
    	((FileListView)findViewById(R.id.fileListView)).setOnItemClickListener(
    			new OnItemClickListener() {

					public void onItemClick(AdapterView<?> adapterView, View view,
							int position, long id) {
						provider.navigateTo(position);
					}
    				
				});

    	provider.root();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	int NONE = Menu.NONE;
    	menu.add(NONE, MENU_ITEM_EXIT, NONE, R.string.exit);
    	return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    	case MENU_ITEM_EXIT:
    		quit();
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
    		provider.up();
    		return false;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    private void quit(){
    	finish();
    }
}