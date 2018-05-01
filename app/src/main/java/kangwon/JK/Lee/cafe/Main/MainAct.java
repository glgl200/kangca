package kangwon.JK.Lee.cafe.Main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import kangwon.JK.Lee.cafe.R;
import kangwon.JK.Lee.cafe.bookmark.Bookmark;
import kangwon.JK.Lee.cafe.bookmark.Bookmark_Adapter;
import kangwon.JK.Lee.cafe.condition.Condition_Search;
import kangwon.JK.Lee.cafe.map.MapSearch_Activity;


/**
 * Created by JK on 2017-07-11.
 */
public class MainAct extends AppCompatActivity {// AppCompatActivity -> BaseActivity로 하지 않는 이유는, 프래그먼트 매니져가 호출 안됨.

    private Typeface tf;
    private static String TAG = "phptest_MainActivity";
    private URL url;
    private static final String TAG_JSON = "namelist";
    private static final String TAG_NAME = "name";
    public static ArrayList<HashMap<String, String>> mArrayList;
    private String mJsonString;
    private Button text_search;//글자 검색 버튼
    private Button map_search;//지도로 검색 버튼
    private Button book;
    private Button quest;
    private ViewPager viewPager;
    private String[] name = new String[3];
    private static final int COUNT = 3;
    private TextView search, map, bookmark, comment;
    public static Bookmark_Adapter adapter;
    public ProgressDialog dialog;
    private boolean check_first = false;
    private TimeThread timeThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainact);

        set_id();
        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute("http://ksdy200.cafe24.com/recommend.php");


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /*초기 아이디를 설정하는 메소드*/
    public void set_id() {

        tf = Typeface.createFromAsset(this.getAssets(), "custom_font.ttf");
        quest = (Button) findViewById(R.id.quest);
        comment = (TextView) findViewById(R.id.comment);
        comment.setTypeface(tf);
        text_search = (Button) findViewById(R.id.text_search);
        map_search = (Button) findViewById(R.id.map_search);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        search = (TextView) findViewById(R.id.textView7);
        search.setTypeface(tf);

        map = (TextView) findViewById(R.id.textView6);
        map.setTypeface(tf);
        bookmark = (TextView) findViewById(R.id.textView8);
        bookmark.setTypeface(tf);
        adapter = new Bookmark_Adapter();
        comment.setText("이 App은 비영리를 목적으로 합니다.");


    }


    /*각 뷰의 리스너를 설정하는 메소드*/
    public void set_Listener() {


        TextView textView = (TextView) findViewById(R.id.recomm);
        textView.setTypeface(tf);
        map_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeThread = new TimeThread();
                timeThread.execute();
                if (!check_first) {
                    check_first = !check_first;
                    Toast.makeText(getApplicationContext(), "최초 실행 시, 조금 더 기다려주세요.", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(MainAct.this, MapSearch_Activity.class);
                startActivity(intent);

            }
        });
        text_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainAct.this, Condition_Search.class);
                startActivity(intent);
            }
        });

        book = (Button) findViewById(R.id.book);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Bookmark.class);
                startActivity(intent);

            }
        });
        getpref_bookmark(this, "book");
        quest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainAct.this, QnA.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onBackPressed() {


        AlertDialog.Builder d = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog);
        d.setTitle("종료하시겠습니까?");
        d.setPositiveButton("예", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // process전체 종료
                finish();
            }
        });
        d.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        d.show();
    }

    /* 즐겨찾기 , 어플이 종료되거나 시작되면 나의 즐겨찾기를 불러옴*/
    //즐겨찾기
    private void setpref_bookmark(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.putBoolean("check_first", check_first);
        editor.apply();


    }

    private void getpref_bookmark(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    MainAct.adapter.addItem(url);
                    MainAct.adapter.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        check_first = prefs.getBoolean("check_first", false);

    }


    /*랜덤으로 선택 된 추천 카페이름들을 받아서 리스트에 저장하는 클래스(스레드)*/
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

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setDoOutput(true);
                conn.setDoInput(true);

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
        }

        private void showResult(final String result) {
            try {
                mArrayList = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);
                    String name = item.getString(TAG_NAME);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(TAG_NAME, name);

                    mArrayList.add(hashMap);
                }
                set_rand();
                set_Listener();

                viewPager.setAdapter(new pagerAdapter(getSupportFragmentManager(), name) {
                });
                viewPager.setCurrentItem(0);
            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }
        }

    }

    @Override
    protected void onPause() {
        setpref_bookmark(this, "book", adapter.get_List());
        super.onPause();
    }

    /*뷰 페이저에 띄울 프래그먼트를 선택하는 클래스*/
    private class pagerAdapter extends FragmentStatePagerAdapter {
        String[] name;

        public pagerAdapter(android.support.v4.app.FragmentManager fm, String[] name) {
            super(fm);
            this.name = name;

        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Viewpager_Fragment(name[0], 1);
                case 1:
                    return new Viewpager_Fragment(name[1], 2);
                case 2:
                    return new Viewpager_Fragment(name[2], 3);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return COUNT;
        }
    }

    /*전체 카페 리스트 중 랜덤으로 3곳을 선정하는 메소드*/
    public void set_rand() {
        Random random = new Random();
        int[] rand = new int[3];
        boolean check;
        for (int i = 0; i < 3; i++) {
            int temp = random.nextInt(mArrayList.size());
            check = true;
            for (int j = 0; j < i; j++) {

                if (rand[j] == temp) {
                    // arr배열의 방은 다 비어있는 상태이고 위에서 nextInt를 해야 하나씩 들어갑니다.
                    // 그러므로 i의 값만큼 배열에  들어가있는 것입니다.
                    // for문을 돌리면서  방금 받은 random값과 배열에 들어있는 값들을 비교하여 같은게 있으면
                    i--;    // i의 값을 하나 줄여 한 번 더 돌게 합니다.
                    check = false;    // 목적과는 다르게 같은 값이 나왔으므로 cheak를 false로 만듭니다.
                }

            }
            if (check) {
                rand[i] = temp;
            }
            // str_name[i] = mArrayList.get(3).get("name");
            name[i] = mArrayList.get(rand[i]).get("name");
            Log.i("랜덤으로 선택한 값은", "" + mArrayList.get(rand[i]).get("name"));
        }
    }
/* ----------------------------------  */

    /*프로그레스 바 다이럴로그*/
    class TimeThread extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainAct.this);
            dialog.setMessage("지도를 불러오고 있습니다....");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}