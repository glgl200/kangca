package kangwon.JK.Lee.cafe.expain.Menu;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import kangwon.JK.Lee.cafe.R;

/**
 * Created by ksdy2 on 2017-07-29.
 */

public class Menu_Fragment extends Fragment {
    ImageView img;
    ViewPager viewPager;
    String str_name;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        str_name = getArguments().getString("name");


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.room_fragment, container, false);
        img =(ImageView)view.findViewById(R.id.first_picture);

        try {
            str_name= URLEncoder.encode(str_name,"euc-kr").replace("%3F","");
            String menu =URLEncoder.encode("메뉴","euc-kr").replace("%3F","");

            Glide.with(getContext()).load("http://ksdy200.cafe24.com/"+str_name+"/"+menu+"/1.jpg").signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).into(img);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Click_Menu_Activity.class);
                intent.putExtra("cafename",str_name);
                startActivity(intent);
            }
        });
        return view;
    }
}