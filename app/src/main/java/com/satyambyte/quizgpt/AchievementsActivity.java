package com.satyambyte.quizgpt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;


/** TODO: claim button. create different shared preference for each claimable rewards. **/

public class AchievementsActivity extends AppCompatActivity {

    private SoundManager soundManager;
    private int bombHintCount;
    private int skipHintCount;
    private int shuffleHintCount;
    private int coinCount;
    private int chestProgressCounter = 0;
    private TextView chestProgressText;
    private AppCompatButton claimBtn1;
    private AppCompatButton claimBtn2;
    private AppCompatButton claimBtn3;
    private AppCompatButton claimBtn4;
    private AppCompatButton claimBtn5;
    private AppCompatButton claimChest;
    int currentLevel;
    boolean claimAble, firstClaim1, firstClaim2, firstClaim3, firstClaim4, firstClaim5, firstClaimChest;
    private static final String CLAIM_ABLE = "claim_able";
    private static final String FIRST_CLAIM_1 = "first_claimed_1";
    private static final String FIRST_CLAIM_2 = "first_claimed_2";
    private static final String FIRST_CLAIM_3 = "first_claimed_3";
    private static final String FIRST_CLAIM_4 = "first_claimed_4";
    private static final String FIRST_CLAIM_5 = "first_claimed_5";
    private static final String FIRST_CLAIM_CHEST = "first_claimed_chest";
    private static final String CHEST_PROGRESS_COUNTER = "chest_progress_counter";
    SharedPreferences sharedPreferences;
    String progressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        soundManager = SoundManager.getInstance(this);

        loadCurrentLevel();
        loadCurrentWordIndex();
        loadCurrentProgress();
        loadCoinCount();
        loadBombHintCount();
        loadSkipHintCount();
        loadShuffleCount();

        claimBtn1 = findViewById(R.id.taskClaim1);
        claimBtn2 = findViewById(R.id.taskClaim2);
        claimBtn3 = findViewById(R.id.taskClaim3);
        claimBtn4 = findViewById(R.id.taskClaim4);
        claimBtn5 = findViewById(R.id.taskClaim5);
        claimChest = findViewById(R.id.claimChest);
        chestProgressText = findViewById(R.id.chestProgressTxt);
//        SET BUTTON DISABLED AT FIRST
        claimBtn1.setEnabled(false);
        claimBtn2.setEnabled(false);
        claimBtn3.setEnabled(false);
        claimBtn4.setEnabled(false);
        claimBtn5.setEnabled(false);
        claimChest.setEnabled(false);

        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playButtonClickSound();
                saveCoinCount(coinCount);
                saveBombHintCount(bombHintCount);
                saveShuffleCount(shuffleHintCount);
                saveSkipHintCount(skipHintCount);
                finish();
            }
        });



