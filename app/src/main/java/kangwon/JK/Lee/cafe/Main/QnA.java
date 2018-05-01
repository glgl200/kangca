package kangwon.JK.Lee.cafe.Main;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import java.net.URL;

import kangwon.JK.Lee.cafe.R;
import kangwon.JK.Lee.cafe.font.BaseActivity;

/**
 * Created by ksdy2 on 2017-08-08.
 */

public class QnA extends BaseActivity {

    private EditText sub, comment;
    private Button trans, cancel;

    private TextView admin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_page);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//소프트키가 호출될 때 , 액티비티 사이즈를 재조정
        sub = (EditText) findViewById(R.id.sub);
        comment = (EditText) findViewById(R.id.comment);
        trans = (Button) findViewById(R.id.trans);
        cancel = (Button) findViewById(R.id.cancel);
        set_Listener();
    }


    public void set_Listener() {

        trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = sub.getText().toString();
                String main_comm = comment.getText().toString();
                Send send = new Send();
                send.execute(subject, main_comm);

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    class Send extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            s = s.trim();
            if (s=="1") {
                Toast.makeText(getApplicationContext(), "문의사항을 접수했습니다.", Toast.LENGTH_SHORT).show();
                finish();

            } else {
                Toast.makeText(getApplicationContext(), "오류가 생겼습니다. 조속하게 수정하도록 하겠습니다.", Toast.LENGTH_SHORT).show();

            }
        }

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
                URL url = new URL("http://ksdy200.cafe24.com/send.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                String postdata = "subject=" + params[0] + " & " + "comment=" + params[1];
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postdata.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                String result = readStream(conn.getInputStream());
                Log.i("result",""+result);
                conn.disconnect();
                return result.trim();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
