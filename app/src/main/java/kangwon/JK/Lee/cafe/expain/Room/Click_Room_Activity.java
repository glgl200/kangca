package kangwon.JK.Lee.cafe.expain.Room;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import kangwon.JK.Lee.cafe.R;
import kangwon.JK.Lee.cafe.font.BaseFragment;

/**
 * Created by ksdy2 on 2017-08-24.
 */

public class Click_Room_Activity extends BaseFragment {
    private int count_img = 0;
    private static ViewPager viewPager;
    private ArrayList<Bitmap> list = new ArrayList<>();
    private String name = null;
    private int heigh;//디스플레이 높이
    private int width;//가로

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.click_room);
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = (int) (display.getWidth()); //Display 사이즈의 50%
        int height = (int) (display.getHeight() * 0.7);  //Display 사이즈의 50%
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;


        Intent intent = getIntent();

        name = intent.getStringExtra("cafename");

        Read_img read_img = new Read_img();
        read_img.execute();


    }


    class Read_img extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            for (int i = 1; i <= 10; i++) {
                try {
                    Bitmap bitmap = Glide.with(getApplicationContext())
                            .load("http://ksdy200.cafe24.com/" + name + "/" + i + ".jpg")
                            .asBitmap().signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).into(300, 300).get();
                    if (bitmap != null) {
                        list.add(bitmap);
                        count_img++;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), list, count_img));
            viewPager.setCurrentItem(0);
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    static class PageAdapter extends FragmentPagerAdapter {
        private ArrayList<Bitmap> list;
        private int total;

        public PageAdapter(FragmentManager fm, ArrayList<Bitmap> list, int total) {
            super(fm);
            this.list = list;
            this.total = total;
        }

        @Override
        public Fragment getItem(int position) {
            return new Clickroom_Viewpager(list, total, position);
        }

        @Override
        public int getCount() {
            return total;
        }
    }


}
