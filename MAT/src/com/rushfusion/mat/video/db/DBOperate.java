package com.rushfusion.mat.video.db;

import java.util.ArrayList;
import java.util.List;
import com.rushfusion.mat.video.entity.Movie;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBOperate {
	/**
	 * 查询所有电影
	 */
	public List<Movie> queryAllMovie(SQLiteDatabase database, int currentPage, int count) {
		int startCount = currentPage * count + 1 ;
		int endCount = (currentPage+1) * count ;
		
		List<Movie> movies = new ArrayList<Movie>();
		Cursor c = database.rawQuery("select * from movie limit "+startCount+","+endCount, null);
		Movie movie = null;
		while(c.moveToNext()) {
			movie = new Movie(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3), 
					c.getInt(4), c.getString(5), c.getString(6), c.getString(7), 
					c.getInt(8), c.getString(9), c.getString(10), c.getString(11), 
					c.getString(12), c.getString(13), c.getString(14), c.getString(15)) ;
			movies.add(movie);
		}
		if (c != null && !c.isClosed())
			c.close();
		return movies;
	}
}
