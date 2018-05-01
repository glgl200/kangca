package kangwon.JK.Lee.cafe.bookmark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kangwon.JK.Lee.cafe.R;
import kangwon.JK.Lee.cafe.font.BaseActivity;

/**
 * Created by ksdy2 on 2017-08-02.
 */

public class Bookmark_Adapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    public ArrayList<String> listViewItemList = new ArrayList<String>();
    private ViewHolder viewHolder = new ViewHolder();

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();


        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.bookmark_listitem, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.text1);
            viewHolder.title.setTypeface(BaseActivity.mTypeface);
            viewHolder.title.setLetterSpacing((float) 0.2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(listViewItemList.get(position).toString());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String title) {

        listViewItemList.add(title);
    }

    public ArrayList<String> get_List() {
        return listViewItemList;
    }


    public String get_title() {
        return viewHolder.title.getText().toString();
    }

    static class ViewHolder {
        TextView title;
    }
}
