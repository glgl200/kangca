package kangwon.JK.Lee.cafe.map;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by ksdy2 on 2017-07-25.
 */

public class Marker_Set extends MapSearch_Activity {
    private Marker currentMarker = null;
    private Thread mThread;
    private Bitmap bitmap_logo;
    private URL url;


    private LatLng latLng;

    public Marker_Set(LatLng latLng) {
        this.latLng = latLng;
    }

    public void read_logo(final String str_name) {
        mThread = new Thread() {

            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try {
                    String name=str_name;
                    name= URLEncoder.encode(name,"euc-kr").replace("%3F","");
                    StringBuilder url_builder;
                    String uri = "http://ksdy200.cafe24.com/" + name + "/" + name + ".png";
                    url = new URL(uri);
                    HttpGet httpRequest1 = new HttpGet(url.toURI());
                    HttpClient httpClient1 = new DefaultHttpClient();
                    HttpResponse response1 = (HttpResponse) httpClient1.execute(httpRequest1);
                    HttpEntity entity1 = response1.getEntity();
                    BufferedHttpEntity bufferedHttpEntity1 = new BufferedHttpEntity(entity1);
                    InputStream in1 = bufferedHttpEntity1.getContent();
                    bitmap_logo = BitmapFactory.decodeStream(in1);
                    bitmap_logo = bitmap_logo.createScaledBitmap(bitmap_logo, 80, 80, false);

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
    }


    public MarkerOptions current_marker() {
        if (currentMarker != null) currentMarker.remove();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("현 위치");
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        return markerOptions;
    }

   public MarkerOptions mapping_marker(String name, LatLng latLng) {
        read_logo(name);
        if (currentMarker != null) currentMarker.remove();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(name);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap_logo));

        return markerOptions;
    }

}
