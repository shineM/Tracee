package com.zxy.tracee.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.zxy.tracee.R;
import com.zxy.tracee.adapter.TimelineRecyclerViewAdapter;
import com.zxy.tracee.model.Diary;
import com.zxy.tracee.service.RecordTripService;
import com.zxy.tracee.service.ServiceUtil;
import com.zxy.tracee.util.CommonUtils;

import net.tsz.afinal.FinalDb;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    private static final int REQUET_LOACTION_CODE = 0;
    private static final int[] REQUET__CODE_LIST = {REQUET_LOACTION_CODE};

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @Bind(R.id.timeline_hint_view)
    View timeLineHintView;

    @Inject
    FinalDb finalDb;

    private BottomSheetDialog bottomSheet;
    private Bundle bundle;
    private TimelineRecyclerViewAdapter mTimelineRecyclerViewAdapter;
    private boolean isDataUpdated = false;
    private List<Diary> diaryList;
    private RecyclerView mRecyclerView;
    private PermissonUtil permissonUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnim();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        permissonUtil = new PermissonUtil(this);
        permissonUtil.setPermissionOperation(new PermissonUtil.PermissionOperation() {
            @Override
            public void doIfGranted(int requestCode) {
                startRecordService();
            }
        });
        initToolbar(toolbar);
        initFab(fab);
        initDrawer();
        finalDb = FinalDb.create(this);
        initRecylerView(getDiaryLsit());
        EventBus.getDefault().register(this);
        bundle = savedInstanceState;
    }

    private void startRecordService() {
        Intent intent = new Intent(this, RecordTripService.class);
        startService(intent);
        Toast.makeText(getApplicationContext(), "已开启路线记录", Toast.LENGTH_SHORT).show();
    }

    @TargetApi(21)
    private void setAnim() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
    }

    private void initRecylerView(final List<Diary> list) {
        if (list.size() == 0) {
            timeLineHintView.setVisibility(View.VISIBLE);
        } else {
            timeLineHintView.setVisibility(View.INVISIBLE);
        }
        mTimelineRecyclerViewAdapter = new TimelineRecyclerViewAdapter(this.getApplicationContext(), list);
        mTimelineRecyclerViewAdapter.setTimelineItemClick(new TimelineRecyclerViewAdapter.TimelineItemClick() {
            @Override
            public void onTimelineClick(View view, int position) {

                if (CommonUtils.isFastDoubleClick()) {
                    return;
                } else {
                    Intent intent = new Intent(MainActivity.this, DiaryDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("diary", list.get(position));
                    intent.putExtras(bundle);
                    startOPtionalActivity(intent);
                }
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.timline_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mTimelineRecyclerViewAdapter);
    }

    private void startOPtionalActivity(Intent intent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivity(intent);
        } else {
            startAnimActivity(intent);
        }
    }

    @TargetApi(21)
    private void startAnimActivity(Intent intent) {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, fab, AddDiaryActivity.FAB_TRANSITION);
        startActivity(intent, options.toBundle());
    }

    public void onEvent(Integer event) {
        switch (event) {
            case 1:
                isDataUpdated = true;
                break;
            case 2:

            default:
                break;
        }
    }

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                startMyActivity(item.getItemId());

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void initFab(final FloatingActionButton fab) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddDiaryActivity.class);
                startShareViewActivity(intent, fab);
            }
        });
    }

    private void startShareViewActivity(Intent intent, View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, fab, AddDiaryActivity.FAB_TRANSITION);
            startActivity(intent, options.toBundle());
        } else {
            startKitkatActivity(intent, view);
        }
    }

    @TargetApi(21)
    private void startKitkatActivity(Intent intent, View view) {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, view, AddDiaryActivity.FAB_TRANSITION);
        startActivity(intent, options.toBundle());
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            );
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        toolbar.setTitle("Tracee");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.add_now)
    public void addMore() {
        Intent intent = new Intent(MainActivity.this, AddDiaryActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (ServiceUtil.isServiceRunning(this, "com.zxy.tracee.service.RecordTripService")) {
            menu.getItem(0).setIcon(R.drawable.ic_pause_white_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_record_service) {
            if (ServiceUtil.isServiceRunning(this, "com.zxy.tracee.service.RecordTripService")) {
                Intent intent = new Intent(this, RecordTripService.class);
                stopService(intent);
                item.setIcon(R.drawable.ic_play_arrow_white_24dp);
                Toast.makeText(getApplicationContext(), "已关闭路线记录", Toast.LENGTH_SHORT).show();
            } else {
                permissonUtil.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUET_LOACTION_CODE);
                item.setIcon(R.drawable.ic_pause_white_24dp);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissonUtil.onRequestResultAction(requestCode, permissions, grantResults, REQUET__CODE_LIST);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void startMyActivity(int id) {
        Intent intent;
        switch (id) {
            case R.id.nav_map:
                intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_about:
                intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isDataUpdated) {
            initRecylerView(getDiaryLsit());
            isDataUpdated = false;
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    private List<Diary> getDiaryLsit() {
        List<Diary> list = null;
        list = finalDb.findAllByWhere(Diary.class, "", "date", true);
        return list;
    }

}
