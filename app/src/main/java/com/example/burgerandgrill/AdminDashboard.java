package com.example.burgerandgrill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class AdminDashboard extends AppCompatActivity {

    private CardView addUser;
    private CardView inventory;
    private CardView sales;
    private CardView settings;
    private CardView menu;
    private CardView takeOrder;
    private CardView homeDelivery;
    private CardView promotions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        addUser = findViewById(R.id.add_user);
        inventory = findViewById(R.id.inventory);
        sales = findViewById(R.id.sales);
        settings = findViewById(R.id.settings);
        menu = findViewById(R.id.create_menu);
        takeOrder = findViewById(R.id.take_order);
        homeDelivery = findViewById(R.id.home_delivery);
        promotions = findViewById(R.id.promotions);


        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAddUser();

            }
        });
        inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoInventory();
            }
        });
        sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSales();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSettings();
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMenu();
            }
        });
        takeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTakeOrder();
            }
        });
        homeDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoHomeDelivery();
            }
        });

    }
    private void gotoAddUser(){
        final Intent intent = new Intent(this,AddUser.class);
        startActivity(intent);
    }
    private void gotoInventory(){
        final Intent intent = new Intent(this,Inventory.class);
        startActivity(intent);
    }
    private void gotoSales(){
        final Intent intent = new Intent(this,Sales.class);
        startActivity(intent);
    }
    private void gotoSettings(){
        final Intent intent = new Intent(this,Settings.class);
        startActivity(intent);
    }
    private void gotoMenu(){
        final Intent intent = new Intent(this,Menu.class);
        startActivity(intent);
    }
    private void gotoTakeOrder(){
        final Intent intent = new Intent(this,TakeOrder.class);
        startActivity(intent);
    }
    private void gotoHomeDelivery(){
        final Intent intent = new Intent(this,HomeDelivery.class);
        startActivity(intent);
    }

}
