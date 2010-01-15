package com.marcnuri.android.widget;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class ListAdapter<T, V extends ViewHolder<T>> extends
		BaseAdapter {
	protected Context context;
	private ArrayList<T> listItems = new ArrayList<T>();
	private LayoutInflater layoutInflater;
	private int mViewId;

	public ListAdapter(Context context, int viewid, ArrayList<T> listItems) {
		layoutInflater = LayoutInflater.from(context);
		mViewId = viewid;
		this.listItems = listItems;
		this.context = context;
	}

	public int getCount() {
		return listItems.size();
	}

	public T getItem(int position) {
		return listItems.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unchecked")
	public View getView(int position, View view, ViewGroup parent) {
		// A ViewHolder keeps references to children views to avoid uneccessary
		// calls
		// to findViewById() on each row.
		V holder;

		// When view is not null, we can reuse it directly, there is no need
		// to reinflate it. We only inflate a new View when the view supplied
		// by ListView is null.
		if (view == null) {

			view = layoutInflater.inflate(mViewId, null);
			// call the user's implementation
			holder = createHolder(view);
			// we set the holder as tag
			view.setTag(holder);

		} else {
			// get holder back...much faster than inflate
			holder = (V) view.getTag();
		}

		// we must update the object's reference
		holder.data = getItem(position);
		// call the user's implementation
		bindHolder(holder);

		return view;
	}

	/**
	 * Creates your custom holder, that carries reference for e.g. ImageView
	 * and/or TextView. If necessary connect your clickable View object with the
	 * PrivateOnClickListener, or PrivateOnLongClickListener
	 * 
	 * @param vThe
	 *            view for the new holder object
	 */
	protected abstract V createHolder(View v);

	/**
	 * Binds the data from user's object to the holder
	 * 
	 * @param h
	 *            The holder that shall represent the data object.
	 */
	protected abstract void bindHolder(V h);
}
