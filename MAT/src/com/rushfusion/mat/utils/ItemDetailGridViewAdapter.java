package com.rushfusion.mat.utils;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rushfusion.mat.R;

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
		View view = LayoutInflater.from(con).inflate(R.layout.page_item_detail_adapter_item	, null);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.count = (TextView)view.findViewById(R.id.page_item_detail_adapter_item_count);
		viewHolder.count.setText("第"+(position+1)+"集");
		return view;
	}
	
	public class ViewHolder{
		TextView count;
	}

}
