package kangwon.JK.Lee.cafe.expain.Room;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kangwon.JK.Lee.cafe.R;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by ksdy2 on 2017-08-24.
 */

public class Clickroom_Viewpager extends Fragment {
    int position;//현재 포지션
    int total_count;
    ArrayList<Bitmap> list;

    public Clickroom_Viewpager() {
    }

    public Clickroom_Viewpager(ArrayList<Bitmap> list, int total_count, int counting) {
        this.list=list;
        this.total_count=total_count;
            position = counting;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_pager, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.menu_imgfragment);
        imageView.setImageBitmap(list.get(position));
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView);
       photoViewAttacher.setScaleType(ImageView.ScaleType.FIT_XY);
        //photoViewAttacher.setScale(500);
        TextView textView = (TextView) view.findViewById(R.id.count);
        textView.setText(position + 1 + " / " + total_count);
        return view;
    }
}
