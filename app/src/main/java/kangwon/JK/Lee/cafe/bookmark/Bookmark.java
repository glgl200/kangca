package kangwon.JK.Lee.cafe.bookmark;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import kangwon.JK.Lee.cafe.expain.Explain_Activity;
import kangwon.JK.Lee.cafe.Main.MainAct;
import kangwon.JK.Lee.cafe.R;

/**
 * Created by ksdy2 on 2017-07-31.
 */

public class Bookmark extends Activity {
    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bookmark_listview);
        listView = (ListView) findViewById(R.id.book_list);

        listView.setAdapter(MainAct.adapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    Intent intent = new Intent(getApplicationContext(), Explain_Activity.class);
                    intent.putExtra("name", parent.getItemAtPosition(position).toString());
                    startActivity(intent);
                    finish();

            }


        });

    }

}

