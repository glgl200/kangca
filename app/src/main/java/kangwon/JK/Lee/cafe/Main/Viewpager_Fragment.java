package kangwon.JK.Lee.cafe.Main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import kangwon.JK.Lee.cafe.R;
import kangwon.JK.Lee.cafe.expain.Explain_Activity;


/**
 * Created by ksdy2 on 2017-07-26.
 */

public class Viewpager_Fragment extends Fragment {
    private Bitmap bitmap;
    private ImageView imageView;
    private TextView textView;
    private String name;
    private int num;

    public Viewpager_Fragment() {
        super();
    }

    public Viewpager_Fragment(String name, int num) {
        this.name = name;
        Viewpaer_Thread viewpaer_thread = new Viewpaer_Thread();
        bitmap = viewpaer_thread.thread(name);
        this.num = num;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recommend_pager, container, false);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "custom_font.ttf");
        TextView logo_num = (TextView) view.findViewById(R.id.logo_number);
        logo_num.setTypeface(tf);
        logo_num.setTextColor(Color.BLACK);
        logo_num.setText(num + "/3");
        imageView = (ImageView) view.findViewById(R.id.imageView2);
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        textView = (TextView) view.findViewById(R.id.textView4);
        textView.setTypeface(tf);
        textView.setText("" + name);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Explain_Activity.class);
                intent.putExtra("name", name);
                startActivity(intent);

            }
        });
        return view;
    }
}
