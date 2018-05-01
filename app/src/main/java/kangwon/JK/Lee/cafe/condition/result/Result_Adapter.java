package kangwon.JK.Lee.cafe.condition.result;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kangwon.JK.Lee.cafe.R;
import kangwon.JK.Lee.cafe.font.BaseActivity;

/**
 * Created by ksdy2 on 2017-07-31.
 */

public class Result_Adapter extends BaseAdapter {


    private Context mcontext = null;
    public  ArrayList<Result_Myitem> mItems = new ArrayList<>();


    public Result_Adapter(Context context) {
        super();
        this.mcontext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Result_Myitem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
   /*뷰 홀더를 통해서, 리스트뷰 크기안에 보이는 아이템들을 배치할때 개수만큼 getView를 호출함*/
   /*위 처럼뷰 홀더를 통해, 이미 호출해서 convertView안에 있으면 , findbyid를 호출하지 않고, 없으면 호출해서 오버헤드를 낮춤*/
        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(mcontext);
            convertView = inflater.inflate(R.layout.result_list_item, null);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.logo);
            viewHolder.s_name = (TextView) convertView.findViewById(R.id.textView_list_name);
            viewHolder.s_loca = (TextView) convertView.findViewById(R.id.textView_list_location);
            viewHolder.s_state = (TextView) convertView.findViewById(R.id.textView_state);
            viewHolder.s_name.setTypeface(BaseActivity.mTypeface);
            viewHolder.s_loca.setTypeface(BaseActivity.mTypeface);
            viewHolder.s_state.setTypeface(BaseActivity.mTypeface);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        Result_Myitem myItem = getItem(position);
        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        viewHolder.s_name.setText(myItem.getName());
        viewHolder.s_loca.setText(myItem.getLocation());
        viewHolder.s_state.setText(myItem.getState());
        String name =viewHolder.s_name.getText().toString();
        /*Glide 를 써서 비트맵 로딩시간 단축*/
        try {
            name= URLEncoder.encode(name,"euc-kr").replace("%3F","");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Glide.with(mcontext)
                .load("http://ksdy200.cafe24.com/"+name+"/"+name+".png")
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(viewHolder.img);
        return convertView;
    }

    public void add_item( String name, String location, String state) {

        Result_Myitem myitem = new Result_Myitem();
        myitem.setName(name);
        myitem.setLocation(location);
        myitem.setState(state);
        mItems.add(myitem);

    }

    public void sort_location() {
        Collections.sort(mItems, new Comparator<Result_Myitem>() {
            public int compare(Result_Myitem obj1, Result_Myitem obj2) {
                // TODO Auto-generated method stub
                return obj1.getLocation().compareToIgnoreCase(obj2.getLocation());
            }
        });

    }

    public void sort_name() {
        Collections.sort(mItems, new Comparator<Result_Myitem>() {
            public int compare(Result_Myitem obj1, Result_Myitem obj2) {
                // TODO Auto-generated method stub
                return obj1.getName().compareToIgnoreCase(obj2.getName());
            }
        });

    }
    public void sort_state() {
        Collections.sort(mItems, new Comparator<Result_Myitem>() {
            public int compare(Result_Myitem obj1, Result_Myitem obj2) {
                // TODO Auto-generated method stub
                return obj2.getState().compareToIgnoreCase(obj1.getState());
            }
        });

    }

    static class ViewHolder {
        ImageView img ;
        TextView s_name;
        TextView s_loca;
        TextView s_state;

    }


}
