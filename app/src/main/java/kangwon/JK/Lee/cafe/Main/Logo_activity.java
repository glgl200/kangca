package kangwon.JK.Lee.cafe.Main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;

import kangwon.JK.Lee.cafe.R;

public class Logo_activity extends AppCompatActivity {
    String deviceVersion;
    String storeVersion;
    Intent intent;
    private BackgroundThread mBackgroundThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo);


        intent = new Intent(this, MainAct.class);
        mBackgroundThread = new BackgroundThread();
        mBackgroundThread.start();


    }


    public class BackgroundThread extends Thread {
        @Override
        public void run() {

            // 패키지 네임 전달
            storeVersion = MarketVersionChecker.getMarketVersion(getPackageName());

            // 디바이스 버전 가져옴
            try {
                deviceVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            deviceVersionCheckHandler.sendMessage(deviceVersionCheckHandler.obtainMessage());
            // 핸들러로 메세지 전달
        }
    }

    private final DeviceVersionCheckHandler deviceVersionCheckHandler = new DeviceVersionCheckHandler(this);

    // 핸들러 객체 만들기
    private static class DeviceVersionCheckHandler extends Handler {
        private final WeakReference<Logo_activity> mainActivityWeakReference;

        public DeviceVersionCheckHandler(Logo_activity mainActivity) {
            mainActivityWeakReference = new WeakReference<Logo_activity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            Logo_activity activity = mainActivityWeakReference.get();
            if (activity != null) {

                try {
                    activity.handleMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 핸들메세지로 결과값 전달
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 0) {
            finish();
            startActivity(intent);
            finish();

        }

        finish();
    }

    private void handleMessage(Message msg) throws InterruptedException {
        //핸들러에서 넘어온 값 체크
        Double device = Double.parseDouble(deviceVersion);
        Log.i("sdf",""+device);

        Double store = Double.parseDouble(storeVersion);

        if (store > device) {
            // 업데이트 필요

            AlertDialog.Builder d = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog);
            d.setTitle("업데이트");
            d.setMessage("업데이트를 하시겠습니까?\n더 나은버전으로 업데이트하세요.");
            d.setPositiveButton("예", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);

                    startActivityForResult(it, 0);
                }
            });
            d.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                    finish();

                }
            });
            d.show();

        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(intent);
            finish();
        }


    }
}
