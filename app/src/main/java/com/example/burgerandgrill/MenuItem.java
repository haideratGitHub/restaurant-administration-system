package com.example.burgerandgrill;

import java.util.List;

public class MenuItem {
    private int mImageResource; //image of burger,sandwich or wrap
    private String mText1; //name of menu item
    private String mText2; //price of menu item
    private String type;
    private String size;
    private List<String> iname;
    private List<String> iquantity;
    private List<String> iunit;

    public MenuItem(int imageResource, String text1, String text2, String type, String size, List<String> iname, List<String> iquantity, List<String> iunit) {
        mImageResource = imageResource;
        mText1 = text1;
        mText2 = text2;
        this.type = type;
        this.size = size;
        this.iname = iname;
        this.iquantity = iquantity;
        this.iunit = iunit;
    }

    public int getmImageResource() {
        return mImageResource;
    }

    public void setmImageResource(int mImageResource) {
        this.mImageResource = mImageResource;
    }

    public String getmText1() {
        return mText1;
    }

    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }

    public String getmText2() {
        return mText2;
    }

    public void setmText2(String mText2) {
        this.mText2 = mText2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public List<String> getIname() {
        return iname;
    }

    public void setIname(List<String> iname) {
        this.iname = iname;
    }

    public List<String> getIquantity() {
        return iquantity;
    }

    public void setIquantity(List<String> iquantity) {
        this.iquantity = iquantity;
    }

    public List<String> getIunit() {
        return iunit;
    }

    public void setIunit(List<String> iunit) {
        this.iunit = iunit;
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
