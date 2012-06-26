package com.rushfusion.mat;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rushfusion.mat.page.ItemDetailPage;
//import com.rushfusion.mat.page.PageCache;
import com.rushfusion.mat.page.RecommendPage;
import com.rushfusion.mat.utils.DataParser;

public class MATActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	private View menu;
	private View conditionBar;
	
	
	private String currentOrigin="";
	private String currentCategory="";
	private String currentType="";
	
	private List<String> categories;
	private HashMap<String,List<String>> conditions = new HashMap<String, List<String>>();
	private List<String> types;
	private List<String> years;
	private List<String> areas;
	
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
