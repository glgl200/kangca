package kangwon.JK.Lee.cafe.expain;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import kangwon.JK.Lee.cafe.R;
import kangwon.JK.Lee.cafe.map.Marker_Set;

/**
 * Created by ksdy2 on 2017-08-04.
 */

public class View_Map extends Activity implements OnMapReadyCallback {
    private GoogleMap map;
    private Marker currentMarker = null, mapping_marker = null;
    private URL url;
    private ArrayList<HashMap<String, String>> mArrayList;

    private Map_list map_list;
    private MapFragment mapFragment;

    private static String TAG = "phptest_MainActivity";
    private static final String TAG_JSON = "latlnglist";
    private static final String TAG_LATLNG = "latlng";
    private String mJsonString;
    String str_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mapsearch_page);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.Linear_textsearch);
        linearLayout.setVisibility(View.GONE);
        Intent intent = getIntent();
        str_name = intent.getStringExtra("name");
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = (int) (display.getWidth() * 0.7); //Display 사이즈의 70%
        int height = (int) (display.getHeight() * 0.5);  //Display 사이즈의 50%
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;
        map_list = new Map_list();
        map_list.execute();
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    protected void onStop() {
        map.clear();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }


    public void setCurrentLocation(double lat, double lng) {
        LatLng latLng = new LatLng(lat, lng);
        if (currentMarker != null) currentMarker.remove();
        Marker_Set marker_set = new Marker_Set(latLng);
        MarkerOptions markerOptions = marker_set.current_marker();
        currentMarker = map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        map.clear();
        super.onDestroy();
    }


    class Map_list extends AsyncTask<Void, Void, String> {

        private String readStream(InputStream in) throws IOException {
            StringBuilder jsonHtml = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = null;

            while ((line = reader.readLine()) != null)
                jsonHtml.append(line);

            reader.close();
            return jsonHtml.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            mJsonString = s;
            showResult(mJsonString);


            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                url = new URL("http://ksdy200.cafe24.com/find_map.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                String postData = "name=" + str_name;
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postData.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                String result = readStream(conn.getInputStream());
                conn.disconnect();
                return result.trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private void showResult(final String result) {
            try {
                mArrayList = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    String[] latlng = item.getString(TAG_LATLNG).split(",");
                    double lat = Double.parseDouble(latlng[0]);
                    double lng = Double.parseDouble(latlng[1]);
                    setCurrentLocation(lat, lng);
                }
            } catch (JSONException e) {
            }
        }
    }


}
