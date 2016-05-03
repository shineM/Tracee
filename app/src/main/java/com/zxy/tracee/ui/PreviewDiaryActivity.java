package com.zxy.tracee.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zxy.tracee.R;
import com.zxy.tracee.model.Diary;
import com.zxy.tracee.util.CommonUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class PreviewDiaryActivity extends AppCompatActivity {

    public static final String MARKER_TRANSITION = "marker transition";
    public static final String DIARY_BUNDLE = "preview diray";

    @Bind(R.id.preview_title)
    TextView mTitle;
    @Bind(R.id.preview_content)
    TextView mContent;
    @Bind(R.id.preview_date)
    TextView mDate;
    @Bind(R.id.preview_address)
    TextView mAddress;
    Intent intent;
    Diary diary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_diary);
        ButterKnife.bind(this);
        intent = getIntent();
        initData();
    }

    private void initData() {
        diary = (Diary) intent.getSerializableExtra(DIARY_BUNDLE);
        mTitle.setText(TextUtils.isEmpty(diary.getTitle()) ? "Title Empty" : diary.getTitle());
        mContent.setText(TextUtils.isEmpty(diary.getContent()) ? "No detail description，you can add in info page" : diary.getContent());
        mDate.setText(CommonUtils.getFormatedDateString(diary.getDate()));
        mAddress.setText(diary.getLatitude() + "°," + diary.getLongitude() + "°");
    }

    public void dismiss(View view) {
        onBackPressed();
    }

    @OnClick(R.id.view_detail_btn)
    public void onViewMoreBtnClick() {
        Intent intent = new Intent(PreviewDiaryActivity.this, DiaryDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("diary", diary);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.view_all_today)
    public void viewAll() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MapActivity.DIARY_WANT_QUERY, diary);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
