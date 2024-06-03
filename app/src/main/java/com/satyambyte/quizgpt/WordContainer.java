package com.satyambyte.quizgpt;

public class WordContainer {

    private final String word; // The guessable word
    private final int imageResource;
    private final String lvl;
    public WordContainer(String word, int imageResource, String lvl) {
        this.word = word;
        this.lvl = lvl;
        this.imageResource = imageResource;
    }

    public String getWord() {
        return word;
    }
    public int getImageResource() {
        return imageResource;
    }
    public String getLvl(){
        return lvl;
    }

}
