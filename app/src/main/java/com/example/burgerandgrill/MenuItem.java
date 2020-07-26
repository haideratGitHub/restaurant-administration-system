package com.example.burgerandgrill;

public class MenuItem {
    private int mImageResource; //image of burger,sandwich or wrap
    private String mText1; //name of menu item
    private String mText2; //price of menu item
    public MenuItem(int imageResource, String text1, String text2) {
        mImageResource = imageResource;
        mText1 = text1;
        mText2 = text2;
    }
    public int getImageResource() {
        return mImageResource;
    }
    public String getText1() {
        return mText1;
    }
    public String getText2() {
        return mText2;
    }
}
