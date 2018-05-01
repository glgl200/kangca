package kangwon.JK.Lee.cafe.Main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by ksdy2 on 2017-07-26.
 */

public class Viewpaer_Thread {
    Bitmap bitmap;
    Thread mThread;
    URI url;


    public Bitmap thread(String name) {

        try {
            name = URLEncoder.encode(name, "euc-kr");
            name=name.replace("%3F","");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String finalName = name;

        mThread = new Thread() {

            @Override
            public void run() {
                try {
                    String uri = "http://ksdy200.cafe24.com/" + finalName + "/" + finalName + ".png";


                    url = new URI(uri);
                    HttpGet httpRequest1 = new HttpGet(String.valueOf(url.toURL()));
                    HttpClient httpClient1 = new DefaultHttpClient();
                    HttpResponse response1 = (HttpResponse) httpClient1.execute(httpRequest1);

                    HttpEntity entity1 = response1.getEntity();
                    BufferedHttpEntity bufferedHttpEntity1 = new BufferedHttpEntity(entity1);
                    InputStream in1 = bufferedHttpEntity1.getContent();
                    bitmap = BitmapFactory.decodeStream(in1);
                    // bitmap= bitmap.createScaledBitmap(bitmap, 300,300,false);

                    in1.close();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
