package kangwon.JK.Lee.cafe.expain.Detail;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.Date;

import kangwon.JK.Lee.cafe.R;
import kangwon.JK.Lee.cafe.expain.Explain_Activity;

/**
 * Created by ksdy2 on 2017-07-25.
 */

public class Detail_Fragment extends Fragment {
    private String name = null, location = null, time = null, study = null, smoke = null, tel = null, j_url = null,
            comment = null, state = Explain_Activity.state;
    private URL url;
    private TextView text_location, text_time, text_tel,
            text_smoke, text_study, text_comment, text_state;
    private static final String TAG_JSON = "namelist";
    private static final String TAG_NAME = "name";
    private static final String TAG_LOACTION = "location";
    private static final String TAG_TIME = "time";
    private static final String TAG_STUDY = "studyroom";
    private static final String TAG_SMOKE = "smoking";
    private static final String TAG_TEL = "tel";
    private static final String TAG_COMMENT = "comment";
    private String mJsonString;
    private String str_name;
    private GetDataJSON getDataJSON;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        str_name = getArguments().getString("name");
        Log.i("strname", "" + getArguments().getString("name"));

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "custom_font.ttf");
        text_location = (TextView) view.findViewById(R.id.location);
        text_location.setTypeface(tf);
        text_time = (TextView) view.findViewById(R.id.time);
        text_time.setTypeface(tf);
        text_study = (TextView) view.findViewById(R.id.study);
        text_study.setTypeface(tf);
        text_smoke = (TextView) view.findViewById(R.id.smoke);
        text_smoke.setTypeface(tf);
        text_tel = (TextView) view.findViewById(R.id.tel);
        text_tel.setTypeface(tf);
        text_comment = (TextView) view.findViewById(R.id.comment);
        text_comment.setTypeface(tf);
        text_state = (TextView) view.findViewById(R.id.state);
        text_state.setTypeface(tf);
        getDataJSON = new
                GetDataJSON();
        getDataJSON.execute("http://ksdy200.cafe24.com/explain.php");





        return view;
    }

    class GetDataJSON extends AsyncTask<String, Void, String> {
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
        protected String doInBackground(String... params) {

            try {
                url = new URL(params[0]);
                String postData = "name=" + str_name;
                //Log.i("postdata=", "" + postData);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
                return null;
            }


        }

        @Override
        protected void onPostExecute(String result) {
            mJsonString = result;
            showResult();
            Log.i("Detail_프래그먼트 : ", "스레드 종료");

        }

        private void showResult() {

            long now = System.currentTimeMillis();
            // 현재시간을 date 변수에 저장한다.
            Date date = new Date(now);
            // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
            SimpleDateFormat sdfNow = new SimpleDateFormat("kk:mm aa");
            // nowDate 변수에 값을 저장한다.
            String formatDate = sdfNow.format(date);

            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    location = item.getString(TAG_LOACTION);
                    time = item.getString(TAG_TIME);
                    smoke = item.getString(TAG_SMOKE);
                    study = item.getString(TAG_STUDY);
                    tel = item.getString(TAG_TEL);
                    comment = item.getString(TAG_COMMENT);
                    String[] compare = time.split("~");
                    String currtime = formatDate.split(" ")[0];

                    int curr = Integer.parseInt(currtime.replace(":",""));
                    int open = Integer.parseInt(compare[0].replace(":",""));
                    int close = Integer.parseInt(compare[1].replace(":","").trim());



                    Log.i("오픈",""+open);
                    Log.i("마감",""+close);
                    Log.i("현재",""+curr);

                    if(curr>=open && curr<=close){
                        state="OPEN";
                    }
                    else{
                        state="CLOSE";
                    }



                    int modify_timeFirst = Integer.parseInt(compare[1].split(":")[0]);
                    String modify_timeSecond = compare[1].split(":")[1];
                    if (modify_timeFirst>24) {//만약 새벽까지 한다면, 25시 26시 이렇게 DB에표기한 뒤 24시를 뺴서 계싼
                        modify_timeFirst -= 24;
                        time = compare[0]+"~0"+ modify_timeFirst + ":" + modify_timeSecond;
                        //text_time.setText("* 운영시간:" + time);
                    }
                    else{
                      //  text_time.setText("* 운영시간:" + time+);

                    }
                    text_time.setText("* 운영시간: " + time);
                    text_location.setText("* 카페위치: " + location);
                    text_tel.setText("* 전화번호: " + tel.trim());
                    text_smoke.setText("* 흡 연 실: " + smoke);
                    text_study.setText("* 스터디룸: " + study);
                    text_state.setText("* 상     태: " + state);
                }

                String[] temp = comment.split(" ");
                StringBuilder spl = new StringBuilder();
                for (int i = 0; i < temp.length; i++) {
                   /*if (i % 2 == 0 && i != 0) {
                        spl.append("\n- ");
                    } else if (i == 0) {
                        spl.append("- ");
                    }*/
                    spl.append("#"+temp[i] + " ");

                }
                text_comment.setText("* 추가설명:\n" + spl.toString());
                set_call();

            } catch (JSONException e) {
            }
        }

    }

    /*전화번호 눌렀을때 즉시 권한얻거나 있을경우 콜 리스너*/
    public void set_call() {
        final String number = "tel:" + tel;
        text_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { /** * 사용자 단말기의 권한 중 "전화걸기" 권한이 허용되어 있는지 확인한다. * Android는 C언어 기반으로 만들어졌기 때문에 Boolean 타입보다 Int 타입을 사용한다. */
                    int permissionResult = getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE); /** * 패키지는 안드로이드 어플리케이션의 아이디이다. * 현재 어플리케이션이 CALL_PHONE에 대해 거부되어있는지 확인한다. */
                    if (permissionResult == PackageManager.PERMISSION_DENIED) { /** * 사용자가 CALL_PHONE 권한을 거부한 적이 있는지 확인한다. * 거부한적이 있으면 True를 리턴하고 * 거부한적이 없으면 False를 리턴한다. */
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            dialog.setTitle("권한이 필요합니다.").setMessage("이 기능을 사용하기 위해서는 단말기의 \"전화걸기\" 권한이 필요합니다. 계속 하시겠습니까?")
                                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) { /** * 새로운 인스턴스(onClickListener)를 생성했기 때문에 * 버전체크를 다시 해준다. */
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // CALL_PHONE 권한을 Android OS에 요청한다.
                                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);

                                            }
                                        }
                                    }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getActivity(), "기능을 취소했습니다", Toast.LENGTH_SHORT).show();
                                }
                            }).create().show();
                        } // 최초로 권한을 요청할 때
                        else { // CALL_PHONE 권한을 Android OS에 요청한다.
                            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                        }
                    } // CALL_PHONE의 권한이 있을 때
                    else { // 즉시 실행
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                        startActivity(intent);
                    }
                } // 마시멜로우 미만의 버전일 때
                else { // 즉시 실행
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                    startActivity(intent);
                }
            }
        });

    }


}