//        STORE THE BOOLEAN
        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        
        claimAble = sharedPreferences.getBoolean(CLAIM_ABLE, false);
        firstClaim1 = sharedPreferences.getBoolean(FIRST_CLAIM_1, true);
        firstClaim2 = sharedPreferences.getBoolean(FIRST_CLAIM_2, true);
        firstClaim3 = sharedPreferences.getBoolean(FIRST_CLAIM_3, true);
        firstClaim4 = sharedPreferences.getBoolean(FIRST_CLAIM_4, true);
        firstClaim5 = sharedPreferences.getBoolean(FIRST_CLAIM_5, true);
        firstClaimChest = sharedPreferences.getBoolean(FIRST_CLAIM_CHEST, true);


        chestProgressCounter = sharedPreferences.getInt(CHEST_PROGRESS_COUNTER, 0);
        progressText = getString(R.string.chest_progress_format, chestProgressCounter);
        chestProgressText.setText(progressText);
        checkLvl();

        if (!firstClaim1){
            claimBtn1.setEnabled(false);
            claimBtn1.setText(R.string.claimed);
        }
        if (!firstClaim2){
            claimBtn2.setEnabled(false);
            claimBtn2.setText(R.string.claimed);
        }
        if (!firstClaim3){
            claimBtn3.setEnabled(false);
            claimBtn3.setText(R.string.claimed);
        }
        if (!firstClaim4){
            claimBtn4.setEnabled(false);
            claimBtn4.setText(R.string.claimed);
        }
        if (!firstClaim5){
            claimBtn5.setEnabled(false);
            claimBtn5.setText(R.string.claimed);
        }
        if(!firstClaimChest){
            claimChest.setEnabled(false);
            claimChest.setText(R.string.claimed);
        }

    }
    private void updateClaimBox(){
        Intent i = new Intent(AchievementsActivity.this, AchievementsActivity.class);
        finish();
        overridePendingTransition(0, 0);
        startActivity(i);
        overridePendingTransition(0, 0);
    }
    private int loadCurrentWordIndex() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        return sharedPreferences.getInt("currentWordIndex", 0); // 0 is the default index if not found
    }

    private int loadCurrentLevel() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        currentLevel = sharedPreferences.getInt("currentLevel", 1); // 1 is the default level if not found
        return currentLevel;
    }
    private int loadCurrentProgress(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        chestProgressCounter = sharedPreferences.getInt("chest_progress_counter", 0); // 0 is the default level if not found
        return chestProgressCounter;

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

    private void checkLvl() {
        if (currentLevel > 4 && firstClaim1) {
            claimAble = true;
            claimBtn1.setVisibility(View.VISIBLE);
            claimBtn1.setEnabled(true);
            claimBtn1.setBackgroundResource(R.drawable.effect_button_clicked);
            ClaimBtnOnClick1();
        }

        if (currentLevel > 24 && firstClaim2) {             /*TODO://Set level as 24 */
            claimAble = true;
            claimBtn2.setVisibility(View.VISIBLE);
            claimBtn2.setEnabled(true);
            claimBtn2.setBackgroundResource(R.drawable.effect_button_clicked);
            ClaimBtnOnClick2();
        }

        if (currentLevel > 49 && firstClaim3) {
            claimAble = true;
            claimBtn3.setVisibility(View.VISIBLE);
            claimBtn3.setEnabled(true);
            claimBtn3.setBackgroundResource(R.drawable.effect_button_clicked);
            ClaimBtnOnClick3();
        }
        if (currentLevel > 99 && firstClaim4) {
            claimAble = true;
            claimBtn4.setVisibility(View.VISIBLE);
            claimBtn4.setEnabled(true);
            claimBtn4.setBackgroundResource(R.drawable.effect_button_clicked);
            ClaimBtnOnClick4();
        }
        if (currentLevel > 149 && firstClaim5) {
            claimAble = true;
            claimBtn5.setVisibility(View.VISIBLE);
            claimBtn5.setEnabled(true);
            claimBtn5.setBackgroundResource(R.drawable.effect_button_clicked);
            ClaimBtnOnClick5();
        }
        if (chestProgressCounter == 199 && firstClaimChest){
            claimAble = true;
            claimChest.setEnabled(true);
            claimChest.setVisibility(View.VISIBLE);
            claimChest.setBackgroundResource(R.drawable.effect_button_clicked);
            ClaimBtnOnChest();
        }
    }

    void ClaimBtnOnClick1() {
        claimBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Rewards received dialog
                Toast.makeText(AchievementsActivity.this, "Rewards received", Toast.LENGTH_SHORT).show();
//                GIVE REWARDS
                //Shuffle
                shuffleHintCount += 5;
                saveShuffleCount(shuffleHintCount);
//Skip
                skipHintCount += 1;
                saveSkipHintCount(skipHintCount);
//Bomb
                bombHintCount += 3;
                saveBombHintCount(bombHintCount);

                chestProgressCounter = 1;
                chestProgressText.setText(progressText);

                soundManager.playButtonClickSound();
                claimBtn1.setEnabled(false);
                claimBtn1.setText(R.string.claimed);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(CHEST_PROGRESS_COUNTER, chestProgressCounter);
                editor.putBoolean(FIRST_CLAIM_1, false);
                editor.apply();
                updateClaimBox();
            }
        });
        claimAble = false;
    }
    void ClaimBtnOnClick2() {
        claimBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Rewards received dialog
                Toast.makeText(AchievementsActivity.this, "Rewards received", Toast.LENGTH_SHORT).show();
                // Update hints
//Shuffle
                shuffleHintCount += 5;
                saveShuffleCount(shuffleHintCount);
//Skip
                skipHintCount += 1;
                saveSkipHintCount(skipHintCount);
//Bomb
                bombHintCount += 5;
                saveBombHintCount(bombHintCount);

                chestProgressCounter = 2;
                chestProgressText.setText(progressText);

//                MANAGER
                soundManager.playButtonClickSound();
                claimBtn2.setEnabled(false);
                claimBtn2.setText(R.string.claimed);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(FIRST_CLAIM_2, false);
                editor.putInt(CHEST_PROGRESS_COUNTER, chestProgressCounter);
                editor.apply();
                updateClaimBox();
            }
        });
        claimAble = false;
    }
    void ClaimBtnOnClick3() {
        claimBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Rewards received dialog
                Toast.makeText(AchievementsActivity.this, "Rewards received", Toast.LENGTH_SHORT).show();
                // Update hints
                //Shuffle
                shuffleHintCount += 7;
                saveShuffleCount(shuffleHintCount);
//Skip
                skipHintCount += 2;
                saveSkipHintCount(skipHintCount);
//Bomb
                bombHintCount += 7;
                saveBombHintCount(bombHintCount);

                chestProgressCounter = 3;
                chestProgressText.setText(progressText);

                claimBtn3.setEnabled(false);
                claimBtn3.setText(R.string.claimed);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(FIRST_CLAIM_3, false);
                editor.putInt(CHEST_PROGRESS_COUNTER, chestProgressCounter);
                editor.apply();
                soundManager.playButtonClickSound();
                updateClaimBox();
            }
        });
        claimAble = false;
    }
    void ClaimBtnOnClick4() {
        claimBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Rewards received dialog
                Toast.makeText(AchievementsActivity.this, "Rewards received", Toast.LENGTH_SHORT).show();
                // Update hints
//Shuffle
                coinCount += 100;
                saveCoinCount(coinCount);
                saveShuffleCount(shuffleHintCount);
//Skip
                skipHintCount += 2;
                saveSkipHintCount(skipHintCount);
//Bomb
                bombHintCount += 10;
                saveBombHintCount(bombHintCount);

                chestProgressCounter = 4;
                chestProgressText.setText(progressText);

                claimBtn4.setEnabled(false);
                claimBtn4.setText(R.string.claimed);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(FIRST_CLAIM_4, false);
                editor.putInt(CHEST_PROGRESS_COUNTER, chestProgressCounter);
                editor.apply();
                soundManager.playButtonClickSound();
                updateClaimBox();
            }
        });
        claimAble = false;
    }
    void ClaimBtnOnClick5() {
        claimBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Rewards received dialog
                Toast.makeText(AchievementsActivity.this, "Rewards received", Toast.LENGTH_SHORT).show();
                // Update hints
//Shuffle
                coinCount += 150;
                saveCoinCount(coinCount);
                saveShuffleCount(shuffleHintCount);
//Skip
                skipHintCount += 3;
                saveSkipHintCount(skipHintCount);
//Bomb
                bombHintCount += 10;
                saveBombHintCount(bombHintCount);

                chestProgressCounter = 5;
                chestProgressText.setText(progressText);

                soundManager.playButtonClickSound();
                claimBtn5.setEnabled(false);
                claimBtn5.setText(R.string.claimed);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(FIRST_CLAIM_5, false);
                editor.putInt(CHEST_PROGRESS_COUNTER, chestProgressCounter);
                editor.apply();
                updateClaimBox();
            }
        });
        claimAble = false;
    }
    void ClaimBtnOnChest(){
        claimChest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playButtonClickSound();
                Toast.makeText(AchievementsActivity.this, "Rewards received", Toast.LENGTH_SHORT).show();

//                Dialog
                coinCount += 250;
                skipHintCount += 5;
                bombHintCount += 5;
                shuffleHintCount += 5;
                saveCoinCount(coinCount);
                saveShuffleCount(shuffleHintCount);
                saveSkipHintCount(skipHintCount);
                saveBombHintCount(bombHintCount);

                claimChest.setEnabled(false);
                claimChest.setText(R.string.claimed);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(FIRST_CLAIM_CHEST, false);
                editor.apply();
                updateClaimBox();
            }
        });
        claimAble = false;
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


}
