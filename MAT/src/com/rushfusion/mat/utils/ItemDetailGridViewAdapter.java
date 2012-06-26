package com.rushfusion.mat.utils;

import java.util.List;

import com.rushfusion.mat.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
		View view = LayoutInflater.from(con).inflate(R.layout.page_item_detail_adapter_item	, null);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.count = (TextView)view.findViewById(R.id.page_item_detail_adapter_item_count);
		viewHolder.count.setText((position+1)+"");
		return view;
	}
	
	public class ViewHolder{
		TextView count;
		
	}

}
