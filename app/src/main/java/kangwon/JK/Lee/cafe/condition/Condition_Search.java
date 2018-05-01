package kangwon.JK.Lee.cafe.condition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.Spinner;
import android.widget.TextView;

import kangwon.JK.Lee.cafe.font.BaseActivity;
import kangwon.JK.Lee.cafe.R;
import kangwon.JK.Lee.cafe.condition.result.Result_Search;


/**
 * Created by JK on 2017-07-11.
 */

public class Condition_Search extends BaseActivity {
    private CheckBox C_smoke, C_study;
    private EditText name;
    private Button search_button,all_button;
    private Spinner s_location;//스피너 : 위치/ 구조
    private String Search_name = "";
    private String Search_location = "";
    private String Search_smoke = "";
    private String Search_study = "";
    TextView rec;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(Window.FEATURE_NO_TITLE, Window.FEATURE_NO_TITLE);
        setContentView(R.layout.condition_search);

        /*팝업 크기*/
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = (int) (display.getWidth() * 0.7); //Display 사이즈의 70%
        int height = (int) (display.getWidth()*0.9);  //Display 사이즈의 90%
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = WindowManager.LayoutParams.WRAP_CONTENT;
        name = (EditText) findViewById(R.id.name);//이름검색 에딧

        rec= (TextView)findViewById(R.id.recomm);

        //name.setInputType(0);

        C_smoke = (CheckBox) findViewById(R.id.smoke_check);
        C_study = (CheckBox) findViewById(R.id.study_check);
        s_location = (Spinner) findViewById(R.id.spinner_location);//위치검색 스피너
        String[] arr = {"전체","강대후문","강대정문","자대쪽문","애막골","공대쪽문","팔호광장","축사","교내"};
        Location_SpinnerAdapter locationSpinnerAdapter = new Location_SpinnerAdapter(this,android.R.layout.simple_spinner_item,arr);
        s_location.setAdapter(locationSpinnerAdapter);
        s_location.setPrompt("위치를 선택하세요.");
        search_button = (Button) findViewById(R.id.Search_button);
        setListener();
    }

    public void setListener() {

        /*location 클릭 리스너*/
        s_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                              TextView textView = (TextView)s_location.getSelectedView();
                textView.setTypeface(BaseActivity.mTypeface);
                textView.setLetterSpacing((float) 0.2);
                if (position == 0) {
                    Search_location = "";
                } else {
                    Search_location = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Search_location = "";
            }
        });

        /*흡연실 및 스터디 룸 리스너*/
        C_smoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(C_smoke.isChecked()==true){
                        Search_smoke="Yes";
                    }
                    else{
                        Search_smoke="No";
                    }

            }
        });

        C_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(C_study.isChecked()==true){
                    Search_study="Yes";
                }
                else{
                    Search_study="No";
                }

            }
        });

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search_name = name.getText().toString();
                Intent intent = new Intent(Condition_Search.this,Result_Search.class);
                intent.putExtra("cafename",Search_name);
                intent.putExtra("cafelocation",Search_location);
                intent.putExtra("cafesmoke",Search_smoke);
                intent.putExtra("cafestudy",Search_study);
                startActivity(intent);
                finish();

            }
        });
        all_button= (Button)findViewById(R.id.all);
        all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search_name = name.getText().toString();
                Intent intent = new Intent(Condition_Search.this,Result_Search.class);
                intent.putExtra("cafename","");
                intent.putExtra("cafelocation","");
                intent.putExtra("cafesmoke","");
                intent.putExtra("cafestudy","");
                startActivity(intent);
                finish();
            }
        });
    }

}
