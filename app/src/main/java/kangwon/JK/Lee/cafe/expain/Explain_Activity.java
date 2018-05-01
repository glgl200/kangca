package kangwon.JK.Lee.cafe.expain;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kangwon.JK.Lee.cafe.R;
import kangwon.JK.Lee.cafe.Main.MainAct;
import kangwon.JK.Lee.cafe.expain.Detail.Detail_Fragment;
import kangwon.JK.Lee.cafe.expain.Menu.Menu_Fragment;
import kangwon.JK.Lee.cafe.expain.Reply.Reply_Adapter;
import kangwon.JK.Lee.cafe.expain.Reply.Reply_Fragment;
import kangwon.JK.Lee.cafe.expain.Room.Room_Fragment;
import kangwon.JK.Lee.cafe.font.BaseFragment;

/**
 * Created by JK on 2017-07-14.
 */

public class Explain_Activity extends BaseFragment implements View.OnClickListener {

    public static String str_name;// 카페이름
    private Button menu, detail, room, jjim, find_map, reply;//각 프래그먼트 호출 버튼
    private TextView subject;// 제목
    public static String state;
    private boolean check_state = false;

    /*한줄평 view*/
    private Button regit;//
    private EditText nick, rp;
    private ListView listView;
    private Reply_Adapter reply_adapter;

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explain_activity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//소프트키가 호출될 때 , 액티비티 사이즈를 재조정
        Intent intent = getIntent();
        state = intent.getStringExtra("state");
        subject = (TextView) findViewById(R.id.subject);

        find_map = (Button) findViewById(R.id.find_map);
        find_map.setOnClickListener(this);
        detail = (Button) findViewById(R.id.detailFragment);
        room = (Button) findViewById(R.id.room);
        detail.setOnClickListener(this);
        room.setOnClickListener(this);
        menu = (Button) findViewById(R.id.menu);
        menu.setOnClickListener(this);
        reply=(Button)findViewById(R.id.reply);
        reply.setOnClickListener(this);

        str_name = intent.getStringExtra("name"); // 셀렉트 된 카페 이름을  유아이 또는 검색 조건을 사용
        subject.setText("*  " + str_name + "  *");
        jjim = (Button) findViewById(R.id.jjim);

        check_list();
        jjim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (check_state == true) {
                    ArrayList<String> arrayList = MainAct.adapter.get_List();
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i).toString().equals(str_name)) {
                            arrayList.remove(i);
                            break;
                        }
                    }
                    MainAct.adapter.notifyDataSetChanged();
                    jjim.setBackgroundResource(R.drawable.jjim);
                    Toast.makeText(getApplicationContext(), str_name + "이(가) 삭제되었습니다..", Toast.LENGTH_SHORT).show();
                } else {
                    MainAct.adapter.addItem(str_name);
                    jjim.setBackgroundResource(R.drawable.fulljjim);
                    Toast.makeText(getApplicationContext(), str_name + "이(가) 추가되었습니다.", Toast.LENGTH_SHORT).show();
                }
                check_list();

            }
        });



