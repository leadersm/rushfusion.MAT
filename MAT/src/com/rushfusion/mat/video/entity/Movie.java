package com.rushfusion.mat.video.entity;


public class Movie {
	/**
	 * total": 1,
      "score": 79,
      "comment": 622214,
      "artists": "杨千嬅;余文乐",
      "name": "志明与春娇",
      "area": "香港",
      "play": 9011,
      "count": 1,
      "length": "01:42:03",
      "recent": 189,
      "year": 2010,
      "directors": "彭浩翔",
      "thumb": "http://cache.mars.sina.com.cn/nd/movievideo/thumb/71/3071_120160.jpg",
      "url": "http://v.iask.com/v_play_ipad.php?vid=51734192",
      "type": "爱情;喜剧",
      "id": 3071,
      "description": "一纸戒烟令让狭窄的后巷成为新型社交区域，无数抽数的男男女女不得不躲进那里抽烟。来自广告公司的志明和在化妆品专柜工作的春娇在那相识，志明的不幸遭遇以及为春娇点烟的举动让春娇对这个斯文的男人略生好感。在接下来的几个日日夜夜，两人不时的传送短信和相约街角吸烟。感情通过拇指传送，爱情在烟雾之中滋生……",
      "category": "movie"

	 */
	
	private int count ;
	private int total ;
	private int score ;
	private int click ;
	private int comment ;
	private String category ;
	private String name ;
	private String type ;
	private int year ;
	private String directors ;
	private String artists ;
	private String area ;
	private String description ;
	private String thumb ;	//图片
	private String length ;	//时长
	private String url ;	//播放地址
	
	public Movie(){
		
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getClick() {
		return click;
	}
	public void setClick(int click) {
		this.click = click;
	}
	public int getComment() {
		return comment;
	}
	public void setComment(int comment) {
		this.comment = comment;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getDirectors() {
		return directors;
	}
	public void setDirectors(String directors) {
		this.directors = directors;
	}
	public String getArtists() {
		return artists;
	}
	public void setArtists(String artists) {
		this.artists = artists;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Movie(int count, int total, int score, int click, int comment,
			String category, String name, String type, int year,
			String directors, String artists, String area, String description,
			String thumb, String length, String url) {
		super();
		this.count = count;
		this.total = total;
		this.score = score;
		this.click = click;
		this.comment = comment;
		this.category = category;
		this.name = name;
		this.type = type;
		this.year = year;
		this.directors = directors;
		this.artists = artists;
		this.area = area;
		this.description = description;
		this.thumb = thumb;
		this.length = length;
		this.url = url;
	}
	
	
	
}
