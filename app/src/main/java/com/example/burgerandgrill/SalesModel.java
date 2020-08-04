package com.example.burgerandgrill;

public class SalesModel {
    String saleType;
    String saleDate;
    String saleBill;

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public String getSaleBill() {
        return saleBill;
    }

    public void setSaleBill(String saleBill) {
        this.saleBill = saleBill;
    }

    public SalesModel(String saleType, String saleDate, String saleBill) {
        this.saleType = saleType;
        this.saleDate = saleDate;
        this.saleBill = saleBill;
    }
}
