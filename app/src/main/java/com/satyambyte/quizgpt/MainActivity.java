package com.satyambyte.quizgpt;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;

import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.SnapshotsClient;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.ads.MobileAds;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class MainActivity extends BaseActivity {
    private CardView playCardView;
    private CardView moreGames;
    private int coinCount = 0; // Initialize the coin count
    private TextView levelTxt;
    private TextView coinView;
    private SoundManager soundManager;
    private ImageView addCoin, circularImageCoin;
    private static final int RC_SAVED_GAMES = 9003;
    private String mCurrentSaveName = "snapshotTemp";
    private static final String PREF_FIRST_TIME = "first_time";

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
        loadCurrentLevel();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signInSilently();
        updateUI();

//        PRIVACY POLICY
        // Check if privacy policy has been shown before
//        if (!getPrivacyPolicyShownStatus()) {
//            showPrivacyPolicyPopup();
//        }
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean(PREF_FIRST_TIME, true);
        if (isFirstTime) {
            // Show the dialog
            showPrivacyPolicyPopup();
            // Set the first time flag to false
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PREF_FIRST_TIME, false);
            editor.apply();
        }


//          GOOGLE ADS
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadBannerAd();


        soundManager = SoundManager.getInstance(this);

//        COIN
        coinView = findViewById(R.id.coinView);
        loadCoinCount();
        loadCurrentLevel();
        loadCurrentWordIndex();
        

        // Update the coin count display
        updateCoinCounter();

        addCoin = findViewById(R.id.addCoin);
        circularImageCoin = findViewById(R.id.circularImageCoin);
        addCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playButtonClickSound();
                startActivity(new Intent(MainActivity.this, Shop.class));
            }
        });
        circularImageCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playButtonClickSound();
                startActivity(new Intent(MainActivity.this, Shop.class));
            }
        });
        coinView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playButtonClickSound();
                startActivity(new Intent(MainActivity.this, Shop.class));
            }
        });


        // Find your TextView in the layout

        levelTxt = findViewById(R.id.levelTxt); // Replace with your TextView ID
        int currentLevel = loadCurrentLevel();
        displayCurrentLevel(currentLevel);
        // Load and display the current level

        CardView shopCardView = findViewById(R.id.shopCardView);
        shopCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playButtonClickSound();
                startActivity(new Intent(MainActivity.this, Shop.class));
            }
        });

        ImageView achievementActivity = findViewById(R.id.achievementsActivity);
        achievementActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playButtonClickSound();
                startActivity(new Intent(MainActivity.this, AchievementsActivity.class));
            }
        });


//Circular Image
        ImageView circularImageView = findViewById(R.id.circularImageProfile);
        circularImageView.setImageResource(R.drawable.setting_btn);

//// Create a round shape
//        circularImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        circularImageView.setClipToOutline(true);
//        circularImageView.setOutlineProvider(new ViewOutlineProvider() {
//            @Override
//            public void getOutline(View view, Outline outline) {
//                int width = view.getWidth();
//                int height = view.getHeight();
//                int radius = Math.min(width, height) / 2;
//                outline.setRoundRect(10, 10, width, height, radius);
//            }
//        });
        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playButtonClickSound();
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });


        setupPlayButton();
