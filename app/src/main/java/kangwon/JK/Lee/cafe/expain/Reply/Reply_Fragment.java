package kangwon.JK.Lee.cafe.expain.Reply;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import kangwon.JK.Lee.cafe.R;


/**
 * Created by ksdy2 on 2017-07-25.
 */

public class Reply_Fragment extends Fragment {

    /*한줄평 view*/
    private Button regit;//
    private EditText nick, rp, pwd;
    private ListView listView;
    private Reply_Adapter reply_adapter;
    String cafename;
    private ArrayList<HashMap<String, String>> mArrayList;
    public ArrayList<Reply_entry> mItems = new ArrayList<>();
    private int reply_count = 0;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        cafename = getArguments().getString("name");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reply_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.list);
        reply_adapter = new Reply_Adapter(getContext());
        pwd = (EditText) view.findViewById(R.id.pw);
        nick = (EditText) view.findViewById(R.id.name);
        rp = (EditText) view.findViewById(R.id.comment);
        regit = (Button) view.findViewById(R.id.regit);

        Read_DB read_db = new Read_DB();//최초 해당 카페의 댓글 읽어오기
        read_db.execute();
        set_Listener();


        return view;
    }

    public void set_Listener() {
        regit.setOnClickListener(new View.OnClickListener() {//등록
            @Override
            public void onClick(View v) {
                reply_count++;
                String name = nick.getText().toString();
                String comment = rp.getText().toString();
                String pw = pwd.getText().toString();
                Date dt = new Date();
                SimpleDateFormat curr_date = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a");
                Save_RP save_rp = new Save_RP(name, curr_date.format(dt).toString(), comment, pw, cafename);
                save_rp.execute();
                reply_adapter.clear();
                Read_DB read_db = new Read_DB();
                read_db.execute();
                listView.setSelection(reply_adapter.getCount() - 1);
                nick.setText("");
                rp.setText("");
                pwd.setText("");

            }
        });


        /*댓글 클릭했을때 삭제 OR 수정 여부 물어보기*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String cafename = reply_adapter.getItem(position).getCafename().toString();
                final String number = reply_adapter.getItem(position).getCount().toString();
                final String comment = reply_adapter.getItem(position).getComment().toString();
                final String pwd = reply_adapter.getItem(position).getPw().toString();

                final AlertDialog.Builder d = new AlertDialog.Builder(getContext());
                d.setTitle("삭제 OR 수정");
                d.setMessage("본인 댓글의 비밀번호를 입력하세요.");

                final EditText et = new EditText(getContext());
                et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
                et.setTransformationMethod(PasswordTransformationMethod.getInstance());

                d.setView(et);
                d.setPositiveButton("삭제", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {


                        if (et.getText().toString().trim().equals(pwd.trim())) {
                            Delet_DB delet_db = new Delet_DB(cafename, number);
                            delet_db.execute();
                            reply_adapter.clear();
                            reply_adapter.notifyDataSetChanged();
                            Read_DB read_db = new Read_DB();
                            read_db.execute();
                        } else {
                            Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                d.setNegativeButton("수정", new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int which) {
                        if (et.getText().toString().trim().equals(pwd.trim())) {
                            Intent intent = new Intent(getContext(), Update_Reply.class);
                            intent.putExtra("count", number);
                            intent.putExtra("cafename", cafename);
                            intent.putExtra("comment", comment);
                            startActivityForResult(intent, 1);
                        } else {
                            Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                final AlertDialog dialog = d.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onShow(DialogInterface arg0) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.BLACK);
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.BLACK);
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);

                    }
                });
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();

                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                params.height= WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setAttributes(params);

                dialog.show();

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode != 0) {

            String cafename = data.getStringExtra("cafename");
            String count = data.getStringExtra("count");
            String comment = data.getStringExtra("comment");
            String date = data.getStringExtra("date");
            reply_adapter.update_adapter(count, cafename, comment, date);
            reply_adapter.notifyDataSetChanged();

        }
    }


    public class Delet_DB extends AsyncTask<Void, Void, Void> {
        private String count;
        private String cafename;

        Delet_DB(String cafename, String count) {
            this.count = count;
            this.cafename = cafename;
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
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL("http://ksdy200.cafe24.com/delete_reply.php");
                String postData = "cafename=" + cafename + " & " + "count=" + count;
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

    public class Read_DB extends AsyncTask<String, Void, String> {


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
                URL url = new URL("http://ksdy200.cafe24.com/read_reply.php");
                String postData = "cafename=" + cafename;
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

                return result;
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

        @Override
        protected void onPostExecute(String result) {
            showResult(result);
            listView.setSelection(reply_adapter.getCount() - 1);
            super.onPostExecute(result);
        }

        private void showResult(final String result) {

            try {
                mArrayList = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("replylist");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    String rownum = item.getString("rownum");
                    String count = item.getString("count");
                    String name = item.getString("name");
                    String curr_date = item.getString("date");
                    String comment = item.getString("comment");
                    String cafename = item.getString("cafename");
                    String pwd = item.getString("pwd");
                    reply_count = Integer.parseInt(rownum);
                    reply_adapter.add_item(rownum, name, curr_date, comment, cafename, count, pwd);

                }
                reply_adapter.notifyDataSetChanged();
                listView.setAdapter(reply_adapter);

            } catch (JSONException e) {
            }
        }
    }

    public class Save_RP extends AsyncTask<Void, Void, Void> {
        private String name;
        private String comment;
        private String date;
        private String cafename;
        private String pwd;

        public Save_RP(String name, String date, String comment, String pwd, String cafename) {
            this.name = name;
            this.comment = comment;
            this.pwd = pwd;
            this.date = date;
            this.cafename = cafename;

        }

        @Override
        protected void onPreExecute() {
            mItems = reply_adapter.get_list();

            super.onPreExecute();
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
        protected Void doInBackground(Void... params) {

            try {
                URL url = new URL("http://ksdy200.cafe24.com/save_reply.php");
                String postData = "name=" + name +
                        " & " + "date=" + date +
                        " & " + "comment=" + comment +
                        " & " + "pwd=" + pwd +
                        " & " + "cafename=" + cafename;

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setDoOutput(true);
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postData.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                String result = readStream(conn.getInputStream());

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }
    }
}
