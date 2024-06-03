package com.satyambyte.quizgpt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;

public class SettingActivity extends BaseActivity {
    Spinner spinner;
    private SoundManager soundManager;
    public static final String[] languages = {"Choose Language","English", "Español", "Hindi", "Nepali"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        updateUI();


        soundManager = SoundManager.getInstance(this);

        ImageButton crossBtn = findViewById(R.id.cross_btn);
        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playButtonClickSound();
                finish();
            }
        });

        ImageButton privacy_btn = findViewById(R.id.privacy_policy_btn);
        privacy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent("android.intent.action.VIEW",
                        Uri.parse("https://satyamregmi.com.np/policies/privacy-policy.html"));
                startActivity(viewIntent);

            }
        });
        ImageButton info = findViewById(R.id.info_btn);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent("android.intent.action.VIEW",
                        Uri.parse("https://satyamregmi.com.np/"));
                startActivity(viewIntent);
            }
        });


//        ADS
//        MobileAds.initialize(this);
//        AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
//                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
//                    @Override
//                    public void onNativeAdLoaded(NativeAd nativeAd) {
//                        NativeTemplateStyle styles = new
//                                NativeTemplateStyle.Builder().build();
//                        TemplateView template = findViewById(R.id.native_ads);
//                        template.setStyles(styles);
//                        template.setNativeAd(nativeAd);
//                    }
//                })
//                .build();
//
//        adLoader.loadAd(new AdRequest.Builder().build());


        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = parent.getItemAtPosition(position).toString();
                switch (selectedLang) {
                    case "English":
                        changeLanguage("en");
                        updateUI();
                        restartActivity();
                        break;
                    case "Español":
                        changeLanguage("es");
                        updateUI();
                        restartActivity();
                        break;
                    case "Nepali":
                        changeLanguage("ne");
                        updateUI();
                        restartActivity();
                        break;
                    case "Hindi":
                        changeLanguage("hi");
                        updateUI();
                        restartActivity();
                        break;
                    default:
                        updateUI();
                        break;
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void restartActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    private void updateUI() {
        TextView setting_txt = findViewById(R.id.privacyPolicyText);
        setting_txt.setText(getResources().getString(R.string.settings_txt));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

}
