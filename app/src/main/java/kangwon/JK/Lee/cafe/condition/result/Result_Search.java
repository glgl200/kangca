package kangwon.JK.Lee.cafe.condition.result;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;

import kangwon.JK.Lee.cafe.R;
import kangwon.JK.Lee.cafe.expain.Explain_Activity;
import kangwon.JK.Lee.cafe.font.BaseActivity;

/**
 * Created by ksdy2 on 2017-07-29.
 */

public class Result_Search extends BaseActivity {
    private static String TAG = "phptest_MainActivity";
    private static final String TAG_JSON = "namelist";
    private static final String TAG_NAME = "name";
    private static final String TAG_LOACTION = "location";
    private static final String TAG_TIME = "time";
    private static final String TAG_STATE = "state";
    private String Search_name = "";
    private String Search_location = "";
    private String Search_smoke = "";
    private String Search_study = "";
    private URL url;
    private GetDataJSON getDataJSON;
    private ArrayList<HashMap<String, String>> mArrayList;
    private ListView mlistView;
    private String mJsonString;
    private Result_Adapter mMyAdapter;
    private TextView result_name, result_loc, result_state;
    private String state = "";

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_search);

        mlistView = (ListView) findViewById(R.id.listView_main_list);// 리스트 뷰
        mlistView.setTextFilterEnabled(true);
        result_name = (TextView) findViewById(R.id.result_name);
        result_loc = (TextView) findViewById(R.id.result_loc);
        result_state = (TextView) findViewById(R.id.result_state);

        Intent intent = getIntent();
        Search_name = intent.getStringExtra("cafename");
        Search_location = intent.getStringExtra("cafelocation");
        Search_smoke = intent.getStringExtra("cafesmoke");
        Search_study = intent.getStringExtra("cafestudy");
        getDataJSON = new GetDataJSON();
        getDataJSON.execute("http://ksdy200.cafe24.com/search.php");
    }


    @Override
    protected void onDestroy() {
        try {
            if (getDataJSON.getStatus() == AsyncTask.Status.RUNNING) {
                getDataJSON.cancel(true);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    /////  /*URL로 부터 읽어오는 내용 리스트 표시 및 필터*///////////////////////
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
               /* Search_name= URLEncoder.encode(Search_name,"euc-kr").replace("%3F","");
                Search_location= URLEncoder.encode(Search_location,"euc-kr").replace("%3F","");
                Search_smoke= URLEncoder.encode(Search_smoke,"euc-kr").replace("%3F","");
                Search_study= URLEncoder.encode(Search_study,"euc-kr").replace("%3F","");
*/
                String postData = "name=" + Search_name + " & " + "location=" + Search_location + " & " + "smoke=" + Search_smoke
                        + " & " + "study=" + Search_study;
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
            showResult(result);
            dataSetting();
            set_ListListener();
            set_Button();

        }


        private void showResult(final String result) {
            long now = System.currentTimeMillis();
            // 현재시간을 date 변수에 저장한다.
            Date date = new Date(now);
            // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
            SimpleDateFormat sdfNow = new SimpleDateFormat("kk:mm aa");
            // nowDate 변수에 값을 저장한다.
            String formatDate = sdfNow.format(date);

            try {
                mArrayList = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);

                    String name = item.getString(TAG_NAME);
                    String location = item.getString(TAG_LOACTION);
                    String time = item.getString(TAG_TIME);
                    String[] compare = time.split("~");
                    String currtime = formatDate.split(" ")[0];

                    int curr = Integer.parseInt(currtime.replace(":",""));
                    int open = Integer.parseInt(compare[0].replace(":",""));
                    int close = Integer.parseInt(compare[1].replace(":",""));


                    if(curr>=open && curr<=close){
                        state="OPEN";
                    }
                    else{
                        state="CLOSE";
                    }


                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(TAG_NAME, name);
                    hashMap.put(TAG_LOACTION, location);
                    hashMap.put(TAG_STATE, state);
                    mArrayList.add(hashMap);
                }


            } catch (JSONException e) {
            }
        }

        private void dataSetting() {

            mMyAdapter = new Result_Adapter(getApplicationContext());


            for (int i = 0; i < mArrayList.size(); i++) {
                String name = mArrayList.get(i).get("name");
                String location = mArrayList.get(i).get("location");
                String state = mArrayList.get(i).get("state");
                mMyAdapter.add_item(name, location, state);
            }
            mMyAdapter.sort_location();
            mMyAdapter.notifyDataSetChanged();
            mlistView.setAdapter(mMyAdapter);

        }
    }


    public void set_Button() {
        result_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "이름순으로 정렬합니다.", Toast.LENGTH_SHORT).show();
                mMyAdapter.sort_name();
                mMyAdapter.notifyDataSetChanged();
                mlistView.setAdapter(mMyAdapter);

            }
        });
        result_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "위치순으로 정렬합니다.", Toast.LENGTH_SHORT).show();

                mMyAdapter.sort_location();
                mMyAdapter.notifyDataSetChanged();
                mlistView.setAdapter(mMyAdapter);

            }
        });
        result_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "상태순으로 정렬합니다.", Toast.LENGTH_SHORT).show();

                mMyAdapter.sort_state();
                mMyAdapter.notifyDataSetChanged();
                mlistView.setAdapter(mMyAdapter);
            }
        });
    }

    public void set_ListListener() {
        /* 리스트 클릭 리스너*/
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Result_Search.this, Explain_Activity.class);
                intent.putExtra("name", mMyAdapter.getItem(position).getName());
                String state = mMyAdapter.getItem(position).getState();
                startActivity(intent);


            }
        });
    }
}