/*초기 프래그먼트 설정*/
        Detail_Fragment detailFragment = new Detail_Fragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("name", str_name);
        detailFragment.setArguments(bundle1);
        FragmentManager fm1 = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction1 = fm1.beginTransaction();
        fragmentTransaction1.replace(R.id.fram_layout, detailFragment);
        fragmentTransaction1.commit();




    }

    public void check_list() {
        ArrayList<String> arrayList=null ;

        check_state = false;
        if(MainAct.adapter.get_List()!=null){
           arrayList = MainAct.adapter.get_List();
        }
        for (int i = 0; i < arrayList.size(); i++) {

            if (arrayList.get(i).toString().equals(str_name)) {
                check_state = true;
                jjim.setBackgroundResource(R.drawable.fulljjim);
                break;
            }
        }

        if (check_state == false) {
            jjim.setBackgroundResource(R.drawable.jjim);

        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detailFragment:
                detail.setBackgroundColor(Color.WHITE);
                detail.setTextColor(Color.BLACK);
                room.setBackgroundColor(Color.BLACK);
                room.setTextColor(Color.WHITE);
                menu.setBackgroundColor(Color.BLACK);
                menu.setTextColor(Color.WHITE);
                find_map.setBackgroundColor(Color.BLACK);
                find_map.setTextColor(Color.WHITE);
                reply.setBackgroundColor(Color.BLACK);
                reply.setTextColor(Color.WHITE);
                Detail_Fragment explain = new Detail_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", str_name);
                explain.setArguments(bundle);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fram_layout, explain);
                fragmentTransaction.commit();
                break;
            case R.id.room:
                detail.setBackgroundColor(Color.BLACK);
                detail.setTextColor(Color.WHITE);
                room.setBackgroundColor(Color.WHITE);
                room.setTextColor(Color.BLACK);
                menu.setBackgroundColor(Color.BLACK);
                menu.setTextColor(Color.WHITE);
                find_map.setBackgroundColor(Color.BLACK);
                find_map.setTextColor(Color.WHITE);
                reply.setBackgroundColor(Color.BLACK);
                reply.setTextColor(Color.WHITE);
                Room_Fragment roomFragment1 = new Room_Fragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString("name", str_name);
                roomFragment1.setArguments(bundle1);
                FragmentManager fm1 = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction1 = fm1.beginTransaction();
                fragmentTransaction1.replace(R.id.fram_layout, roomFragment1);
                fragmentTransaction1.commit();
                break;
            case R.id.menu:
                detail.setBackgroundColor(Color.BLACK);
                detail.setTextColor(Color.WHITE);
                room.setBackgroundColor(Color.BLACK);
                room.setTextColor(Color.WHITE);
                menu.setBackgroundColor(Color.WHITE);
                menu.setTextColor(Color.BLACK);
                find_map.setBackgroundColor(Color.BLACK);
                find_map.setTextColor(Color.WHITE);
                reply.setBackgroundColor(Color.BLACK);
                reply.setTextColor(Color.WHITE);
                Menu_Fragment menuFragment1 = new Menu_Fragment();
                Bundle bundle2 = new Bundle();
                bundle2.putString("name", str_name);
                menuFragment1.setArguments(bundle2);
                FragmentManager fm2 = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction2 = fm2.beginTransaction();
                fragmentTransaction2.replace(R.id.fram_layout, menuFragment1);
                fragmentTransaction2.commit();
                break;
            case R.id.find_map:
                detail.setBackgroundColor(Color.BLACK);
                detail.setTextColor(Color.WHITE);

                room.setBackgroundColor(Color.BLACK);
                room.setTextColor(Color.WHITE);

                menu.setBackgroundColor(Color.BLACK);
                menu.setTextColor(Color.WHITE);

                find_map.setBackgroundColor(Color.WHITE);
                find_map.setTextColor(Color.BLACK);

                reply.setBackgroundColor(Color.BLACK);
                reply.setTextColor(Color.WHITE);

                Intent intent = new Intent(this, View_Map.class);
                Toast.makeText(getApplicationContext(), "잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
                intent.putExtra("name", str_name);
                startActivity(intent);
                break;
            case R.id.reply:
                detail.setBackgroundColor(Color.BLACK);
                detail.setTextColor(Color.WHITE);

                room.setBackgroundColor(Color.BLACK);
                room.setTextColor(Color.WHITE);

                menu.setBackgroundColor(Color.BLACK);
                menu.setTextColor(Color.WHITE);

                find_map.setBackgroundColor(Color.BLACK);
                find_map.setTextColor(Color.WHITE);

                reply.setBackgroundColor(Color.WHITE);
                reply.setTextColor(Color.BLACK);
                Reply_Fragment reply_fragment = new Reply_Fragment();
                Bundle bundle3 = new Bundle();
                bundle3.putString("name", str_name);
                reply_fragment.setArguments(bundle3);
                FragmentManager fm3 = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction3 = fm3.beginTransaction();
                fragmentTransaction3.replace(R.id.fram_layout, reply_fragment);
                fragmentTransaction3.commit();
                break;
        }
    }


}