//        setupShopButton();
        setupMoreGamesBtn();

    }

    private void updateUI() {
        TextView moreGamesTxt = findViewById(R.id.moreGamesTxtView);
        TextView playBtnTxt = findViewById(R.id.playTextView);
        moreGamesTxt.setText(getResources().getString(R.string.more_games));
        playBtnTxt.setText(getResources().getString(R.string.play));
        TextView shopTxtView = findViewById(R.id.shopTxtView);
        shopTxtView.setText(getResources().getString(R.string.shop_txt));
    }

    private void showPrivacyPolicyPopup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_privacy_policy);
        dialog.setCancelable(false);
        ImageButton buttonPlayNext = dialog.findViewById(R.id.closePopupButton);
        buttonPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void loadBannerAd() {
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void signInSilently() {
        GoogleSignInOptions signInOption =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                        // Add the APPFOLDER scope for Snapshot support.
                        .requestScopes(Drive.SCOPE_APPFOLDER)
                        .build();

        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, signInOption);
        signInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            onConnected(task.getResult());
                        } else {
                            // Player will need to sign-in explicitly using the UI
                        }
                    }
                });
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        loadSnapshotData();
    }

    private void loadSnapshotData() {
        SnapshotsClient snapshotsClient = PlayGames.getSnapshotsClient(this);
        snapshotsClient.open(mCurrentSaveName, true, SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED)
                .addOnCompleteListener(new OnCompleteListener<SnapshotsClient.DataOrConflict<Snapshot>>() {
                    @Override
                    public void onComplete(@NonNull Task<SnapshotsClient.DataOrConflict<Snapshot>> task) {
                        if (task.isSuccessful()) {
                            Snapshot snapshot = task.getResult().getData();
                            if (snapshot != null) {
                                try {
                                    byte[] data = snapshot.getSnapshotContents().readFully();
                                    String savedData = new String(data);
                                    // Parse the saved data to get the current level and coin count
                                    // Update your game's current level and coin count with the data
                                } catch (IOException e) {
                                    // Handle the IOException
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            // Handle the error
                            Exception e = task.getException();
                        }
                    }
                });
    }
    public void saveSnapshotData() {
        SnapshotsClient snapshotsClient = PlayGames.getSnapshotsClient(this);

        // Prepare your data to save (this is just a basic example)
        int currentLevel = loadCurrentLevel();
        displayCurrentLevel(currentLevel);
        updateCoinCounter();
        String dataToSave = "Level: " + currentLevel + " Coins: " + coinCount;

        // Convert the data to bytes
        byte[] data = dataToSave.getBytes();

        // Create a snapshot metadata change
        SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                .setDescription("Player progress")
                .build();

        // Open the current snapshot (or create a new one if it doesn't exist)
        snapshotsClient.open(mCurrentSaveName, true, SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED)
                .addOnCompleteListener(new OnCompleteListener<SnapshotsClient.DataOrConflict<Snapshot>>() {
                    @Override
                    public void onComplete(@NonNull Task<SnapshotsClient.DataOrConflict<Snapshot>> task) {
                        if (task.isSuccessful()) {
                            Snapshot snapshot = task.getResult().getData();
                            if (snapshot != null) {
                                // Set the data payload for the snapshot
                                snapshot.getSnapshotContents().writeBytes(data);

                                // Commit the operation
                                snapshotsClient.commitAndClose(snapshot, metadataChange)
                                        .addOnCompleteListener(new OnCompleteListener<SnapshotMetadata>() {
                                            @Override
                                            public void onComplete(@NonNull Task<SnapshotMetadata> task) {
                                                if (task.isSuccessful()) {
                                                    // Snapshot saved successfully
                                                } else {
                                                    // Handle the error
                                                    Exception e = task.getException();
                                                }
                                            }
                                        });
                            }
                        } else {
                            // Handle the error
                            Exception e = task.getException();
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SAVED_GAMES && intent != null) {
            if (intent.hasExtra(SnapshotsClient.EXTRA_SNAPSHOT_METADATA)) {
                // Load a snapshot.
                SnapshotMetadata snapshotMetadata =
                        intent.getParcelableExtra(SnapshotsClient.EXTRA_SNAPSHOT_METADATA);
                mCurrentSaveName = snapshotMetadata.getUniqueName();

                // Load the game data from the Snapshot
                // ...
            } else if (intent.hasExtra(SnapshotsClient.EXTRA_SNAPSHOT_NEW)) {
                // Create a new snapshot named with a unique string
                String unique = new BigInteger(281, new Random()).toString(13);
                mCurrentSaveName = "snapshotTemp-" + unique;

                // Create the new snapshot
                // ...
            }
        }
    }
    Task<byte[]> loadSnapshot() {
        // Display a progress dialog
        // ...

        // Get the SnapshotsClient from the signed-in account.
        SnapshotsClient snapshotsClient =
                PlayGames.getSnapshotsClient(this);

        // In the case of a conflict, the most recently modified version of this snapshot will be used.
        int conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED;

        // Open the saved game using its name.
        return snapshotsClient.open(mCurrentSaveName, true, conflictResolutionPolicy)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Log.e(TAG, "Error while opening Snapshot.", e);
                    }
                }).continueWith(new Continuation<SnapshotsClient.DataOrConflict<Snapshot>, byte[]>() {
                    @Override
                    public byte[] then(@NonNull Task<SnapshotsClient.DataOrConflict<Snapshot>> task) throws Exception {
                        Snapshot snapshot = task.getResult().getData();

                        // Opening the snapshot was a success, and any conflicts have been resolved.
                        try {
                            // Extract the raw data from the snapshot.
                            return snapshot.getSnapshotContents().readFully();
                        } catch (IOException e) {
                            Log.e(TAG, "Error while reading Snapshot.", e);
                        }

                        return null;
                    }
                }).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        // Dismiss the progress dialog and reflect the changes in the UI when complete.
                        // ...
                    }
                });
    }

    private Task<SnapshotMetadata> writeSnapshot(Snapshot snapshot,
                                                 byte[] data, Bitmap coverImage, String desc) {

        // Set the data payload for the snapshot
        snapshot.getSnapshotContents().writeBytes(data);

        // Create the change operation
        SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                .setCoverImage(coverImage)
                .setDescription(desc)
                .build();

        SnapshotsClient snapshotsClient =
                PlayGames.getSnapshotsClient(this);

        // Commit the operation
        return snapshotsClient.commitAndClose(snapshot, metadataChange);
    }
    private int loadCurrentWordIndex() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        return sharedPreferences.getInt("currentWordIndex", 0); // 0 is the default index if not found
    }

    private void displayCurrentLevel(int currentLevel) {
        levelTxt.setText("# " + currentLevel);
    }
    private int loadCurrentLevel() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        return sharedPreferences.getInt("currentLevel", 1); // 1 is the default level if not found
    }
    private void loadCoinCount() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        coinCount = sharedPreferences.getInt("coinCount", 0); // 0 is the default value if coinCount is not found
    }

    private void updateCoinCounter() {
        coinView.setText("" + coinCount);
    }

    private void saveCoinCount(int coinCount) {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("coinCount", coinCount);
        editor.apply();
    }

    private void setupMoreGamesBtn() {
        moreGames = findViewById(R.id.moreGames);
        soundManager.playButtonClickSound();
        moreGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToPlayPublisher();
            }
        });
    }

    private void redirectToPlayPublisher() {
            Intent viewIntent = new Intent("android.intent.action.VIEW",
                            Uri.parse("https://play.google.com/store/apps/developer?id=Satyam+Games"));
            startActivity(viewIntent);

    }

    private void setupPlayButton() {
        playCardView = findViewById(R.id.playCardView);
        playCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playButtonClickSound();
                openPlaygameActivity();
            }
        });
    }

//    private void setupShopButton(){
//        //        SHOP
//        shopCardView.findViewById(R.id.shopCardView);
//        shopCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                soundManager.playButtonClickSound();
//                openShopActivity();
//            }
//        });
//    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save the coin count when the activity is paused
        saveCoinCount(coinCount);
        loadCurrentLevel();
        updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundManager.release();
        updateUI();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Load the coin count when the activity is resumed
        loadCoinCount();
        loadCurrentLevel();
        // Update the UI to reflect the updated coin count
        updateCoinCounter();
        int currentLevel = loadCurrentLevel();
        loadBannerAd();
        updateUI();

        // Update the UI to reflect the updated current level
        displayCurrentLevel(currentLevel);
    }

    private void openPlaygameActivity() {
        Intent intent = new Intent(this, PlaygameActivity.class);
        startActivity(intent);
    }

//    private void openShopActivity(){
//        Intent intent = new Intent(this, Shop.class);
//        startActivity(intent);
//    }
}