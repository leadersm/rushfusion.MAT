package com.rushfusion.mat.movie.message;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class PullParserXml {

	public List<String> getDownLoadUrlPortal(InputStream inputStream) {
		List<String> portalList = null ;
		XmlPullParser parser = Xml.newPullParser() ;
		String url = null  ;
		try {
			parser.setInput(inputStream, "UTF-8") ;
			int event = parser.getEventType() ;
			while(event!=XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					portalList = new ArrayList<String>() ;
					url = "" ;
					break;
				case XmlPullParser.START_TAG:
					if("urls".equals(parser.getName())) {
						url = parser.getAttributeValue(0) ;
						//String url = parser.nextText() ;
					}
					break;
					
				case XmlPullParser.END_TAG:
					if("urls".equals(parser.getName())) {
						portalList.add(url) ;
					}
					break;
				default:
					break;
				}
				event = parser.next() ;
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return portalList;
	}
}
