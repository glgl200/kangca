package kangwon.JK.Lee.cafe.expain.Reply;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import kangwon.JK.Lee.cafe.R;
import kangwon.JK.Lee.cafe.font.BaseActivity;

/**
 * Created by ksdy2 on 2017-09-11.
 */

public class Update_Reply extends BaseActivity {
    private String count;
    private String cafename;
    private Button mod, cancel;
    private EditText text;
    private Intent intent;
    private String comment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(Window.FEATURE_NO_TITLE, Window.FEATURE_NO_TITLE);
        setContentView(R.layout.update_reply);

        intent = getIntent();
        count = intent.getStringExtra("count");
        cafename = intent.getStringExtra("cafename");
        comment = intent.getStringExtra("comment");
        set_id();
        set_Listener();

    }

    private void set_id() {
        mod = (Button) findViewById(R.id.modify);
        cancel = (Button) findViewById(R.id.cancel);
        text = (EditText) findViewById(R.id.mod_edit);
        text.setText(comment);
        mod.setTypeface(mTypeface);
        cancel.setTypeface(mTypeface);

    }

    private void set_Listener() {
        mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date dt = new Date();
                SimpleDateFormat curr_date = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a");
                String date = curr_date.format(dt).toString();
                String comment = text.getText().toString();
                Update_DB update_db = new Update_DB(count, cafename, comment, date);
                update_db.execute();
                intent.putExtra("count", count);
                intent.putExtra("cafename", cafename);
                intent.putExtra("comment", comment);
                intent.putExtra("date", date);
                setResult(1, intent);
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public class Update_DB extends AsyncTask<Void, Void, Void> {
        private String count;
        private String cafename;
        private String comment;
        private String date;

        Update_DB(String count, String cafename, String comment, String date) {
            this.count = count;
            this.cafename = cafename;
            this.comment = comment;
            this.date = date;
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

        /*비밀번호로 인증기능 추가해야함*/
        @Override
        protected Void doInBackground(Void... params) {
            try {

                URL url = new URL("http://ksdy200.cafe24.com/update_reply.php");
                String postData = "cafename=" + cafename + " & " + "count=" + count + " & " + "date=" + date + " & " + "comment=" + comment;
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
                Log.i("post데이터", "" + postData);

                Log.i("수정값", "" + result);
                conn.disconnect();
                return null;
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
