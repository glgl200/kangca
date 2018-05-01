package kangwon.JK.Lee.cafe.expain.Reply;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kangwon.JK.Lee.cafe.R;

/**
 * Created by ksdy2 on 2017-07-31.
 */

public class Reply_Adapter extends BaseAdapter {


    private Context mcontext = null;
    public ArrayList<Reply_entry> mItems = new ArrayList<>();

    public Reply_Adapter(Context context) {
        super();

        this.mcontext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Reply_entry getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
   /*뷰 홀더를 통해서, 리스트뷰 크기안에 보이는 아이템들을 배치할때 개수만큼 getView를 호출함*/
   /*위 처럼뷰 홀더를 통해, 이미 호출해서 convertView안에 있으면 , findbyid를 호출하지 않고, 없으면 호출해서 오버헤드를 낮춤*/
        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(mcontext);
            convertView = inflater.inflate(R.layout.reply_listitem, null);
            viewHolder.s_name = (TextView) convertView.findViewById(R.id.nickname);
            viewHolder.s_name.setTextColor(Color.BLACK);
            viewHolder.s_rp = (TextView) convertView.findViewById(R.id.comment);
            viewHolder.s_rp.setTextColor(Color.BLACK);
            viewHolder.count = (TextView) convertView.findViewById(R.id.count);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        Reply_entry myItem = getItem(position);
        viewHolder.count.setText(myItem.getRownum() + "번째 ");
        viewHolder.date.setText(myItem.getDate());
        viewHolder.s_name.setText(myItem.getNickname().toString());
        viewHolder.s_rp.setText(myItem.getComment().toString());
        return convertView;
    }


    public void add_item(String rownum, String name, String date, String comment, String cafename,String count,String pwd) {
        Reply_entry myitem = new Reply_entry();
        myitem.setRownum(rownum);
        myitem.setNickname(name);
        myitem.setPw(pwd);
        myitem.setComment(comment);
        myitem.setCount(count);
        myitem.setDate(date);
        myitem.setCafename(cafename);
        mItems.add(myitem);

    }

    public void clear(){
        mItems.clear();
    }
    public ArrayList<Reply_entry> get_list() {
        return mItems;
    }

    public void update_adapter(String count,String cafename,String comment,String date){
        int size=mItems.size();
        for(int i=0; i<size; i++){
            if(mItems.get(i).getCafename().equals(cafename) && mItems.get(i).getCount().equals(count)){
                mItems.get(i).setDate(date);
                mItems.get(i).setComment(comment);
            }
        }
    }

    static class ViewHolder {
        TextView count;
        TextView date;
        TextView s_name;
        TextView s_rp;

    }


}
