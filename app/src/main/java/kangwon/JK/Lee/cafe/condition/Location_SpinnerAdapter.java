package kangwon.JK.Lee.cafe.condition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import kangwon.JK.Lee.cafe.font.BaseActivity;

/**
 * Created by ksdy2 on 2017-07-31.
 */

public class Location_SpinnerAdapter extends ArrayAdapter<String> {
    Context context;
    String[] items = new String[] {};

    public Location_SpinnerAdapter(final Context context,
                                   final int textViewResourceId, final String[] objects) {
        super(context, textViewResourceId, objects);
        this.items = objects;
        this.context = context;
    }




    /**
     * 스피너 클릭시 보여지는 View의 정의
     */
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(
                    android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView tv = (TextView) convertView
                .findViewById(android.R.id.text1);
        tv.setText(items[position]);
        tv.setTypeface(BaseActivity.mTypeface);
        tv.setTextSize(20);
        tv.setLetterSpacing((float)0.2);
        return convertView;
    }

    /**
     * 기본 스피너 View 정의
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(
                    android.R.layout.simple_spinner_item, parent, false);
        }

        TextView tv = (TextView) convertView
                .findViewById(android.R.id.text1);
        tv.setText(items[position]);
        tv.setTypeface(BaseActivity.mTypeface);
        tv.setTextSize(20);
        tv.setLetterSpacing((float)0.2);
        return convertView;
    }
}

