package com.example.searchbestseller;

import android.graphics.Bitmap;

public class ItemListData {

     //String title;
     String img_url;
     //String writer;


//    public ItemListData(String title , String url){//, String writer){
    public ItemListData(String url){//, String writer){
        //this.title = title;
        this.img_url = url;
        //this.writer = writer;

    }

  //  public String getTitle() {
//        return title;
//    }

    public String getImg_url() {
        return img_url;
    }
//    public String getDetail_link() {
//        return writer;
//    }
}
