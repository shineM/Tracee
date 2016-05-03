package com.zxy.tracee.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zxy.tracee.R;
import com.zxy.tracee.adapter.DetailImagesGridAdapter;
import com.zxy.tracee.model.Diary;
import com.zxy.tracee.util.ActivityUtil;
import com.zxy.tracee.util.CommonUtils;
import com.zxy.tracee.util.OnImageClick;
import com.zxy.tracee.util.SpaceItemDecoration;

import net.tsz.afinal.FinalDb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class DiaryDetailActivity extends AppCompatActivity {

    public static final String DIARY_EDIT = "edit";
    private static final int EDIT_DONE = 0;
    public static final String DIARY_LATLNG_INFO = "diary_latlng";
    public static final String DIARY_LAT_INFO = "lat";
    public static final String DIARY_LNG_INFO = "lng";

    @Bind(R.id.diary_content_text)
    TextView contentView;

    @Bind(R.id.diary_title_text)
    TextView titleView;

    @Bind(R.id.location_inf)
    TextView locationText;

    @Bind(R.id.date_info)
    TextView dateText;

    @Bind(R.id.location_view)
    View locationView;

    @Bind(R.id.date_view)
    View view;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.image_detail_list)
    RecyclerView recyclerView;

//    @Bind(R.id.fab)
//    FloatingActionButton fab;

    FinalDb finalDb;

    Diary diary;
    String imagePath;
    List<String> imageList;
    String[] imageArray;
    int width;
    DetailImagesGridAdapter adapter;
    private GridLayoutManager grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnim();

        setContentView(R.layout.activity_diary_detail);
        ButterKnife.bind(this);

        width = CommonUtils.getScreenWidth(this);
        finalDb = FinalDb.create(this);
        Intent intent = this.getIntent();
        diary = (Diary) intent.getSerializableExtra("diary");
        initData();
        initToolBar();


    }

    @TargetApi(21)
    private void setAnim() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide(Gravity.BOTTOM));

    }

    private void initData() {
        if (diary == null) {
            return;
        }
        String title = diary.getTitle();
        String content = diary.getContent();
        Double latitude = diary.getLatitude();
        Double longitude = diary.getLongitude();
        Date date = diary.getDate();
        String dateString = CommonUtils.getFormatedDateString(date);

        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            contentView.setText(content);
        }
        locationText.setText(latitude + "°," + longitude + "°");
        dateText.setText(dateString);
        imagePath = diary.getPicPath();
        imageList = CommonUtils.parsePicString(imagePath);
        initImagesGrid(this);
    }

    @OnClick(R.id.location_view)
    public void onLocationClick() {
        Intent intent = new Intent(DiaryDetailActivity.this, MapActivity.class);

        intent.putExtra(MapActivity.INTENT_FLAG, MapActivity.CHECK_DIARY_LOCATION);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MapActivity.DIARY_BUNDLE, diary);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.date_view)
    public void onDateClick() {

    }

    private void initImagesGrid(Context context) {
        if (imagePath == null || imagePath.equals("")) {
            return;
        }
        adapter = new DetailImagesGridAdapter(context, width, imageList);
        adapter.setOnImageClick(new OnImageClick() {
            @Override
            public void onClick(String imagePath, View view) {
                viewLargeImage(imagePath, view);
            }
        });
        grid = new GridLayoutManager(this, adapter.getRowCount(imageList.size()));

        recyclerView.setLayoutManager(grid);
        recyclerView.addItemDecoration(new SpaceItemDecoration(0));
        recyclerView.setAdapter(adapter);
    }

    private void viewLargeImage(String imagePath, View view) {
        ActivityUtil.startHDImageActivity(this, this, imagePath, view);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_diary_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_DONE && resultCode == RESULT_OK) {
            diary = (Diary) data.getSerializableExtra(EditDiaryActivity.UPDATED_DATA);
            initData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        if (menuId == R.id.diary_edit_menu) {
            Intent intent = new Intent(this, EditDiaryActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(DIARY_EDIT, diary);
            intent.putExtras(bundle);
            startActivityForResult(intent, EDIT_DONE);
        } else if (menuId == R.id.diary_delete_menu) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.confirm_delete);
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                        case DialogInterface.BUTTON_POSITIVE:
                            finalDb.delete(diary);
                            EventBus.getDefault().post(1);
                            finish();
                            break;
                        default:
                            break;
                    }
                }
            };
            builder.setPositiveButton(R.string.ok, listener);
            builder.setNegativeButton(R.string.cancel, listener);
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolBar() {
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
        String title;
        if (null == diary.getTitle() || "".equals(diary.getTitle())) {
            title = getText(R.string.titleStart) + " " + CommonUtils.getMnDString(diary.getDate());
        } else {
            title = diary.getTitle();
        }
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

}
