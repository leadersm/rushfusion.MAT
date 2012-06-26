package com.rushfusion.mat.utils;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ItemDetailGridViewAdapter extends BaseAdapter{
	Context con;
	List<String> list;
	
	public ItemDetailGridViewAdapter(Context con, List<String> list) {
		super();
		this.con = con;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv = new TextView(con);
		tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		tv.setGravity(Gravity.CENTER);
		tv.setText(position+1+"");
		return null;
	}

}
