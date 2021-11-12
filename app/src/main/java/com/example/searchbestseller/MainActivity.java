package com.example.searchbestseller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    
    private String Gubun;                       //사이트 구분
    private String htmlPageUrl;                 //홈페이지 주소
    private String book_img_link;               //이미지 링크
    //private String book_title;                //책이름
    private RecyclerView mRecyclerView;         //리사이클러뷰
    private MyRecyclerAdapter mRecyclerAdapter; //어댑터
    private ArrayList<ItemListData> mItemList = new ArrayList<>();  //데이터 담아주는 부분

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RecycleView 선언
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //교보문고 버튼
        Button kyobo_button = (Button) findViewById(R.id.kyobobutton);
        //알라딘 버튼
        Button aladin_button = (Button) findViewById(R.id.aladinbutton);
        //YES24 버튼
        Button yes24_button = (Button) findViewById(R.id.yes24button);

        //버튼 이벤트(교보문고 버튼 클릭 시)
        kyobo_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                htmlPageUrl = "https://www.kyobobook.co.kr/bestSellerNew/bestseller.laf?orderClick=d79";
                Gubun = "kyobo";
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
            }
        });

        //알라딘 버튼 눌렀을때
        aladin_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                htmlPageUrl = "https://www.aladin.co.kr/shop/common/wbest.aspx?BranchType=1&start=we";
                Gubun = "aladin";
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
            }
        });

        //YES24 버튼 눌렀을때
        yes24_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                htmlPageUrl = "https://www.yes24.com/24/Category/BestSeller";
                Gubun = "yes24";
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
            }
        });
    }
    //파싱은 네트워크 작업을 해야 되서 AsyncTask를 사용함
    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //프로그래스바 시작
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("조회중 입니다.");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(htmlPageUrl).get();
                mItemList.removeAll(mItemList);
                //교보문고 일때
                if (Gubun == "kyobo") {
                    Elements mElementDataSize = doc.select("ul[class=list_type01]").select("li").select("div[class=cover] a img");

                    for(Element element : mElementDataSize){
                        book_img_link = element.attr("src").replace("http","https");
                        //book_title = element.select("div[class=cover] a img").attr("alt");

                        mItemList.add(new ItemListData(book_img_link));
                    }
                }
                //알리딘 일때
                else if(Gubun == "aladin")
                {
                    Elements mElementDataSize = doc.select("div[class=ss_book_box]");

                    for(Element element : mElementDataSize){
                        book_img_link = element.select("tr a img").attr("src");
                        //book_title = element.select("div[class=cover] a img").attr("alt");
                        mItemList.add(new ItemListData(book_img_link));
                    }
                }
                //YES24 일때
                else
                {
                    Elements mElementDataSize = doc.select("ol[class=\"\"]").select("li").select("[class=image] a img");
                    for(Element element : mElementDataSize){
                        book_img_link = element.attr("src").replace("http","https");
                        //book_title = element.select("div[class=cover] a img").attr("alt");
                        mItemList.add(new ItemListData(book_img_link));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        
        //어댑터 연결
        @Override
        protected void onPostExecute(Void result) {
            mRecyclerAdapter = new MyRecyclerAdapter();
            /* initiate recyclerview */
            mRecyclerView.setAdapter(mRecyclerAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            mRecyclerAdapter.setItemList(mItemList);

            //프로그래스바 종료
            progressDialog.dismiss();
            //SearchBookImage();
        }
    }


    //버튼 클릭 시 url을 통해서 이미지를 가져오는 부분
//    private void SearchBookImage()
//    {
//        imageView = (ImageView) findViewById(R.id.ImageView_bookimage);
//        //textview_author = (TextView) findViewById(R.id.textView_author);
//        textView_title = (TextView) findViewById(R.id.textView_title);
//        //textView_title.setText(book_title);
//        //textview_author.setText(book_author);
//
//        Thread mThread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL(book_img_link);
//                    // Web에서 이미지를 가져온 뒤
//                    // ImageView에 지정할 Bitmap을 만든다
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setDoInput(true); // 서버로 부터 응답 수신
//                    conn.connect();
//
//                    InputStream is = conn.getInputStream(); // InputStream 값 가져오기
//                    bitmap = BitmapFactory.decodeStream(is); // Bitmap으로 변환
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        mThread.start(); // Thread 실행
//
//        try {
//            // 메인 Thread는 별도의 작업 Thread가 작업을 완료할 때까지 대기해야한다
//            // join()를 호출하여 별도의 작업 Thread가 종료될 때까지 메인 Thread가 기다리게 한다
//            mThread.join();
//
//            // 작업 Thread에서 이미지를 불러오는 작업을 완료한 뒤
//            // UI 작업을 할 수 있는 메인 Thread에서 ImageView에 이미지를 지정한다
//            imageView.setImageBitmap(bitmap);
//
////            mItemList = new ArrayList<>();
////            for(int i=1;i<=10;i++){
////                mItemList.add(new ItemListData(book_title,bitmap,"22"));
////            }
////            mRecyclerAdapter.setItemList(mItemList);
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

}