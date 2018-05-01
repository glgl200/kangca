package kangwon.JK.Lee.cafe.map;

import android.Manifest;
import android.annotation.TargetApi;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import kangwon.JK.Lee.cafe.expain.Explain_Activity;
import kangwon.JK.Lee.cafe.R;

public class MapSearch_Activity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap map;
    private BroadcastReceiver mbroadcastreceiver;
    private Intent service_intent;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private Marker currentMarker = null, mapping_marker = null;
    boolean askPermissionOnceAgain = false;
    private URL url;
    private ArrayList<HashMap<String, String>> mArrayList;
    private static final String TAG_NAME = "name";
    private static final String TAG_MAP = "map";
    private static final String TAG_JSON = "namelist";
    double last_lat, last_lng;
    boolean drag_state = false;
    private Map_list map_list;
    private LocationManager locationManager;
    private MapFragment mapFragment;

    private Button myloc, search;
    private EditText editText;
    private IntentFilter receiver;
    private ProgressDialog dialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsearch_page);


        myloc = (Button) findViewById(R.id.myloc);
        myloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(last_lat, last_lng), 16));
            }
        });
        search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String find_name = editText.getText().toString();
                int size = mArrayList.size();
                for (int i = 0; i < size; i++) {
                    if (mArrayList.get(i).get(TAG_NAME).toString().equals(find_name)) {
                        double lat = Double.parseDouble(mArrayList.get(i).get(TAG_MAP).toString().split(",")[0]);
                        double lng = Double.parseDouble(mArrayList.get(i).get(TAG_MAP).toString().split(",")[1]);
                        LatLng search_latlng = new LatLng(lat, lng);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(search_latlng, 20));
                        drag_state = true;

                    }
                }
            }
        });

        editText = (EditText) findViewById(R.id.text_edit);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String find_name = editText.getText().toString();
                int size = mArrayList.size();
                for (int i = 0; i < size; i++) {
                    if (mArrayList.get(i).get(TAG_NAME).toString().equals(find_name)) {
                        double lat = Double.parseDouble(mArrayList.get(i).get(TAG_MAP).toString().split(",")[0]);
                        double lng = Double.parseDouble(mArrayList.get(i).get(TAG_MAP).toString().split(",")[1]);
                        LatLng search_latlng = new LatLng(lat, lng);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(search_latlng, 20));
                        drag_state = true;
                    }
                }
                return false;
            }
        });
        receiver = new IntentFilter(); //동적 리시버 객체 할당을 위해

        service_intent = new Intent(this, Background_LocationSV.class); // 백그라운드에서 GPS API 사용해서 현재 위치 값 갱신할 서비스
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        map_list = new Map_list();// DB의 맵 좌표를 다 읽어옴
        map_list.execute();
    }

    /*온 스타트에 동적 리시버 쓰는 이유는, 화면이 로테이션 됐을 때 oncreate가 아니라 onstart 아마도?*/
    @Override
    protected void onStart() {
        set_Receiver();
        super.onStart();
    }



    /*액티비티가 디스트로이될 ㄷ땐 연결된 객체들 다 해제 및 마지막 좌표 저장, 서비스 정지 */

    @Override
    protected void onDestroy() {
        stopService(service_intent);
        unregisterReceiver(mbroadcastreceiver);
        savePreferences();
           /*맵뷰 해제*/
        if (mGoogleApiClient != null) {
            mGoogleApiClient.unregisterConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this);
            mGoogleApiClient.unregisterConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this);

            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi
                        .removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
                mGoogleApiClient.disconnect();
            }
        }

        dialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (!checkLocationServicesStatus()) {//GPS값을 받아옴, 둘다 꺼져있으면,
            showDialogForLocationServiceSetting(); // 실행
        }

        getPreferences();
        map = googleMap;
        setLocation(last_lat, last_lng, "first");

        /*현재위치 버튼*/
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LatLng latLng = new LatLng(last_lat, last_lng);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                drag_state = false;
                return false;
            }
        });

        /*마커를 클릭시, 현위치 외의 마커를 클릭하면 해당 카페로 이동*/
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String mark_title = marker.getTitle();
                if (!mark_title.equals("현 위치")) {
                    Intent intent = new Intent(MapSearch_Activity.this, Explain_Activity.class);
                    intent.putExtra("name", mark_title);
                    startActivity(intent);
                }
                return false;
            }
        });

        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                drag_state = true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //API 23 이상이면 런타임 퍼미션 처리 필요

            /*매니패스트에ACCESS_FINE_LOCATION 권한이 선언 돼 있는지확인 */
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

        /*권한이 없다면, */
            if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
               /*런타임으로 사용자에게 권한 요청*/
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {/*있다면면*/
                checkPermissions();
                startService(service_intent);

            }
        }

    }

    /*마지막 현재위치 저장 (로테이션 했을 시)*/
    public void getPreferences() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        last_lat = pref.getFloat("last_lat", 0);
        last_lng = pref.getFloat("last_lng", 0);
    }

    private void savePreferences() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("last_lat", (float) last_lat);
        editor.putFloat("last_lng", (float) last_lng);
        editor.commit();
    }
