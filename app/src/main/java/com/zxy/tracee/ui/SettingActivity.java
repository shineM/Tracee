package com.zxy.tracee.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.zxy.tracee.R;
import com.zxy.tracee.util.SettingUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initToolBar(toolbar);
    }

    private void initToolBar(Toolbar toolbar) {
        if (toolbar != null) {
            toolbar.setTitle("Setting");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    public static class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting_pref);
            SettingUtil.registerOnSharedPrefenceChangeListener(getActivity(), this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(SettingUtil.PREF_NIGHT_MODE)) {
                if (SettingUtil.shouldNightModeOn(getActivity())) {
                    Toast.makeText(getActivity(), "Night Mode On", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Night Mode Off", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            SettingUtil.unRegisterOnSharedPrefenceChangeListener(getActivity(), this);
        }
    }

}
