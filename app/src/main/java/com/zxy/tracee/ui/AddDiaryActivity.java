package com.zxy.tracee.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zxy.tracee.R;
import com.zxy.tracee.adapter.SelectPhotoViewAdapter;
import com.zxy.tracee.model.Diary;
import com.zxy.tracee.util.ActivityUtil;
import com.zxy.tracee.util.AnimationUtil;
import com.zxy.tracee.util.FileUtil;
import com.zxy.tracee.util.ImageLoadUtil;
import com.zxy.tracee.util.OnImageClick;
import com.zxy.tracee.util.SpaceItemDecoration;

import net.tsz.afinal.FinalDb;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class AddDiaryActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_PERMISSION_CODE = 1;
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 2;
    private static final int REQUEST_IMAGE_CODE = 1;
    private static final int[] REQUEST_CODE_LIST = {REQUEST_WRITE_PERMISSION_CODE,REQUEST_LOCATION_PERMISSION_CODE};
    private static final int REQUEST_CAMERA_CODE = 0;
    public static final String FAB_TRANSITION = "fab_add_transition";
    public static final int GALLERY_EMPTY_EVENT = 0;


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
    @Bind(R.id.container)
    View view;
    FinalDb finalDb;
    SelectPhotoViewAdapter selectPhotoViewAdapter;
    FileUtil fileUtil;
    File photoFile;
    List<String> imagePathList = new ArrayList<>();
    private int width;
    private Location mLocation;
    PermissonUtil permissonUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);
        ButterKnife.bind(this);
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        width = windowManager.getDefaultDisplay().getWidth();
        setExitAnim();
        finalDb = FinalDb.create(this);
        permissonUtil = new PermissonUtil(this);
        permissonUtil.setPermissionOperation(new PermissonUtil.PermissionOperation() {
            @Override
            public void doIfGranted(int requestCode) {
                doIfPermissionGranted(requestCode);
            }
        });
        fileUtil = new FileUtil(getApplicationContext());
        initToolbar(toolbar);
        initFabBtn(fab);
        permissonUtil.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_LOCATION_PERMISSION_CODE);

        EventBus.getDefault().register(this);
    }

    private void setExitAnim() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            ViewCompat.setTransitionName(view, FAB_TRANSITION);
        } else {
            ViewCompat.setTransitionName(view, FAB_TRANSITION);
            setMoreSmoothAnim();
        }
    }

    @TargetApi(21)
    private void setMoreSmoothAnim() {
        view.setTransitionName(FAB_TRANSITION);

        AnimationUtil.setAnimation(this, view);
    }


    private void initToolbar(Toolbar toolbar) {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        toolbar.setTitle("添加旅行日记");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.collapseActionView();
        toolbar.inflateMenu(R.menu.menu_add_done);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuId = item.getItemId();
                if (menuId == R.id.action_done) {
                    addDiary();
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void addDiary() {
        String title = titleText.getText().toString();
        String content = contentText.getText().toString();
        Date date = new Date();

        Diary diary = new Diary();
        Long unixEpoch = System.currentTimeMillis();
        if (mLocation != null) {
            diary.setLatitude(mLocation.getLatitude());
            diary.setLongitude(mLocation.getLongitude());
        } else {
            Toast.makeText(getApplicationContext(), R.string.location_null_warn, Toast.LENGTH_SHORT)
                    .show();
        }
        diary.setTitle(title);
        diary.setContent(content);
        diary.setDate(date);
        diary.setLastChangeDate(unixEpoch);

        String[] picPaths = getList();
        if (picPaths != null && selectPhotoViewAdapter != null) {
            StringBuffer picPathString = new StringBuffer();
            for (int i = 0; i < picPaths.length; i++) {
                picPathString.append(picPaths[i] + "|");
            }
            diary.setPicPath(picPathString.toString());
        }
        finalDb.save(diary);
        EventBus.getDefault().post(1);
        finish();
    }

    private void viewLargeImage(String imagePath, View view) {
        ActivityUtil.startHDImageActivity(this, this, imagePath, view);
    }

    private Location getLocation() {
        Location location;
        LocationManager loctionManager;
        String contextService = Context.LOCATION_SERVICE;
        loctionManager = (LocationManager) getSystemService(contextService);

        String provider = getLocationProvider(loctionManager);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (locationPermission == PackageManager.PERMISSION_GRANTED) {
            location = loctionManager.getLastKnownLocation(provider);
        } else {
            location = null;
        }
        return location;
    }

    private String getLocationProvider(LocationManager manager) {
        Criteria cri = new Criteria();
        cri.setAccuracy(Criteria.ACCURACY_FINE);
        cri.setPowerRequirement(Criteria.POWER_HIGH);
        cri.setSpeedRequired(true);
        cri.setCostAllowed(true);
        cri.setAltitudeRequired(false);
        cri.setBearingRequired(false);
        String lp = manager.getBestProvider(cri, true);
        return lp;
    }

    private void initFabBtn(FloatingActionButton fab) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissonUtil.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_PERMISSION_CODE);
            }
        });
    }

    private void doIfPermissionGranted(int requestCode) {
        if (requestCode == REQUEST_WRITE_PERMISSION_CODE) {
            startChooseDialog();
        } else if (requestCode == REQUEST_LOCATION_PERMISSION_CODE) {
            mLocation = getLocation();
        }
    }

    private void initPhotoRecyclerView(Context context, final RecyclerView mRecyclerView) {
        if (imagePathList.size() != 0) {
            linearLayout.setVisibility(View.GONE);
        }
        selectPhotoViewAdapter = new SelectPhotoViewAdapter(context, width, imagePathList);
        selectPhotoViewAdapter.setOnImageClick(new OnImageClick() {
            @Override
            public void onClick(String imagePath, View view) {
                viewLargeImage(imagePath, view);
            }
        });
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(0));
        mRecyclerView.setAdapter(selectPhotoViewAdapter);
    }

    public String[] getList() {
        if (selectPhotoViewAdapter != null) {
            List<String> list = selectPhotoViewAdapter.getmList();
            return list.toArray(new String[list.size()]);
        } else {
            return null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onEvent(Integer event) {
        switch (event) {
            case GALLERY_EMPTY_EVENT:
                linearLayout.setVisibility(View.VISIBLE);
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
                imagePathList.add(imageName);
                initPhotoRecyclerView(this, mRecyclerView);
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

    private void startLibraryIntent() {
        Intent intentChooseLibrary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intentChooseLibrary.resolveActivity(getPackageManager()) != null) {
            intentChooseLibrary.setType("image/*");
            startActivityForResult(intentChooseLibrary, REQUEST_IMAGE_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        permissonUtil.onRequestResultAction(requestCode,permissions,grantResults,REQUEST_CODE_LIST);

    }

}
