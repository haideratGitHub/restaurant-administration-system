package com.example.burgerandgrill;

import java.util.ArrayList;
import java.util.List;

public class OrderModel {
    private String productName;
    private String productType; //small, medium, large, none
    private String count; //how many same type of item ordered
    private String price;

    //For updating inventory, not to be saved in firebase
    private List<String> iname;
    private List<String> iquantity;
    private List<String> iunit;

    public OrderModel(String productName, String productType, String count, String price, List<String> iname, List<String> iquantity, List<String> iunit) {
        this.productName = productName;
        this.productType = productType;
        this.count = count;
        this.price = price;
        this.iname = iname;
        this.iquantity = iquantity;
        this.iunit = iunit;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
