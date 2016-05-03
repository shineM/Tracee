package com.zxy.tracee.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.cocosw.bottomsheet.BottomSheet;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.zxy.tracee.R;
import com.zxy.tracee.model.Diary;
import com.zxy.tracee.model.RoutePoint;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION_CODE = 1;
    public static final String INTENT_FLAG = "intent_flag";
    public static final int CHECK_DIARY_LOCATION = 1;
    public static final String DIARY_BUNDLE = "diary";
    public static final String ONE_DIARY = "one diary";
    private static final String MAP_ELEMENTS_TRANSITION = "map_transition";
    public static final String DIARY_WANT_QUERY = "diary want query";
    public static final int VIEW_TODAY = 2;
    private static final int REQUEST_IF_CHECK_ALL = 3;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.mBaiduMapView)
    MapView mapView;

    @Bind(R.id.map_layout)
    RelativeLayout mapParentView;

    @Bind(R.id.search_view)
    MaterialSearchView searchView;


    private BDLocation mLocation;
    private LatLng latLng;
    private LocationClient locationClient;
    private BDLocationListener locationListener;
    private BaiduMap mBaiduMap;
    private String mAddr;
    private boolean isFirstIn = true;
    private double mLatitude;
    private double mLongitude;

    private Context context;
    private Intent intent;
    private int width;
    private int height;
    private View invisibleCenterView;
    private FinalDb finalDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        context = this;
        intent = getIntent();
        width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        finalDb = FinalDb.create(this);
        initToolBar(toolbar);
        initMapView();
        addInvisibleCenterView();
        handleIntent();

//        checkPermission();
    }


    private void handleIntent() {
        setOnMarkerListener();

        int intentFlag = intent.getIntExtra(INTENT_FLAG, 0);
        if (intentFlag == CHECK_DIARY_LOCATION) {
            handleCheckDiary();
        }

    }

    //添加虚拟的share element
    private void addInvisibleCenterView() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(0, 0);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);

        mapParentView.addView(LayoutInflater.from(context).inflate(R.layout.icon_location, null), lp);
        invisibleCenterView = findViewById(R.id.view_for_transition);
    }

    //查看当前Tracee的位置信息
    private void handleCheckDiary() {
        Diary diary = (Diary) intent.getSerializableExtra(DIARY_BUNDLE);
        LatLng latLng = new LatLng(diary.getLatitude(), diary.getLongitude());
        centerToLocation(latLng);
        addMarker(diary);
    }

    private void addMarker(Diary diary) {
        LatLng latLng = new LatLng(diary.getLatitude(), diary.getLongitude());
        OverlayOptions options = new MarkerOptions()
                .position(latLng)
                .zIndex(1)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon));

        Marker mMarker = (Marker) mBaiduMap.addOverlay(options);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ONE_DIARY, diary);
        mMarker.setExtraInfo(bundle);
    }

    private void setOnMarkerListener() {
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                final Diary d = (Diary) marker.getExtraInfo().get(ONE_DIARY);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(new LatLng(d.getLatitude(), d.getLongitude()));
                mBaiduMap.animateMapStatus(msu);

                Intent intent = new Intent(MapActivity.this, PreviewDiaryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(PreviewDiaryActivity.DIARY_BUNDLE, d);
                intent.putExtras(bundle);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MapActivity.this, invisibleCenterView, MAP_ELEMENTS_TRANSITION);
                startActivityForResult(intent, REQUEST_IF_CHECK_ALL, optionsCompat.toBundle());

                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IF_CHECK_ALL && resultCode == RESULT_OK) {
            Diary diary = (Diary) data.getSerializableExtra(DIARY_WANT_QUERY);
            initTheSameDayDiary(diary);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //加载当天所有记录
    private void initTheSameDayDiary(Diary diary) {
        List<Diary> allDayList = finalDb.findAllByWhere(Diary.class, "lastChangeDate between '" + (diary.getLastChangeDate() - 43200000) + "' and '" + (diary.getLastChangeDate() + 43200000) + "'");
        for (Diary d : allDayList) {
            addMarker(d);
        }

    }

    private void centerToLocation(LatLng latlng) {
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latlng);
        mBaiduMap.animateMapStatus(msu);
    }

    private void initToolBar(Toolbar toolbar) {
        toolbar.setTitle("Map");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void initMapView() {
        mBaiduMap = mapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);
        mBaiduMap.setMapStatus(msu);
        mapView.showScaleControl(false);
        mapView.showZoomControls(false);
        mBaiduMap.setMyLocationEnabled(true);
    }

    private void initMyLoaction() {
        locationClient = new LocationClient(getApplicationContext());
        locationListener = new MyLocationListener();

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(3000);
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(locationListener);
        if (!locationClient.isStarted()) {
            locationClient.start();
        }
    }

    //位置监听
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            MyLocationData data = new MyLocationData.Builder()//
                    .accuracy(location.getRadius())//
                    .latitude(location.getLatitude())//
                    .longitude(location.getLongitude())//
                    .build();

            mBaiduMap.setMyLocationData(data);

            // 更新经纬度
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            mAddr = location.getAddrStr();
            if (isFirstIn) {
                LatLng latlng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latlng);
                mBaiduMap.animateMapStatus(msu);
                isFirstIn = false;

                Toast.makeText(context, location.getAddrStr(),
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

    @OnClick(R.id.location_button)
    public void myLocation(View view) {
        initMyLoaction();
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(new LatLng(mLatitude, mLongitude));
        mBaiduMap.setMapStatus(msu);
        if (mAddr == null) {
            Toast.makeText(this, "请确认网络可用",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "您的位置是:" + mAddr, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION_CODE:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        initMyLoaction();
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, R.string.location_deny_warn, Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override

    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStop() {
        if (locationClient != null) {
            locationClient.stop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mBaiduMap.setMyLocationEnabled(false);
        super.onDestroy();
        mapView.onDestroy();
        mapView = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            new BottomSheet.Builder(this).title("选择时间").sheet(R.menu.search_list).listener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case R.id.today:
                            showTodayTrip();
                        case R.id.week:
                            showThisWeekTrip();
                    }
                }
            }).show();
        }
        return super.onOptionsItemSelected(item);
    }


    private void showThisWeekTrip() {
        long now = System.currentTimeMillis();
        initTripBetween(now - 7 * 86400000, now);
    }

    private void showTodayTrip() {
        long now = System.currentTimeMillis();
        initTripBetween(now - 86400000, now);
    }

    private void initTripBetween(long begin, long end) {

        List<Diary> allDayList = finalDb.findAllByWhere(Diary.class, "lastChangeDate between '" + begin + "' and '" + end + "'");
        mBaiduMap.clear();
        for (Diary d : allDayList) {
            addMarker(d);

        }
        if (allDayList.size() > 0) {
            centerToLocation(new LatLng(allDayList.get(allDayList.size() - 1).getLatitude(), allDayList.get(allDayList.size() - 1).getLongitude()));
        }
        List<LatLng> pointList = new ArrayList<>();
        List<RoutePoint> list = finalDb.findAllByWhere(RoutePoint.class, "date between '" + begin + "' and '" + end + "'");
        for (RoutePoint point : list) {
            pointList.add(new LatLng(point.getLatitude(), point.getLongitude()));
        }
        if (pointList.size() > 2) {
            OverlayOptions options = new PolylineOptions().points(pointList).width(6).color(0xAAFF6600);
            mBaiduMap.addOverlay(options);
        } else {
            return;
        }
    }


    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

}