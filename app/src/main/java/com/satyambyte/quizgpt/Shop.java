package com.satyambyte.quizgpt;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewarded.ServerSideVerificationOptions;

import io.reactivex.annotations.NonNull;

public class Shop extends BaseActivity {

    private Handler handler = new Handler();
    private int coinCount = 0;
    private int bombHintCount = 0;
    private int skipHintCount = 0;
    private int shuffleHintCount = 0;
    private TextView coinView;
    private TextView bombView;
    private TextView skipView;
    private TextView shuffleView;
    private TextView bombCoin, skipCoin, shuffleCoin, errorMsg;
    private LinearLayout shopLayout1;
    private LinearLayout shopLayout2;
    private LinearLayout shopLayout3;
    private LinearLayout buyLayout1;
    private LinearLayout buyLayout2;
    private LinearLayout buyLayout3;

    private SoundManager soundManager;
    private AdView mAdView;
    private RewardedAd rewardedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);


        soundManager = SoundManager.getInstance(this);


//        GO_BACK to HOME
        ImageView homeBtn = findViewById(R.id.home);
        homeBtn.setOnClickListener(v -> {
            soundManager.playButtonClickSound();
            saveCoinCount(coinCount);
            saveBombHintCount(bombHintCount);
            saveShuffleCount(shuffleHintCount);
            saveSkipHintCount(skipHintCount);
            updateBombHintCounter();
            updateCoinCounter();
            updateShuffleCounter();
            updateSkipHintCounter();
            finish();
        });

//        ADS

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadBombAd();
                loadSkipAd();
                loadShuffleAd();
                loadCoinAd();
            }
        });

//        LOAD ADS
        loadBannerAd();
        loadBombAd();
        loadSkipAd();
        loadShuffleAd();
        loadCoinAd();


//        SHOP LAYOUT

        shopLayout1 = findViewById(R.id.shopLayout1);
        shopLayout2 = findViewById(R.id.shopLayout2);
        shopLayout3 = findViewById(R.id.shopLayout3);
//        Buy LAYOUT
        buyLayout1 = findViewById(R.id.buyLayout1);
        buyLayout2 = findViewById(R.id.buyLayout2);
        buyLayout3 = findViewById(R.id.buyLayout3);
//        ADS REWARD LAYOUT
        LinearLayout adRewards1 = findViewById(R.id.adRewards1);
        adRewards1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBombAd();
                //        CheckAd
                checkBombAd();
            }
        });
        LinearLayout adRewards2 = findViewById(R.id.adRewards2);
        adRewards2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSkipAd();
                checkSkipAd();
            }
        });
        LinearLayout adRewards3 = findViewById(R.id.adRewards3);
        adRewards3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadShuffleAd();
                checkShuffleAd();
            }
        });
        LinearLayout adRewards4 = findViewById(R.id.adRewards4);
        adRewards4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCoinAd();
                //        CheckAd
                checkCoinAd();
            }
        });

        shopBomb();
        shopSkip();
        shopShuffle();

        buyLayoutA();
        buyLayoutB();
        buyLayoutC();


