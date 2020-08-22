package com.example.burgerandgrill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Promotions extends AppCompatActivity {

    EditText promoMessage;
    Button sendToAll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotions);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#ffcc0000"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        promoMessage = findViewById(R.id.promo_message);
        sendToAll = findViewById(R.id.btn_send_to_all);
        sendToAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInputs()){
                    onSend(promoMessage.getText().toString());
                    promoMessage.setText("");
                    makeToast();
                }else{
                    makeInputToast();
                }

            }
        });

    }
    private void makeInputToast(){
        Toast.makeText(this,"Enter promotion message!!",Toast.LENGTH_SHORT).show();
    }
    private boolean checkInputs(){
        if(!TextUtils.isEmpty(promoMessage.getText().toString())){
            return true;
        }else{
            return false;
        }
    }
    private void makeToast(){
        Toast.makeText(this,"sending...",Toast.LENGTH_SHORT).show();
    }
    private void onSend(String sms){
        if(checkPermission(Manifest.permission.SEND_SMS)){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("03078780061",null,sms,null,null);

        }else{
            Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this,permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }
}