/*----------------------------------------------*/


    public void setLocation(double lat, double lng, String state) {

        if (state.equals("curr")) {
            LatLng latLng = new LatLng(lat, lng);
            if (currentMarker != null) currentMarker.remove();
            Marker_Set marker_set = new Marker_Set(latLng);
            MarkerOptions markerOptions = marker_set.current_marker();
            currentMarker = map.addMarker(markerOptions);
        } else {
            LatLng latLng = new LatLng(lat, lng);
            if (currentMarker != null) currentMarker.remove();
            Marker_Set marker_set = new Marker_Set(latLng);
            MarkerOptions markerOptions = marker_set.current_marker();
            currentMarker = map.addMarker(markerOptions);
            if (drag_state == false)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
    }


    /*설정 창 갔다왔을 때*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:
//if (resultCode == RESULT_OK)
//사용자가 GPS 활성 시켰는지 검사
                if (locationManager == null)
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (checkLocationServicesStatus()) {/* GPS활성화돼 있는지 검사*/
// GPS 가 ON으로 변경되었을 때의 처리.
                    mapFragment.getMapAsync(this);//다시한번 갱신

                }
                break;
        }
    }

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
            /*위치 권한을 요청하는 메세지창 띄우기.*/

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED
                && fineLocationRationale) {
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
        }


        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        }


        else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            //허가 했을 때
            startService(service_intent);
        }
    }

    /*GPS활성화 검사하는 것*/
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        /*GPS 정보 가져오기*/
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }           /*현재 네트워크 상태값 알아오기*/


    //최초 실행시 다이얼로그에 거절 헀을 때 , 추가 설명
    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapSearch_Activity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getApplicationContext().getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapSearch_Activity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱의 사용을 위해서는 위치 서비스가 필요합니다.\n"
                + "Setting 하시겠습니까?");
        builder.setCancelable(true);

        /*설정 버튼 클릭시, GPS설정 페이지로 간다.*/
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });


        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    /*최초 런타임 권한 묻는 다이얼로그그*/
    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapSearch_Activity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(MapSearch_Activity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onResume() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();

        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;
                checkPermissions();
            }
        }
        mapFragment.onResume();
        super.onResume();
    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {
            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (permissionAccepted) {

                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    startService(service_intent);
                }
            } else {
                checkPermissions();
            }
        }
    }


    class Map_list extends AsyncTask<Void, Void, Void> {
        MarkerOptions markerOptions;

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
        protected void onPreExecute() {
            dialog = new ProgressDialog(MapSearch_Activity.this);
            dialog.setMessage("잠시만 기다려주세요......");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            for (int i = 0; i < mArrayList.size(); i++) {
                /*Json 파싱된 걸 이름 / 좌표값으로 분해*/
                String name = mArrayList.get(i).get("name").toString();
                String[] mapping = mArrayList.get(i).get("map").split(","); //Lat,Lng로 분리

                double lat = Double.parseDouble(mapping[0].trim());//더블형 캐스팅
                double lng = Double.parseDouble(mapping[1].trim());

                LatLng latLng = new LatLng(lat, lng); //LatLng 객체에 전달

                Marker_Set marker_set = new Marker_Set(latLng);
                markerOptions = marker_set.mapping_marker(name, latLng);
                mapping_marker = map.addMarker(markerOptions);
            }
            dialog.dismiss();

            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                url = new URL("http://ksdy200.cafe24.com/read_map.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                String result = readStream(conn.getInputStream());
                conn.disconnect();

                mArrayList = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    String name = item.getString(TAG_NAME);
                    String map = item.getString(TAG_MAP);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(TAG_NAME, name);
                    hashMap.put(TAG_MAP, map);
                    mArrayList.add(hashMap);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


    }

    /*리시버 동적 등록 및 해제 */
    public void set_Receiver() {
        receiver.addAction("location");
        mbroadcastreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                double lat = intent.getDoubleExtra("lat", 0);
                double lng = intent.getDoubleExtra("lng", 0);
                last_lat = lat;
                last_lng = lng;
                setLocation(lat, lng, "curr");
                if (drag_state == false) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16));
                }
            }
        };
        registerReceiver(mbroadcastreceiver, receiver);
    }

    public void remove_Receiver() {
        unregisterReceiver(mbroadcastreceiver);
    }
    /*-------------------------------------------------------------------------------------------------*/


}
