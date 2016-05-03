package com.zxy.tracee.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.zxy.tracee.R;
import com.zxy.tracee.util.ImageLoadUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoViewAttacher;

public class HDImageActivity extends AppCompatActivity {
    public static final String TRANSIT_PIC = "picture";
    @Bind(R.id.image_hd)
    ImageView imageView;

    private PhotoViewAttacher mPhotoViewAttacher;

    private String imagePath;

    private LoadImageThread loadImageThread;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    imageView.setImageBitmap((Bitmap) message.obj);
                    mPhotoViewAttacher = new PhotoViewAttacher(imageView);
                    mPhotoViewAttacher.update();
                    break;
                case 1:
                    Toast.makeText(HDImageActivity.this, "图片加载失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private class LoadImageThread extends Thread {

        @Override
        public void run() {
            try {
                Bitmap bitmap = ImageLoadUtil.decodeSampledBitmapFromResource(imagePath, 1000, 1000);
                mHandler.obtainMessage(0, bitmap).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hdimgae);
        initUI();
        ButterKnife.bind(this);
        ViewCompat.setTransitionName(imageView, TRANSIT_PIC);
        Intent intent = this.getIntent();
        imagePath = intent.getStringExtra("image_path");
        loadImageThread = new LoadImageThread();
        loadImageThread.start();
    }

    private void initUI() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            );
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void setupPhotoAttacher() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mPhotoViewAttacher.cleanup();
        super.onDestroy();
    }
}
