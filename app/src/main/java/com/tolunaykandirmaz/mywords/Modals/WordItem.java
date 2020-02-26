package com.tolunaykandirmaz.mywords.Modals;

public class WordItem {
    private int id;
    private String nativeWord;
    private String foreignWord;
    private byte[] image;

    public WordItem(int id, String nativeWord, String foreignWord, byte[] image){
        this.id = id;
        this.nativeWord = nativeWord;
        this.foreignWord = foreignWord;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getNativeWord() {
        return nativeWord;
    }

    public String getForeignWord() {
        return foreignWord;
    }

    public byte[] getImage() {
        return image;
    }
}
