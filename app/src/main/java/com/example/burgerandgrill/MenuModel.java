package com.example.burgerandgrill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuModel {

    String name;
    String price;
    String type;
    String size;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    //ingredient list in 3 maps - corresponding values name,quantity,unit
    List<String> iname;
    List<String> iquantity;
    List<String> iunit;

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

    public MenuModel(String name, String price, String type, String size, List<String> iname, List<String> iquantity, List<String> iunit) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.iname = iname;
        this.iquantity = iquantity;
        this.iunit = iunit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

}