//                COIN               // COLLECTABLES
        coinView = findViewById(R.id.coinView);
        shuffleView = findViewById(R.id.shuffleView);
        bombView = findViewById(R.id.bombView);
        skipView = findViewById(R.id.skipView);

        bombCoin = findViewById(R.id.bombCoin);
        skipCoin = findViewById(R.id.skipCoin);
        shuffleCoin = findViewById(R.id.shuffleCoin);
        errorMsg = findViewById(R.id.error_msg);

        loadCoinCount();
        loadBombHintCount();
        loadSkipHintCount();
        loadShuffleCount();

        updateCoinCounter();
        updateBombHintCounter();
        updateSkipHintCounter();
        updateShuffleCounter();

        checkCoinForColor();

    }

    private void checkBombAd() {
//        REWARDED ADS
        if (rewardedAd != null) {
            Activity activityContext = Shop.this;
            rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    bombHintCount = bombHintCount + 2;
                    saveBombHintCount(bombHintCount);
                    updateBombHintCounter();
                }
            });
            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad dismissed fullscreen content.");
                    rewardedAd = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.");
                    rewardedAd = null;
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.");
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
            adErrDialog();
        }
    }
    private void checkSkipAd() {
//        REWARDED ADS

        if (rewardedAd != null) {
            Activity activityContext = Shop.this;
            rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    skipHintCount++;
                    saveSkipHintCount(skipHintCount);
                    updateSkipHintCounter();
                }
            });
            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad dismissed fullscreen content.");
                    rewardedAd = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.");
                    rewardedAd = null;
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.");
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
            adErrDialog();
        }
    }
    private void checkShuffleAd() {
//        REWARDED ADS

        if (rewardedAd != null) {
            Activity activityContext = Shop.this;
            rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    shuffleHintCount+=5;
                    saveShuffleCount(shuffleHintCount);
                    updateShuffleCounter();
                }
            });
            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad dismissed fullscreen content.");
                    rewardedAd = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.");
                    rewardedAd = null;
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.");
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
            adErrDialog();
        }
    }
    private void checkCoinAd(){
        //        REWARDED ADS
        if (rewardedAd != null) {
            Activity activityContext = Shop.this;
            rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    coinCount+=100;
                    saveCoinCount(coinCount);
                    updateCoinCounter();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           soundManager.playCoinEffectSound();
                        }
                    }, 800);
                }
            });
            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad dismissed fullscreen content.");
                    rewardedAd = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.");
                    rewardedAd = null;
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.");
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
            adErrDialog();
            Toast.makeText(this, "Ad was not ready!", Toast.LENGTH_SHORT).show();
        }
    }

    private void adErrDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.ad_err_dialog);
        dialog.setCancelable(false);
        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void loadShuffleAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-4972856716609696/4014769222",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                        ServerSideVerificationOptions options = new ServerSideVerificationOptions
                                .Builder()
                                .setCustomData("SAMPLE_CUSTOM_DATA_STRING")
                                .build();
                        rewardedAd.setServerSideVerificationOptions(options);
                    }
                });
    }
    private void loadSkipAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-4972856716609696/4597223155",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                        ServerSideVerificationOptions options = new ServerSideVerificationOptions
                                .Builder()
                                .setCustomData("SAMPLE_CUSTOM_DATA_STRING")
                                .build();
                        rewardedAd.setServerSideVerificationOptions(options);
                    }
                });
    }
    private void loadBombAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-4972856716609696/4262181233",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                        ServerSideVerificationOptions options = new ServerSideVerificationOptions
                                .Builder()
                                .setCustomData("SAMPLE_CUSTOM_DATA_STRING")
                                .build();
                        rewardedAd.setServerSideVerificationOptions(options);
                    }
                });
    }
    private void loadCoinAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-4972856716609696/3128777552",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                        ServerSideVerificationOptions options = new ServerSideVerificationOptions
                                .Builder()
                                .setCustomData("SAMPLE_CUSTOM_DATA_STRING")
                                .build();
                        rewardedAd.setServerSideVerificationOptions(options);
                    }
                });
    }
    private void loadBannerAd() {
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    private void saveCoinCount(int coinCount) {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("coinCount", coinCount);
        editor.apply();
    }
    private void saveBombHintCount(int bombHintCount) {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("bombHintCount", bombHintCount);
        editor.apply();
    }
    private void saveSkipHintCount(int skipHintCount) {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("skipHintCount", skipHintCount);
        editor.apply();
    }
    private void saveShuffleCount(int shuffleHintCount) {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("shuffleHintCount", shuffleHintCount);
        editor.apply();
    }

    private void updateCoinCounter() {
        coinView.setText("" + coinCount);
    }
    private void updateBombHintCounter() {
        bombView.setText("" + bombHintCount);
    }
    private void updateSkipHintCounter() {
        skipView.setText("" + skipHintCount);
    }
    private void updateShuffleCounter() {
        shuffleView.setText("" + shuffleHintCount);
    }

    private void loadCoinCount() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        coinCount = sharedPreferences.getInt("coinCount", 0); // 0 is the default value if coinCount is not found
    }
    private void loadBombHintCount() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        bombHintCount = sharedPreferences.getInt("bombHintCount", 0); // 0 is the default value if bombCount is not found
        Log.d("BombCount", "Loaded bomb hint: " + bombHintCount);
    }
    private void loadSkipHintCount() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        skipHintCount = sharedPreferences.getInt("skipHintCount", 0);
        Log.d("SkipCount", "Loaded skip hint: " + skipHintCount);
    }
    private void loadShuffleCount() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        shuffleHintCount = sharedPreferences.getInt("shuffleHintCount", 0);
        Log.d("ShuffleCount", "Loaded shuffle hint: " + shuffleHintCount);
    }

    private void shopBomb(){
        shopLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(coinCount < 50){
                    bombCoin.setTextColor(Color.parseColor("red"));
                    notEnoughCoinErr();
                }else{
                coinCount -= 50;
                bombHintCount++;
                updateBombHintCounter();
                updateCoinCounter();
                saveBombHintCount(bombHintCount);
                saveCoinCount(coinCount);
                checkCoinForColor();
                }
            }
        });
    }
    private void shopSkip(){
        shopLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(coinCount < 500){
                    skipCoin.setTextColor(Color.parseColor("red"));
                    notEnoughCoinErr();
                }else {
                    coinCount -= 500;
                    skipHintCount++;
                    updateSkipHintCounter();
                    updateCoinCounter();
                    saveCoinCount(coinCount);
                    saveSkipHintCount(skipHintCount);
                    checkCoinForColor();
                }
            }
        });
    }
    private void shopShuffle(){
        shopLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(coinCount < 10){
                    shuffleCoin.setTextColor(Color.parseColor("red"));
                    notEnoughCoinErr();
                }else {
                    coinCount -= 10;
                    shuffleHintCount++;
                    updateShuffleCounter();
                    updateCoinCounter();
                    saveCoinCount(coinCount);
                    saveShuffleCount(shuffleHintCount);
                    checkCoinForColor();
                }
            }
        });
    }

    private void buyLayoutA(){
        buyLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable foregroundDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.disabled_bg_shop);
                buyLayout1.setForeground(foregroundDrawable);
            }
        });
    }
    private void buyLayoutB(){
        buyLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable foregroundDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.disabled_bg_shop);
                buyLayout2.setForeground(foregroundDrawable);
            }
        });
    }
    private void buyLayoutC(){
        buyLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable foregroundDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.disabled_bg_shop);
                buyLayout3.setForeground(foregroundDrawable);
            }
        });
    }
    private void notEnoughCoinErr(){
        errorMsg.setText("Not Enough Coins");
        errorMsg.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                errorMsg.setVisibility(View.GONE);
            }
        }, 800);
    }

    private void checkCoinForColor(){
        if(coinCount < 500) skipCoin.setTextColor(Color.parseColor("red"));
        if (coinCount<50) bombCoin.setTextColor(Color.parseColor("red"));
        if (coinCount<10) shuffleCoin.setTextColor(Color.parseColor("red"));
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Load the coin count when the activity is resumed
        loadCoinCount();
        loadBombHintCount();
        loadSkipHintCount();
        loadShuffleCount();
        loadBannerAd();

        updateCoinCounter();
        updateBombHintCounter();
        updateSkipHintCounter();
        updateShuffleCounter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveCoinCount(coinCount);
        saveShuffleCount(shuffleHintCount);
        saveBombHintCount(bombHintCount);
        saveSkipHintCount(skipHintCount);
    }
}