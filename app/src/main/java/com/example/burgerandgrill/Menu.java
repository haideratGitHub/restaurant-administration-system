package com.example.burgerandgrill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class Menu extends AppCompatActivity {

    CardView view;
    CardView add;
    CardView edit;
    CardView delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        view = findViewById(R.id.view_menu);
        add = findViewById(R.id.add_menu_item);
        edit = findViewById(R.id.edit_menu_item);
        delete = findViewById(R.id.delete_menu_item);

        final ScrollView sv = new ScrollView(this);
        final LinearLayout ll = new LinearLayout(this);
        final EditText et = new EditText(this);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }
}
