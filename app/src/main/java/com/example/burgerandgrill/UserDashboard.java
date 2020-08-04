package com.example.burgerandgrill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UserDashboard extends AppCompatActivity {

    private CardView takeOrder;
    private CardView homeDelivery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);


        takeOrder = findViewById(R.id.take_order_user);
        homeDelivery = findViewById(R.id.home_delivery_user);

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

    private void gotoTakeOrder(){
        final Intent intent = new Intent(this,TakeOrder.class);
        startActivity(intent);
    }
    private void gotoHomeDelivery(){
        final Intent intent = new Intent(this,HomeDelivery.class);
        startActivity(intent);
    }
}
