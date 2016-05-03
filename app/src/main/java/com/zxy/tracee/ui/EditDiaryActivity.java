package com.zxy.tracee.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zxy.tracee.R;
import com.zxy.tracee.adapter.SelectPhotoViewAdapter;
import com.zxy.tracee.model.Diary;
import com.zxy.tracee.util.ActivityUtil;
import com.zxy.tracee.util.CommonUtils;
import com.zxy.tracee.util.FileUtil;
import com.zxy.tracee.util.ImageLoadUtil;
import com.zxy.tracee.util.OnImageClick;

import net.tsz.afinal.FinalDb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class EditDiaryActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_PERMISSION_CODE = 1;

    private static final int REQUEST_IMAGE_CODE = 1;

    private static final int REQUEST_CAMERA_CODE = 0;
    public static final String FAB_TRANSITION = "fab_add_transition";
    public static final int GALLERY_EMPTY_EVENT = 0;


    public static final String UPDATED_DATA = "updated data";
    @Bind(R.id.add_diary_title)
    EditText titleText;
    @Bind(R.id.add_diary_content)
    EditText contentText;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.select_photos_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.gallery_hint)
    LinearLayout linearLayout;
    FinalDb finalDb;
    private Diary diary;
    List<String> pictureList;
    File photoFile;
    SelectPhotoViewAdapter selectPhotoViewAdapter;
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);
        ButterKnife.bind(this);
        diary = (Diary) getIntent().getSerializableExtra(DiaryDetailActivity.DIARY_EDIT);
        initToolbar();
        initData();
        finalDb = FinalDb.create(this);
        initFabBtn(fab);
        EventBus.getDefault().register(this);
    }

    private void initData() {
        list = CommonUtils.parsePicString(diary.getPicPath());
        if (diary != null) {
            titleText.setText(diary.getTitle());
            contentText.setText(diary.getContent());
            initGallery();
        }
    }

    private void initGallery() {

        if (null == list || list.size() == 0) {
            linearLayout.setVisibility(View.VISIBLE);
            return;
        }
        linearLayout.setVisibility(View.GONE);
        selectPhotoViewAdapter = new SelectPhotoViewAdapter(this, CommonUtils.getScreenWidth(this), list);
        selectPhotoViewAdapter.setOnImageClick(new OnImageClick() {
            @Override
            public void onClick(String imagePath, View view) {
                viewLargeImage(imagePath, view);
            }
        });
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.setAdapter(selectPhotoViewAdapter);
    }

    private void initToolbar() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        toolbar.setTitle(R.string.edit_diary_title);
        toolbar.inflateMenu(R.menu.menu_edit_done);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_done) {
                    diary.setTitle(titleText.getText().toString());
                    diary.setContent(contentText.getText().toString());

                    if (list == null || list.size() == 0) {
                        diary.setPicPath(null);
                    } else {
                        StringBuffer picPathString = new StringBuffer();
                        String[] picPaths = list.toArray(new String[list.size()]);


                        for (int i = 0; i < picPaths.length; i++) {
                            picPathString.append(picPaths[i] + "|");
                        }
                        diary.setPicPath(picPathString.toString());
                    }
                    diary.setLatitude(diary.getLatitude());

                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(UPDATED_DATA, diary);
                    intent.putExtras(bundle);
                    finalDb.update(diary);
                    setResult(RESULT_OK, intent);
                    EventBus.getDefault().post(1);
                    finish();
                }
                return true;
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void initFabBtn(FloatingActionButton fab) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });
    }

    private void checkPermission() {
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        String[] reqPermissonList = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, reqPermissonList, REQUEST_WRITE_PERMISSION_CODE);
        } else {
            startChooseDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION_CODE: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        startCameraIntent();
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        return;
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private void startChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择添加方式");
        final String[] items = {"拍照", "图库"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    startCameraIntent();
                } else {
                    startLibraryIntent();
                }
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startLibraryIntent() {
        Intent intentChooseLibrary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intentChooseLibrary.resolveActivity(getPackageManager()) != null) {
            intentChooseLibrary.setType("image/*");
            startActivityForResult(intentChooseLibrary, REQUEST_IMAGE_CODE);
        }
    }

    private void startCameraIntent() {
        Intent intentTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentTakePicture.resolveActivity(getPackageManager()) != null) {
            photoFile = FileUtil.createFileByDate();
            if (photoFile != null) {
                intentTakePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                intentTakePicture.putExtra("photoName", photoFile.getAbsolutePath());
                startActivityForResult(intentTakePicture, REQUEST_CAMERA_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            String imageName = null;
            if (requestCode == REQUEST_CAMERA_CODE) {
                imageName = photoFile.getAbsolutePath();

            } else if (requestCode == REQUEST_IMAGE_CODE) {
                Uri selectedImageUri = data.getData();
                imageName = ImageLoadUtil.getPath(selectedImageUri, this);
            }
            File mImage = new File(imageName);
            if (mImage.exists()) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(imageName);
                initGallery();
            }
        }
    }

    private void viewLargeImage(String imagePath, View view) {
        ActivityUtil.startHDImageActivity(this, this, imagePath, view);
    }

    public void onEvent(Integer event) {
        switch (event) {
            case GALLERY_EMPTY_EVENT:
                linearLayout.setVisibility(View.VISIBLE);
        }
    }

}
