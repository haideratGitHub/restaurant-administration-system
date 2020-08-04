package com.example.burgerandgrill;

import java.util.List;

public class DeliverySaveModel {
    String date;
    String type;
    String discountGiven;
    String finalBill;
    String customerName;
    String customerPhoneNumber;
    String customerAddress;
    List<String> productName;
    List<String> productType;
    List<String> count;
    List<String> price;

    public DeliverySaveModel(String date, String type, String discountGiven, String finalBill, String customerName, String customerPhoneNumber, String customerAddress, List<String> productName, List<String> productType, List<String> count, List<String> price) {
        this.date = date;
        this.type = type;
        this.discountGiven = discountGiven;
        this.finalBill = finalBill;
        this.customerName = customerName;
        this.customerPhoneNumber = customerPhoneNumber;
        this.customerAddress = customerAddress;
        this.productName = productName;
        this.productType = productType;
        this.count = count;
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiscountGiven() {
        return discountGiven;
    }

    public void setDiscountGiven(String discountGiven) {
        this.discountGiven = discountGiven;
    }

    public String getFinalBill() {
        return finalBill;
    }

    public void setFinalBill(String finalBill) {
        this.finalBill = finalBill;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public List<String> getProductName() {
        return productName;
    }

    public void setProductName(List<String> productName) {
        this.productName = productName;
    }

    public List<String> getProductType() {
        return productType;
    }

    public void setProductType(List<String> productType) {
        this.productType = productType;
    }

    public List<String> getCount() {
        return count;
    }

    public void setCount(List<String> count) {
        this.count = count;
    }

    public List<String> getPrice() {
        return price;
    }

    public void setPrice(List<String> price) {
        this.price = price;
    }
}
