package com.satyambyte.quizgpt;


import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;
import static androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior.getTag;

import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.SnapshotsClient;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlaygameActivity extends BaseActivity {
    private WordContainer[] wordContainers; // Your array of guessable words
    private int currentWordIndex = 0; // Index of the current word
    private ImageView wordImage; // ImageView to display the image
    private TextView randomWordsTextView; // TextView to display random words
    private TextView levelTxt;
    private String currentWordWithUnderscores; // Current word with underscores
    private String currentWord; // Current word
    private Handler handler = new Handler();
    private boolean hasGameEnded = false;
    private int coinCount = 0;
    private int bombHintCount = 0;
    private int skipHintCount = 0;
    private int shuffleHintCount = 0;
    private TextView coinView;
    private TextView bombView;
    private TextView skipView;
    private TextView shuffleView;
    private TextView plusFive;
    private ImageButton backspace;
    private int hintCounter = 0;
    private VideoView videoView;
    private ImageButton bombBtn;
    private SoundManager soundManager;
    private ImageView giftBox;
    private CardView adCoinCv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playgame);



        soundManager = SoundManager.getInstance(this);


//COIN

        loadCoinCount();
        loadBombHintCount();
        loadSkipHintCount();
        loadShuffleCount();
        loadCurrentWordIndex();
        loadCurrentLevel();


        coinView = findViewById(R.id.coinView);
        shuffleView = findViewById(R.id.shuffleView);
        bombView = findViewById(R.id.bombView);
        skipView = findViewById(R.id.skipView);

        updateCoinCounter();
        updateBombHintCounter();
        updateSkipHintCounter();
        updateShuffleCounter();

        plusFive = findViewById(R.id.plusFive);
        plusFive.setVisibility(View.GONE);


        adCoinCv = findViewById(R.id.addCoinCardView);
        adCoinCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playButtonClickSound();
                startActivity(new Intent(PlaygameActivity.this, Shop.class));
            }
        });


        ImageView homeBtn = findViewById(R.id.home);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playButtonClickSound();
                finish();
            }
        });


        videoView = findViewById(R.id.bomb_blast_video);
        String videoPath;
        videoPath = "android.resource://" + getPackageName() + "/" + R.raw.blast;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        videoView.stopPlayback();

        wordImage = findViewById(R.id.hintImage); // Initialize the ImageView
        randomWordsTextView = findViewById(R.id.randomWords); // Initialize the TextView
        levelTxt = findViewById(R.id.levelTxt);


        // Initialize your wordContainers array (as shown in previous examples)
        wordContainers = new WordContainer[]{
                new WordContainer("Apple", R.drawable.apple_logo, "1"),
                new WordContainer("starbucks", R.drawable.starbucks_logo,"2"),
                new WordContainer("youtube", R.drawable.yt,"3"),
                new WordContainer("Adidas", R.drawable.adidas_logo,"4"),
                new WordContainer("chrome", R.drawable.google_chrome,"5"),
                new WordContainer("facebook", R.drawable.fb,"6"),
                new WordContainer("drive", R.drawable.g_drive,"7"),
                new WordContainer("honda", R.drawable.honda,"8"),
                new WordContainer("caterpillar", R.drawable.caterpillar,"9"),
                new WordContainer("adobe", R.drawable.adobe,"10"),
                new WordContainer("android", R.drawable.android,"11"),
                new WordContainer("huawei", R.drawable.huawei,"12"),
                new WordContainer("gucci", R.drawable.gucci,"13"),
                new WordContainer("hyundai", R.drawable.hyundai,"14"),
                new WordContainer("kfc", R.drawable.kfc,"15"),
                new WordContainer("mastercard", R.drawable.master_card,"16"),
                new WordContainer("mcdonalds", R.drawable.mc_donalds,"17"),
                new WordContainer("mercedes", R.drawable.mercedes,"18"),
                new WordContainer("office", R.drawable.ms_office,"19"),
                new WordContainer("louisvuitton", R.drawable.louis_vuitton,"20"),
                new WordContainer("paypal", R.drawable.paypal,"21"),
                new WordContainer("redbull", R.drawable.redbull,"22"),
                new WordContainer("rolex", R.drawable.rolex,"23"),
                new WordContainer("audi", R.drawable.audi,"24"),
                new WordContainer("toyota", R.drawable.toyota,"25"),
                new WordContainer("pepsi", R.drawable.pepsi,"26"),
                new WordContainer("Tmobile", R.drawable.t_mobile,"27"),
                new WordContainer("shell", R.drawable.shell,"28"),
                new WordContainer("flutter", R.drawable.flutter,"29"),
                new WordContainer("html", R.drawable.html,"30"),
                new WordContainer("pubg", R.drawable.pubg,"31"),
                new WordContainer("nbc", R.drawable.nbc,"32"),
                new WordContainer("lacoste", R.drawable.lacoste,"33"),
                new WordContainer("onedrive", R.drawable.onedrive,"34"),
                new WordContainer("imovie", R.drawable.imovie,"35"),
                new WordContainer("vlc", R.drawable.vlc,"36"),
                new WordContainer("bluestack", R.drawable.bluestack,"37"),
                new WordContainer("garena", R.drawable.garena,"38"),
                new WordContainer("worldcup", R.drawable.worldcup,"39"),
                new WordContainer("netflix", R.drawable.netflix,"40"),
                new WordContainer("nestle", R.drawable.nestle,"41"),
                new WordContainer("xbox", R.drawable.xbox,"42"),
                new WordContainer("shazam", R.drawable.shazam,"43"),
                new WordContainer("skype", R.drawable.skype,"44"),
                new WordContainer("chanel", R.drawable.chanel,"45"),
                new WordContainer("wikipedia", R.drawable.wikipedia,"46"),
                new WordContainer("who", R.drawable.who,"47"),
                new WordContainer("motorola", R.drawable.motorola,"48"),
                new WordContainer("swift", R.drawable.swift,"49"),
                new WordContainer("linux", R.drawable.linux,"50"),
                new WordContainer("airtel", R.drawable.airtel,"51"),
                new WordContainer("reliance", R.drawable.reliance,"52"),
                new WordContainer("telegram", R.drawable.telegram,"53"),
                new WordContainer("oneplus", R.drawable.oneplus,"54"),
                new WordContainer("puma", R.drawable.puma,"55"),
                new WordContainer("newbalance", R.drawable.new_balance,"56"),
                new WordContainer("tesla", R.drawable.tesla,"57"),
                new WordContainer("python", R.drawable.python,"58"),
                new WordContainer("rockstar", R.drawable.rockstar,"59"),
                new WordContainer("realmadrid", R.drawable.real_madrid,"60"),
                new WordContainer("photos", R.drawable.gphotos,"61"),
                new WordContainer("chatgpt", R.drawable.chatgpt,"62"),
                new WordContainer("mitsubishi", R.drawable.mitsubishi,"63"),
                new WordContainer("unilever", R.drawable.unilever,"64"),
                new WordContainer("amazon", R.drawable.amazon,"65"),
                new WordContainer("reebok", R.drawable.reebok,"66"),
                new WordContainer("superman", R.drawable.superman,"67"),
                new WordContainer("volkswagen", R.drawable.volkswagen,"68"),
                new WordContainer("batman", R.drawable.batman,"69"),
                new WordContainer("hewlettpackard", R.drawable.hewlett_packard,"70"),
                new WordContainer("target", R.drawable.target,"71"),
                new WordContainer("microsoft", R.drawable.microsoft,"72"),
                new WordContainer("olympics", R.drawable.olympics,"73"),
                new WordContainer("walmart", R.drawable.walmart,"74"),
                new WordContainer("bmw", R.drawable.bmw_ed,"75"),
                new WordContainer("wwf", R.drawable.wwf,"76"),
                new WordContainer("nike", R.drawable.nike,"77"),
                new WordContainer("twitter", R.drawable.twitter,"78"),
                new WordContainer("spotify", R.drawable.spotify,"79"),
                new WordContainer("bitcoin", R.drawable.bitcoin,"80"),
                new WordContainer("admob", R.drawable.admob,"81"),
                new WordContainer("threads", R.drawable.threads,"82"),
                new WordContainer("brave", R.drawable.brave,"83"),
                new WordContainer("whitehouse", R.drawable.whitehouse,"84"),
                new WordContainer("commonapp", R.drawable.commonapp,"85"),
                new WordContainer("socratic", R.drawable.socratic,"86"),
                new WordContainer("fiver", R.drawable.fiver,"87"),
                new WordContainer("freelancer", R.drawable.freelancer,"88"),
                new WordContainer("googleearth", R.drawable.googleearth,"89"),
                new WordContainer("inshot", R.drawable.inshot,"90"),
                new WordContainer("youtubestudio", R.drawable.youtubestudio,"91"),
                new WordContainer("fruitninja", R.drawable.fruitninja,"92"),
                new WordContainer("meet", R.drawable.meet,"93"),
                new WordContainer("templerun", R.drawable.templerun,"94"),
                new WordContainer("tripadvisor", R.drawable.tripadvisor,"95"),
                new WordContainer("visualstudio", R.drawable.visualstudio,"96"),
                new WordContainer("bluebook", R.drawable.bluebook,"97"),
                new WordContainer("blender", R.drawable.blender,"98"),
                new WordContainer("blackberry", R.drawable.blackberry,"99"),
                new WordContainer("underarmour", R.drawable.underarmour,"100"),
                new WordContainer("carrefour", R.drawable.carrefour,"101"),
                new WordContainer("bing", R.drawable.bing,"102"),
                new WordContainer("nfl", R.drawable.nfl,"103"),
                new WordContainer("petrochina", R.drawable.petrochina,"104"),
                new WordContainer("nickelodeon", R.drawable.nickelodeon,"105"),
                new WordContainer("dove", R.drawable.dove,"106"),
                new WordContainer("unicef", R.drawable.unicef,"107"),
                new WordContainer("godot", R.drawable.godot,"108"),
                new WordContainer("ubisoft", R.drawable.ubisoft,"109"),
                new WordContainer("laliga", R.drawable.laliga,"110"),
                new WordContainer("dreamworks", R.drawable.dreamworks,"111"),
                new WordContainer("warnerbros", R.drawable.warnerbros,"112"),
                new WordContainer("tinder", R.drawable.tinder,"113"),
                new WordContainer("picasa", R.drawable.picasa,"114"),
                new WordContainer("bluray", R.drawable.bluray,"115"),
                new WordContainer("twitch", R.drawable.twitch,"116"),
                new WordContainer("formulaone", R.drawable.formula1,"117"),
                new WordContainer("lamborghini", R.drawable.lamborghini,"118"),
                new WordContainer("windows", R.drawable.windows,"119"),
                new WordContainer("safari", R.drawable.safari,"120"),
                new WordContainer("soundcloud", R.drawable.soundcloud,"121"),
                new WordContainer("uber", R.drawable.uber,"122"),
                new WordContainer("amongus", R.drawable.amongus,"123"),
                new WordContainer("tacobell", R.drawable.tacobell,"124"),
                new WordContainer("subway", R.drawable.subway,"125"),
                new WordContainer("wendys", R.drawable.wendys,"126"),
                new WordContainer("flipcart", R.drawable.flipcart,"127"),
                new WordContainer("shopify", R.drawable.shopify,"128"),
                new WordContainer("steam", R.drawable.steam,"129"),
                new WordContainer("skullcandy", R.drawable.skullcandy,"130"),
                new WordContainer("nasa", R.drawable.nasa,"131"),
                new WordContainer("unreal", R.drawable.unreal,"132"),
                new WordContainer("airbnb", R.drawable.airbnb,"133"),
                new WordContainer("koolaid", R.drawable.koolaid,"134"),
                new WordContainer("doritos", R.drawable.doritos,"135"),
                new WordContainer("waltdisney", R.drawable.waltdisney,"136"),
                new WordContainer("hsbc", R.drawable.hsbc,"137"),
                new WordContainer("cheetos", R.drawable.cheetos,"138"),
                new WordContainer("notion", R.drawable.notion,"139"),
                new WordContainer("nesquik", R.drawable.nesquik,"140"),
                new WordContainer("unity", R.drawable.unity,"141"),
                new WordContainer("roblox", R.drawable.roblox,"142"),
                new WordContainer("westinghouse", R.drawable.westinghouse,"143"),
                new WordContainer("megafon", R.drawable.megafon,"144"),
                new WordContainer("reddit", R.drawable.reddit,"145"),
                new WordContainer("ikea", R.drawable.ikea,"146"),
                new WordContainer("footlocker", R.drawable.footlocker,"147"),
                new WordContainer("word", R.drawable.word,"148"),
                new WordContainer("beats", R.drawable.beats,"149"),
                new WordContainer("natgeo", R.drawable.natgeo,"150"),
                new WordContainer("southwest", R.drawable.southwest,"151"),
                new WordContainer("chevrolet", R.drawable.chevrolet,"152"),
                new WordContainer("schindler", R.drawable.schindler,"153"),
                new WordContainer("dropbox", R.drawable.dropbox,"154"),
                new WordContainer("meta", R.drawable.meta,"155"),
                new WordContainer("newera", R.drawable.newera,"156"),
                new WordContainer("minecraft", R.drawable.minecraft,"157"),
                new WordContainer("shelby", R.drawable.shelby,"158"),
                new WordContainer("mustang", R.drawable.mustang,"159"),
                new WordContainer("jerry", R.drawable.jerry,"160"),
                new WordContainer("messi", R.drawable.messi,"161"),
                new WordContainer("x", R.drawable.x_logo,"162"),
                new WordContainer("barcelona", R.drawable.barcelona,"163"),
                new WordContainer("tom", R.drawable.tom,"164"),
                new WordContainer("fedex", R.drawable.fedex,"165"),
                new WordContainer("columbia", R.drawable.columbia,"167"),
                new WordContainer("pirelli", R.drawable.pirelli,"168"),
                new WordContainer("tiktok", R.drawable.tiktok,"169"),
                new WordContainer("daraz", R.drawable.daraz,"170"),
                new WordContainer("linkedin", R.drawable.linkedin,"171"),
                new WordContainer("upwork", R.drawable.upwork,"172"),
                new WordContainer("ferrari", R.drawable.ferrari,"173"),
                new WordContainer("crocodile", R.drawable.crocodile,"174"),
                new WordContainer("porsche", R.drawable.porsche,"175"),
                new WordContainer("playstation", R.drawable.playstation,"176"),
                new WordContainer("jaguar", R.drawable.jaguar,"177"),
                new WordContainer("mazda", R.drawable.mazda,"178"),
                new WordContainer("pringles", R.drawable.pringles,"179"),
                new WordContainer("dolby", R.drawable.dolby,"180"),
                new WordContainer("asics", R.drawable.asics,"181"),
                new WordContainer("aol", R.drawable.aol,"182"),
                new WordContainer("virgin", R.drawable.virgin,"183"),
                new WordContainer("infiniti", R.drawable.infiniti,"184"),
                new WordContainer("maserati", R.drawable.maserati,"185"),
                new WordContainer("paramount", R.drawable.paramount,"186"),
                new WordContainer("dominos", R.drawable.dominos,"187"),
                new WordContainer("napster", R.drawable.napster,"188"),
                new WordContainer("mgm", R.drawable.mgm,"189"),
                new WordContainer("vodafone", R.drawable.vodafone,"190"),
                new WordContainer("unacademy", R.drawable.unacademy,"191"),
                new WordContainer("lotto", R.drawable.lotto,"192"),
                new WordContainer("pepsico", R.drawable.pepsico,"193"),
                new WordContainer("pathao", R.drawable.pathao,"194"),
                new WordContainer("converse", R.drawable.converse,"195"),
                new WordContainer("yahoo", R.drawable.yahoo,"196"),
                new WordContainer("bajaj", R.drawable.bajaj,"197"),
                new WordContainer("titan", R.drawable.titan,"198"),
                new WordContainer("discovery", R.drawable.discovery,"199"),
                new WordContainer("MorrisGarages", R.drawable.mg,"200"),
                new WordContainer("namecheap", R.drawable.namecheap,"201"),
                new WordContainer("alibaba", R.drawable.alibaba,"202"),
                new WordContainer("bp", R.drawable.bp,"203"),
                new WordContainer("chevron", R.drawable.chevron,"204"),
                new WordContainer("jpmorgan", R.drawable.jpmorgan,"205"),
                new WordContainer("saudiaramco", R.drawable.saudiaramco,"206"),
                new WordContainer("tailwind", R.drawable.tailwind,"207"),
                new WordContainer("generalmotors", R.drawable.generalmotors,"208"),
                new WordContainer("jd", R.drawable.jd,"209"),
                new WordContainer("kroger", R.drawable.kroger,"210"),
                new WordContainer("ntt", R.drawable.ntt,"211"),
                new WordContainer("pfizer", R.drawable.pfizer,"212"),
                new WordContainer("verizon", R.drawable.verizon,"213"),
                new WordContainer("boeing", R.drawable.boeing,"214"),
                new WordContainer("lockheedmartin", R.drawable.lockheed_martin,"215"),
                new WordContainer("merck", R.drawable.merck,"216"),
                new WordContainer("novartis", R.drawable.novartis,"217"),
                new WordContainer("cisco", R.drawable.cisco,"218"),
                new WordContainer("conocophillips", R.drawable.conocophillips,"219"),
                new WordContainer("monster", R.drawable.monster,"220"),
                new WordContainer("totalenergy", R.drawable.totalenergy,"221"),
                new WordContainer("schlumberger", R.drawable.schlumberger,"222"),
                new WordContainer("eni", R.drawable.eni,"223"),
                new WordContainer("generalelectric", R.drawable.general_electric,"224"),
                new WordContainer("gilead", R.drawable.gilead,"225"),
                new WordContainer("danone", R.drawable.danone,"226"),
                new WordContainer("safran", R.drawable.safran,"227"),
                new WordContainer("lilly", R.drawable.lilly,"228"),
                new WordContainer("mrf", R.drawable.mrf,"229"),
                new WordContainer("emirates", R.drawable.emirates,"230"),
                // Add more words and image resources here
        };

        int savedWordIndex = loadCurrentWordIndex();
        currentWordIndex = savedWordIndex;
        // Display the initial word, image, and letters
        displayWordAndLetters(currentWordIndex);

        backspace = findViewById(R.id.backspaceImg);
        backspaceBtn();
        hintBtn();
        bombBtn = findViewById(R.id.bombImg);
        bombBtn.setEnabled(true);
        bombBtn();


        giftBox = findViewById(R.id.giftBox);
        giftBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playButtonClickSound();
                giftBox();
            }
        });

    }


    private int loadCurrentWordIndex() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        return sharedPreferences.getInt("currentWordIndex", 0); // 0 is the default index if not found
    }
    private void collectCoin() {
        coinCount += 5; // Increment the coin count when a word is correctly guessed
        updateCoinCounter(); // Update the display
        saveCoinCount(coinCount);
        plusFive.setText("+5");
        plusFive.setVisibility(View.VISIBLE);

        int currentLevel = currentWordIndex + 1; // Adding 1 because levels are usually 1-based
        saveCurrentLevel(currentLevel);
        saveCurrentWordIndex(currentWordIndex);
    }
    private void giftBox(){
        coinCount += 5000;
        skipHintCount += 5;
        bombHintCount += 5;
        shuffleHintCount += 5;

        soundManager.playCoinEffectSound();

        updateCoinCounter();
        updateBombHintCounter();
        updateShuffleCounter();
        updateSkipHintCounter();

        saveCoinCount(coinCount);
        saveShuffleCount(shuffleHintCount);
        saveBombHintCount(bombHintCount);
        saveSkipHintCount(skipHintCount);

        plusFive.setText("+5000");
        plusFive.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                plusFive.setVisibility(View.GONE);
            }
        }, 800);
    }

    private void saveCurrentWordIndex(int wordIndex) {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentWordIndex", wordIndex);
        editor.apply();
    }
    private void saveCurrentLevel(int level) {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentLevel", level);
        editor.apply();
    }
    private int loadCurrentLevel() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        return sharedPreferences.getInt("currentLevel", 1); // Default level is 1
    }

    private void saveCoinCount(int coinCount) {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("coinCount", coinCount);
        editor.apply();
    }
    private void updateCoinCounter() {
        coinView.setText("  " + coinCount);
    }
    private void loadCoinCount() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePreferences", MODE_PRIVATE);
        coinCount = sharedPreferences.getInt("coinCount", 0); // 0 is the default value if coinCount is not found
        Log.d("CoinCount", "Loaded coin count: " + coinCount);
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

    private void updateBombHintCounter() {
        bombView.setText("" + bombHintCount);
    }
    private void updateSkipHintCounter() {
        skipView.setText("" + skipHintCount);
    }
    private void updateShuffleCounter() {
        shuffleView.setText("" + shuffleHintCount);
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


    private void setLetterClickListener(final TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playButtonClickSound();
                char letter = (char) textView.getTag();
                handleLetterClick(letter);
            }
        });
    }

    private void handleLetterClick(char letter) {
        int index = currentWordWithUnderscores.indexOf('_');
        if (index != -1) {
            StringBuilder builder = new StringBuilder(currentWordWithUnderscores);
            builder.setCharAt(index, letter);
            currentWordWithUnderscores = builder.toString();
            randomWordsTextView.setText(currentWordWithUnderscores);

            // Check if all blanks are filled with letters before calling checkGameWon
            if (!currentWordWithUnderscores.contains("_")) {
                checkGameWon(); // Call checkGameWon when all blanks are filled
            }
        }
    }
    public void onBackspaceClick(View view) {
        currentWordWithUnderscores = getUnderscoredWord(currentWord);
        soundManager.playButtonClickSound();
        randomWordsTextView.setText(currentWordWithUnderscores);
    }




    private String replaceLetter(String currentWord, char letter) {
        StringBuilder newWord = new StringBuilder(currentWord);
        int index = currentWord.indexOf('_');
        while (index >= 0) {
            newWord.setCharAt(index, letter);
            index = currentWord.indexOf('_', index + 1);
        }
        return newWord.toString();
    }
    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
        loadCurrentLevel();
        // Save the coin count when the activity is paused
      saveCurrentWordIndex(currentWordIndex);
    }
    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
        loadCurrentWordIndex();
        loadCurrentLevel();
        saveCurrentWordIndex(currentWordIndex);

        loadCoinCount();
        loadBombHintCount();
        loadSkipHintCount();
        loadShuffleCount();

        updateCoinCounter();
        updateBombHintCounter();
        updateSkipHintCounter();
        updateShuffleCounter();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void hintBtn(){
        ImageView hintBtn = findViewById(R.id.hintImg);
        hintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (skipHintCount < 1){
                    Toast.makeText(PlaygameActivity.this, "Not Enough Hint!", Toast.LENGTH_SHORT).show();
                } else {

                    skipHintCount -= 1;

                    updateSkipHintCounter();
                    saveSkipHintCount(skipHintCount);
                    loadCurrentLevel();

                    /**Use Hints(useHint)**/
                    generateHint();
//                    shuffleLetters(currentWordIndex);
                }
            }
        });
    }
    private void generateHint() {
        if (currentWord == null || currentWord.isEmpty()) {
            return; // Ensure there's a current word to generate a hint for
        }

        if (hintCounter < currentWord.length()) {
            revealAllLetters();
            hintCounter = currentWord.length(); // Mark all letters as revealed
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    correctGuess(); // Call checkGameWon after a 1-second delay
                    bombBtn.setEnabled(true);
                }
            }, 1000);
            hasGameEnded = true;
        }


    }
    private void revealAllLetters() {
        // Reveal the letter in the currentWordWithUnderscores at the specified index
//        StringBuilder hintWord = new StringBuilder(currentWordWithUnderscores);
//        hintWord.setCharAt(index, letter);
//        randomWordsTextView.setText(hintWord.toString());
        StringBuilder hintWord = new StringBuilder(currentWord);
        randomWordsTextView.setText(hintWord.toString());
    }


    public void correctGuess(){
        hintCounter = 0;
        saveCurrentWordIndex(currentWordIndex);
        collectCoin();
        currentWordIndex++;

        if (currentWordIndex >= wordContainers.length) {
            // If we've reached the end, wrap around to the first word
            currentWordIndex = 0;
        }

        // Display the word, image, and letters for the next word
        displayWordAndLetters(currentWordIndex);
        bombBtn.setEnabled(true);
        plusFive.setVisibility(View.GONE);
    }
    private void checkGameWon() {
        if (hasGameEnded) {
            return; // Don't process further if the game has already ended
        }

        String userGuess = randomWordsTextView.getText().toString().replaceAll(" ", ""); // Remove spaces

        if (userGuess.length() == currentWord.length()) {
            if (userGuess.equals(currentWord)) {
                Log.d("Correct", "Game Won");
                soundManager.playCoinEffectSound();
                plusFive.setText("+5");
                plusFive.setVisibility(View.VISIBLE);
                setCorrectLettersToGreen(userGuess);
                hasGameEnded = true; // Set the flag to indicate the game has ended
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        correctGuess();
                    }
                }, 1000);

            } else {
                Log.d("Incorrect", "Game Lost");
                // Set incorrect letters to red (you need to have the appropriate code to change text color)
                setIncorrectLettersToRed(userGuess);

                // Post a delayed task to display the next word after 2 seconds
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        displaySameWordAndLetters(currentWordIndex);
                    }
                }, 1000); // 1000 milliseconds (1 seconds) delay
            }
        }
    }
    private void backspaceBtn() {
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffleHintCount < 1){
                    Toast.makeText(PlaygameActivity.this, "Not Enough Hints", Toast.LENGTH_SHORT).show();
                } else {
                     shuffleHintCount -= 1;

                    updateShuffleCounter();
                    saveShuffleCount(shuffleHintCount);
                    bombBtn.setEnabled(true);
                    shuffleLetters(currentWordIndex);
                }
            }
        });

    }
    private void bombBtn(){

        bombBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bombHintCount < 1){
                    Toast.makeText(PlaygameActivity.this, "Not Enough Hint", Toast.LENGTH_SHORT).show();
                } else {
                    bombHintCount -= 1;

                    updateBombHintCounter(); // Update the display
                    saveBombHintCount(bombHintCount);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.start();
                    soundManager.playBombExplosionSound();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            videoView.setVisibility(View.GONE);
                        }
                    }, 2000);
                    handleBombButtonClick();
                }
            }
        });

    }

    private void handleBombButtonClick() {
        eraseRandomLetters(1);
    }
    private void eraseRandomLetters(int count) {
        char[] textViewIds = {'L', 'B', 'C', 'H', 'E', 'F', 'G', 'D', 'I', 'J', 'K', 'A'};

        List<Integer> availableIndices = new ArrayList<>();
        for (int i = 0; i < textViewIds.length; i++) {
            TextView textView = getTextViewByTag(textViewIds[i]);
            char tag = (char) textView.getTag();
            if (tag != '_' && !currentWord.contains(String.valueOf(tag))) {
                availableIndices.add(i);
            }
        }

        if (availableIndices.size() < count) {
            // Handle the case when there are not enough available indices
            bombHintCount += 1;
            updateBombHintCounter();
            saveBombHintCount(bombHintCount);
            bombBtn.setEnabled(false);
            Toast.makeText(this, "No More Possible Blast", Toast.LENGTH_SHORT).show();
            Log.d("No Possible Blast", "Cant Blast Bomb");


        } else {
            for (int i = 0; i < count; i++) {
                int randomIndex = availableIndices.get(new Random().nextInt(availableIndices.size()));
                TextView textView = getTextViewByTag(textViewIds[randomIndex]);
                textView.setText("_");
                textView.setTag('_');
                availableIndices.remove(Integer.valueOf(randomIndex));
            }
        }
    }

    private TextView getTextViewByTag(char tag) {
        String viewId = "textView" + tag;
        int resourceId = getResources().getIdentifier(viewId, "id", getPackageName());
        return findViewById(resourceId);
    }


    private boolean isLetterTextView(char textViewId) {
        String viewId = "textView" + textViewId;
        int resourceId = getResources().getIdentifier(viewId, "id", getPackageName());
        TextView textView = findViewById(resourceId);
        if (textView != null) {
            char tag = (char) textView.getTag();
            return Character.isLetter(tag);
        }
        return false;
    }


    private void setCorrectLettersToGreen(String userGuess) {
        SpannableString spannableString = new SpannableString(currentWordWithUnderscores);

        if (userGuess.equals(currentWord)) {
            ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.GREEN);
            spannableString.setSpan(redSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        randomWordsTextView.setText(spannableString);
    }

    private void setIncorrectLettersToRed(String userGuess) {
        SpannableString spannableString = new SpannableString(currentWordWithUnderscores);

        if (!userGuess.equals(currentWord)) {
            ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
            spannableString.setSpan(redSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        randomWordsTextView.setText(spannableString);
    }

    private String getUnderscoredWord(String word) {

        int wordLength = word.length();
        StringBuilder underscoredWord = new StringBuilder();

        for (int i = 0; i < wordLength; i++) {
            underscoredWord.append("_");
            if (i < wordLength - 1) {
                underscoredWord.append(" "); // Add a space between underscores
            }
        }
        return underscoredWord.toString();
    }


    private void displaySameWordAndLetters(int wordIndex){
        hasGameEnded = false;
        WordContainer selectedWord = wordContainers[wordIndex];
        currentWord = selectedWord.getWord().toUpperCase();

        // Set the image resource for the selected word
        wordImage.setImageResource(selectedWord.getImageResource());
        wordImage.setVisibility(View.VISIBLE);

        levelTxt.setText(selectedWord.getLvl());
        levelTxt.setVisibility(View.VISIBLE);

        // Set the word in the TextView
        currentWordWithUnderscores = getUnderscoredWord(currentWord);
        randomWordsTextView.setText(currentWordWithUnderscores);
    }

    private void shuffleLetters(int wordIndex){
        // Shuffle the letters of the word
        List<Character> shuffledLetters = new ArrayList<>();
        for (char letter : currentWord.toCharArray()) {
            shuffledLetters.add(letter);
        }
        Collections.shuffle(shuffledLetters);

        // Assign the shuffled letters to TextViews (A, B, C, etc.)
        char[] textViewIds = {'L', 'B', 'C', 'H', 'E', 'F', 'G', 'D', 'I', 'J', 'K', 'A'};
        for (int i = 0; i < shuffledLetters.size(); i++) {
            char letter = shuffledLetters.get(i);
            if (i < textViewIds.length) {
                String viewId = "textView" + textViewIds[i];
                int resourceId = getResources().getIdentifier(viewId, "id", getPackageName());
                TextView textView = findViewById(resourceId);

                if (textView != null) {
                    textView.setText(String.valueOf(letter));
                    textView.setTag(letter); // Set the tag to the letter
                    setLetterClickListener(textView); // Set a click listener
                }


            }
        }

        // Generate and assign random letters to the remaining TextViews
        for (int i = shuffledLetters.size(); i < textViewIds.length; i++) {
            char randomLetter = (char) ('A' + new Random().nextInt(26)); // Generate a random letter
            String viewId = "textView" + textViewIds[i];
            int resourceId = getResources().getIdentifier(viewId, "id", getPackageName());
            TextView textView = findViewById(resourceId);

            if (textView != null) {
                textView.setText(String.valueOf(randomLetter));
                textView.setTag(randomLetter); // Set the tag to the random letter
                setLetterClickListener(textView); // Set a click listener

            }
        }
    }

    private void displayWordAndLetters(int wordIndex) {

        hasGameEnded = false;
        WordContainer selectedWord = wordContainers[wordIndex];
        currentWord = selectedWord.getWord().toUpperCase();

        // Set the image resource for the selected word
        wordImage.setImageResource(selectedWord.getImageResource());
        wordImage.setVisibility(View.VISIBLE);

        levelTxt.setText(selectedWord.getLvl());
        levelTxt.setVisibility(View.VISIBLE);

        // Set the word in the TextView
        currentWordWithUnderscores = getUnderscoredWord(currentWord);
        randomWordsTextView.setText(currentWordWithUnderscores);

        // Shuffle the letters of the word
        List<Character> shuffledLetters = new ArrayList<>();
        for (char letter : currentWord.toCharArray()) {
            shuffledLetters.add(letter);
        }
        Collections.shuffle(shuffledLetters);

        // Assign the shuffled letters to TextViews (A, B, C, etc.)
        char[] textViewIds = {'L', 'B', 'C', 'H', 'E', 'F', 'G', 'D', 'I', 'J', 'K', 'A'};
        for (int i = 0; i < shuffledLetters.size(); i++) {
            char letter = shuffledLetters.get(i);
            if (i < textViewIds.length) {
                String viewId = "textView" + textViewIds[i];
                int resourceId = getResources().getIdentifier(viewId, "id", getPackageName());
                TextView textView = findViewById(resourceId);

                if (textView != null) {
                    textView.setText(String.valueOf(letter));
                    textView.setTag(letter); // Set the tag to the letter
                    setLetterClickListener(textView); // Set a click listener
                }


            }
        }

        // Generate and assign random letters to the remaining TextViews
        for (int i = shuffledLetters.size(); i < textViewIds.length; i++) {
            char randomLetter = (char) ('A' + new Random().nextInt(26)); // Generate a random letter
            String viewId = "textView" + textViewIds[i];
            int resourceId = getResources().getIdentifier(viewId, "id", getPackageName());
            TextView textView = findViewById(resourceId);

            if (textView != null) {
                textView.setText(String.valueOf(randomLetter));
                textView.setTag(randomLetter); // Set the tag to the random letter
                setLetterClickListener(textView); // Set a click listener

            }
        }
    }

}