package com.chad.baserecyclerviewadapterhelper;

/**
 * Created by yunze on 2019/8/22 14:21
 * Email: arthur.yunze@gmail.com
 */

public class Message {
	private String text;
	private String imgurl;

	public Message(String text,String imgurl){
		this.text = text;
		this.imgurl=imgurl;

	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getImgurl() {
		return imgurl;
	}

	public String getText() {
		return text;
	}

}
