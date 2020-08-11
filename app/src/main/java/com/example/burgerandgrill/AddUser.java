package com.example.burgerandgrill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddUser extends AppCompatActivity {

    private Button viewAllUsers;
    private EditText username;
    private EditText password;
    private RadioGroup radioGroup;
    private Button save;
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        viewAllUsers = findViewById(R.id.view_all_users);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        radioGroup = findViewById(R.id.groupradio);
        save = findViewById(R.id.save_user);
        progressBar = findViewById(R.id.add_user_progressBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#ffcc0000"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final RadioButton[] radioButton = new RadioButton[1];
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton[0] = (RadioButton)group.findViewById(checkedId);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * username.getText().toString().matches(emailPattern)
                 * removing email pattern verification from login
                 * now username will be used to login
                 */

                if(true){
                    if(password.getText().length() >= 6){
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        if(selectedId == -1){
                            showRadioButtonToast();
                        }else{
                            progressBar.setVisibility(View.VISIBLE);
                            save.setVisibility(View.GONE);
                            firebaseFirestore = firebaseFirestore.getInstance();
                            RadioButton radioButton = (RadioButton)radioGroup.findViewById(selectedId);
                            String userName = username.getText().toString();
                            String userPassword = password.getText().toString();
                            String userType = radioButton.getText().toString();

                            User newUser = new User(userName,userPassword,userType);
                            firebaseFirestore.collection("USERS").add(newUser)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            success();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBar.setVisibility(View.GONE);
                                            save.setVisibility(View.VISIBLE);
                                            Log.d("error",e.toString());
                                        }
                                    });
                        }

                    }else{
                        showPasswordToast();
                    }
                }else{
                    showEmailToast();
                }

            }
        });
        viewAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoViewAllUsers();
            }
        });
    }

    private void gotoViewAllUsers(){
        final ArrayList<String> usersList = new ArrayList<>();


        Intent intent = new Intent(this,ViewAllUsers.class);
        startActivity(intent);
    }

    private void success(){
        progressBar.setVisibility(View.GONE);
        save.setVisibility(View.VISIBLE);
        username.setText("");
        password.setText("");
        radioGroup.clearCheck();
        Toast.makeText(this,"User Added Successfully",Toast.LENGTH_SHORT).show();
    }
    private void showEmailToast(){
        Toast.makeText(this,"Please enter valid email!",Toast.LENGTH_SHORT).show();
    }
    private void showPasswordToast(){
        Toast.makeText(this,"Password must be atleast 6 characters long!!",Toast.LENGTH_SHORT).show();
    }
    private void showRadioButtonToast(){
        Toast.makeText(this,"Please select user type!!",Toast.LENGTH_SHORT).show();
    }

    private void checkInputs(){
        if(!TextUtils.isEmpty(username.getText())){
            if(!TextUtils.isEmpty(password.getText())){
                save.setEnabled(true);
                save.setTextColor(Color.BLACK);
            }
            else{
                save.setEnabled(false);
                save.setTextColor(Color.GRAY);

            }
        }
        else{
            save.setEnabled(false);
            save.setTextColor(Color.GRAY);

        }
    }
}
