package com.example.burgerandgrill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button login;
    ProgressBar progressBar;
    private static final String COLLECTION = "USERS";
    private static final String DOCUMENT_ADMIN = "/Admin";
    private static final String DOCUMENT_EMPLOYEE = "/Employee";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        login = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.login_progressBar);


        email.addTextChangedListener(new TextWatcher() {
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
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore = firebaseFirestore.getInstance();
                checkInputsPattern();

//                firebaseAuth = firebaseAuth.getInstance();
//                String e = email.getText().toString();
//                String p = password.getText().toString();
//                firebaseAuth.createUserWithEmailAndPassword(e,p)
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if(task.isSuccessful()){
//                                    checkInputsPattern();
//                                }else{
//                                    String e = task.getException().getMessage();
//                                    String c = e;
//                                }
//                            }
//                        });
                //checkInputsPattern();
            }
        });


    }
    private void checkInputs(){
        if(!TextUtils.isEmpty(email.getText())){
            if(!TextUtils.isEmpty(password.getText())){
                login.setEnabled(true);
                login.setTextColor(Color.parseColor("#ffcc0000"));
            }
            else{
                login.setEnabled(false);
                login.setTextColor(Color.GRAY);

            }
        }
        else{
            login.setEnabled(false);
            login.setTextColor(Color.GRAY);

        }
    }
    private void makeToastPassword(){
        login.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this,"Incorrect Password!!",Toast.LENGTH_SHORT).show();
    }
    private void makeToastNoUser(){
        login.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this,"NO USER FOUND!! Make sure to add corrrect credentials!",Toast.LENGTH_SHORT).show();
    }

    private void checkInputsPattern(){
//        email.getText().toString().matches(emailPattern)
        if(true){
            DocumentReference documentReference1 = firebaseFirestore.document(COLLECTION + DOCUMENT_ADMIN);
            DocumentReference documentReference2 = firebaseFirestore.document(COLLECTION + DOCUMENT_EMPLOYEE);
            CollectionReference collectionReference = firebaseFirestore.collection(COLLECTION);
            login.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            final int[] count = {0}; //to check count of all user. if all users checked and not found then make toast else not
            collectionReference.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                                String userEmail = documentSnapshot.get("email").toString();
                                String userPassword = documentSnapshot.get("password").toString();
                                String userType = documentSnapshot.get("type").toString();
                                String currentEmail = email.getText().toString();
                                String currentPassword = password.getText().toString();
                                if(userEmail.equals(currentEmail)){
                                    if(userPassword.equals(currentPassword)){
                                        if(userType.equals("admin")){
                                            adminLogIn();
                                        }else{
                                            employeeLogIn();
                                        }
                                    }else{
                                        makeToastPassword();
                                        break;
                                    }
                                }else{
                                    count[0] = count[0] + 1;
                                }

                            }
                            if(count[0] == queryDocumentSnapshots.size()){
                                makeToastNoUser();
                            }
                        }
                    });
//            firebaseFirestore = firebaseFirestore.getInstance();
//            Map<Object,String> userdata = new HashMap<>();
//            userdata.put("email",email.getText().toString());
//            firebaseFirestore.collection("USERS").document("Admin").set(userdata)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            startActivity(intent);
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("error",e.toString());
//                        }
//                    });


//            firebaseFirestore.collection("USERS")
//                    .add(userdata)
//                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentReference> task) {
//                            startActivity(intent);
//                        }
//                    });
            
        }
        else{
            Toast.makeText(this,"Please enter valid email!",Toast.LENGTH_SHORT).show();
        }
    }

    private void adminLogIn(){
        final Intent intent = new Intent(this,AdminDashboard.class);
        startActivity(intent);
        this.finish();
    }
    private void employeeLogIn(){
        final Intent intent = new Intent(this,UserDashboard.class);
        startActivity(intent);
        this.finish();
    }

}
