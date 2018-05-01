package kangwon.JK.Lee.cafe.expain.Room;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
 * Created by ksdy2 on 2017-07-25.
 */


public class Room_Fragment extends Fragment {

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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.i("유알엘",""+"http://ksdy200.cafe24.com/"+str_name+"/1.jpg");
        Glide.with(getContext()).load("http://ksdy200.cafe24.com/"+str_name+"/1.jpg").signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).into(img);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Click_Room_Activity.class);
                intent.putExtra("cafename",str_name);
                startActivity(intent);
            }
        });
        return view;
    }


}
