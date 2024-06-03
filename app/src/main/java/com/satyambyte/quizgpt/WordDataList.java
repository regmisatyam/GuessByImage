package com.satyambyte.quizgpt;

import java.util.ArrayList;
import java.util.List;

public class WordDataList {
    public static List<WordContainer> getWordContainers() {
        List<WordContainer> wordContainers = new ArrayList<>();

        wordContainers.add(new WordContainer("Apple", R.drawable.apple_logo, "1"));
        wordContainers.add(new WordContainer("Satyambyte", R.drawable.satyambyte_bright, "2"));
        wordContainers.add(new WordContainer("Coin", R.drawable.coin, "3"));
        // Add more word containers here

        return wordContainers;
    }
}
