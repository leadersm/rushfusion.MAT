package com.rushfusion.mat.page;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.rushfusion.mat.utils.Cache;
import com.rushfusion.mat.utils.ImageLoadTask;
import com.rushfusion.mat.video.entity.Movie;

public class ImageAdapter extends BaseAdapter {
    //int mGalleryItemBackground;
    private Context mContext;
    private Integer[] mImageIds;
    //private ImageView[] mImages;
    private List<Movie> mResult ;
    
    public ImageAdapter(Context c, List<Movie> result) {
     mContext = c;
     mResult = result;
     //mImages = new ImageView[result.size()];
     //TypedArray typedArray = mContext.obtainStyledAttributes(R.styleable.Gallery);
	 //mGalleryItemBackground = typedArray.getResourceId(R.styleable.Gallery_android_galleryItemBackground, 0);
    }
    
    
    
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

    
    
    /**
     * 创建倒影效果
     * @return
     */
    public boolean createReflectedImages(ImageView imageView, Bitmap bitmap) {
     //倒影图和原图之间的距离
     final int reflectionGap = 4;
     int index = 0;
     //for (int imageId : mImageIds) {
      //返回原图解码之后的bitmap对象
      Bitmap originalImage = bitmap;
      int width = originalImage.getWidth();
      int height = originalImage.getHeight();
      //创建矩阵对象
      Matrix matrix = new Matrix();
      
      //指定一个角度以0,0为坐标进行旋转
      // matrix.setRotate(30);
      
      //指定矩阵(x轴不变，y轴相反)
      matrix.preScale(1, -1);
      
      //将矩阵应用到该原图之中，返回一个宽度不变，高度为原图1/2的倒影位图       倒影
      Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
        height/2, width, height/2, matrix, false);
      
      //创建一个宽度不变，高度为原图+倒影图高度的位图	
      Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
        (height + height / 2), Config.ARGB_8888);
      
      //将上面创建的位图初始化到画布
      Canvas canvas = new Canvas(bitmapWithReflection);
      canvas.drawBitmap(originalImage, 0, 0, null);
      
      Paint deafaultPaint = new Paint(); 
      deafaultPaint.setAntiAlias(false);
//    canvas.drawRect(0, height, width, height + reflectionGap,deafaultPaint);
      canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
      Paint paint = new Paint();
      paint.setAntiAlias(false);
       
      /**
       * 参数一:为渐变起初点坐标x位置，
       * 参数二:为y轴位置，
       * 参数三和四:分辨对应渐变终点，
       * 最后参数为平铺方式，
       * 这里设置为镜像Gradient是基于Shader类，所以我们通过Paint的setShader方法来设置这个渐变
       */
      LinearGradient shader = new LinearGradient(0,originalImage.getHeight(), 0,
              bitmapWithReflection.getHeight() + reflectionGap,0x70ffffff, 0x00ffffff, TileMode.MIRROR);
      //设置阴影
      paint.setShader(shader);
      paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN));
      //用已经定义好的画笔构建一个矩形阴影渐变效果
      canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()+ reflectionGap, paint);
      
      //创建一个ImageView用来显示已经画好的bitmapWithReflection
      //ImageView imageView = new ImageView(mContext);
      imageView.setImageBitmap(bitmapWithReflection);
      //设置imageView大小 ，也就是最终显示的图片大小
      imageView.setLayoutParams(new GalleryFlow.LayoutParams(518, 320));
      //imageView.setScaleType(ScaleType.MATRIX);
      //mImages[index++] = imageView;
     //}
     return true;
    }
    @SuppressWarnings("unused")
    private Resources getResources() {
        return null;
    }
    
    public int getLength() {
		return mResult.size();
	}
    
    @Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mResult.get(position%mResult.size());
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position ;
	}
    
    public View getView(int position, View convertView, ViewGroup parent) {
        	//return mImages[position%getLength()];
    	Movie movie =  mResult.get(position%mResult.size());
		ImageView bigImage = new ImageView(mContext);
		//bigImage.setLayoutParams(new Gallery.LayoutParams(512, 320));
		bigImage.setScaleType(ScaleType.FIT_XY);
		//bigImage.setPadding(0, 58, 0, 58) ;
		String imageUrl = movie.getThumb();
		//bigImage.setImageBitmap(ImageLoadTask.loadBitmap(map.get("thumb")));
		ImageLoadTask imageLoadTask = new ImageLoadTask() ;
		if(Cache.getBitmapFromCache(imageUrl)!=null) {
			//bigImage.setImageBitmap(createReflectionImageWithOrigin(Cache.getBitmapFromCache(imageUrl))) ;
			createReflectedImages(bigImage, Cache.getBitmapFromCache(imageUrl)) ;
		} else {
			imageLoadTask.loadImage(bigImage, movie.getThumb(), new ImageLoadTask.ImageViewCallback1() {
				
				public void callbak(ImageView view, Bitmap bm) {
					//view.setImageBitmap(createReflectionImageWithOrigin(bm)) ;
					if(bm!=null) createReflectedImages(view, bm) ;
				} ;
			}) ;
		}
		//bigImage.setBackgroundResource(mGalleryItemBackground);
		return bigImage;
    }
   }
