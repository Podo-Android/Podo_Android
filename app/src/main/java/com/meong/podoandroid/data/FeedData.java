package com.meong.podoandroid.data;

public class FeedData {
    private String title;
    private String img_url;
    private String content;

    public FeedData(String title, String img_url, String content) {
        this.title=title;
        this.img_url=img_url;
        this.content=content;
    }

    public String getTitle(){
        return title;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getContent() {
        return content;
    }


}