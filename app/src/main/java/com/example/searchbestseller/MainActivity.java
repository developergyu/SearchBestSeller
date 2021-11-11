package com.example.searchbestseller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;        //이미지뷰
    private TextView textview_author;   //저자
    private TextView textView_title;    //첵제목
    private String Gubun;               //사이트 구분
    private Bitmap bitmap;              //비트맵으로 이미지 담아줌
    private String htmlPageUrl;         //홈페이지 주소
    private String book_img_link;       //이미지 링크
    private String book_title;          //책이름
    private String book_author;         //책 저자

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button kyobo_button = (Button) findViewById(R.id.kyobobutton);
        Button aladin_button = (Button) findViewById(R.id.aladinbutton);
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


    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(htmlPageUrl).get();

                if (Gubun == "kyobo") {
                    Elements mElementDataSize = doc.select("ul[class=list_type01]").select("li");
                    book_img_link = mElementDataSize.select("li div[class=cover] a img").attr("src").replace("http","https");
                    book_title = mElementDataSize.select("li div[class=cover] a img").attr("alt");
                    //book_author = mElementDataSize.select("li div[class=author]").text();
                }
                else if(Gubun == "aladin")
                {
                    Elements mElementDataSize = doc.select("div[class=ss_book_box]");
                    book_img_link = mElementDataSize.select("div[class=ss_book_box]").select("tr a img").attr("src").toString();
                    book_title = mElementDataSize.select("[class=bo3]").select("b").toString();
                   // book_author = mElementDataSize.select("li div[class=author]").text();
                }
                else
                {
                    Elements mElementDataSize = doc.select("ol[class=\"\"]").select("li");
                    book_img_link = mElementDataSize.select("[class=image] a img").attr("src").replace("http","https").toString();
                    book_title = mElementDataSize.select("[class=image] a img").attr("alt").toString();
                    // book_author = mElementDataSize.select("li div[class=author]").text();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        //끝나고 실행하는 부분 인듯
        @Override
        protected void onPostExecute(Void result) {
            SearchBookImage();
        }
    }


    //버튼 클릭 시 url을 통해서 이미지를 가져오는 부분
    private void SearchBookImage()
    {
        imageView = (ImageView) findViewById(R.id.ImageView_bookimage);
        textview_author = (TextView) findViewById(R.id.textView_author);
        textView_title = (TextView) findViewById(R.id.textView_title);
        textView_title.setText(book_title);
        textview_author.setText(book_author);

        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(book_img_link);
                    // Web에서 이미지를 가져온 뒤
                    // ImageView에 지정할 Bitmap을 만든다
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // 서버로 부터 응답 수신
                    conn.connect();

                    InputStream is = conn.getInputStream(); // InputStream 값 가져오기
                    bitmap = BitmapFactory.decodeStream(is); // Bitmap으로 변환

                } catch (MalformedURLException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        mThread.start(); // Thread 실행

        try {
            // 메인 Thread는 별도의 작업 Thread가 작업을 완료할 때까지 대기해야한다
            // join()를 호출하여 별도의 작업 Thread가 종료될 때까지 메인 Thread가 기다리게 한다
            mThread.join();

            // 작업 Thread에서 이미지를 불러오는 작업을 완료한 뒤
            // UI 작업을 할 수 있는 메인 Thread에서 ImageView에 이미지를 지정한다
            imageView.setImageBitmap(bitmap);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class ItemObject {
        private String title;
        private String img_url;
        private String writer;


        public ItemObject(String title, String url, String writer){
            this.title = title;
            this.img_url = url;
            this.writer = writer;

        }


        public String getTitle() {
            return title;
        }

        public String getImg_url() {
            return img_url;
        }

        public String getDetail_link() {
            return writer;
        }

    }
    
}