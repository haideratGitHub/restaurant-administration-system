package com.example.burgerandgrill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Settings extends AppCompatActivity {

    EditText adminUsername;
    EditText adminPassword;
    Button save;

    private static final String COLLECTION = "USERS";
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#ffcc0000"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        adminUsername = findViewById(R.id.admin_username);
        adminPassword = findViewById(R.id.admin_password);
        save = findViewById(R.id.btn_save_admin_details);

        firebaseFirestore = firebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection(COLLECTION);
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            String userEmail = documentSnapshot.get("email").toString();
                            String userPassword = documentSnapshot.get("password").toString();
                            String userType = documentSnapshot.get("type").toString();

                            if (userType.equals("admin")){
                                setText(userEmail,userPassword);
                            }

                        }
                    }
                });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputs()){
                    saveDetails();
                    success();
                }else{
                    makeToast();
                }
            }
        });
    }
    private void makeToast(){
        Toast.makeText(this,"Fields cannot be empty!!", Toast.LENGTH_SHORT).show();
    }
    private void success(){
        Toast.makeText(this,"Details updated successfully", Toast.LENGTH_SHORT).show();
    }
    private void setText(String name, String password){
        adminUsername.setText(name);
        adminPassword.setText(password);
    }
    private boolean checkInputs(){
        if(!TextUtils.isEmpty(adminUsername.getText().toString())){
            if (!TextUtils.isEmpty(adminPassword.getText().toString())){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    private void saveDetails(){
        firebaseFirestore = firebaseFirestore.getInstance();
        DocumentReference adminDoc = firebaseFirestore.document("USERS/Admin");
        Map<String,String> admin = new HashMap<>();
        admin.put("email",adminUsername.getText().toString());
        admin.put("type","admin");
        admin.put("password",adminPassword.getText().toString());
        adminDoc.set(admin, SetOptions.merge());
//        final CollectionReference collectionReference = firebaseFirestore.collection(COLLECTION);
//        collectionReference.whereEqualTo("type","admin")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            for(DocumentSnapshot documentSnapshot: task.getResult()){
//                                String docID = documentSnapshot.getId();
//                                collectionReference.document(docID).update("email",adminUsername.getText().toString());
//                                collectionReference.document(docID).update("password",adminPassword.getText().toString());
//                            }
//                        }
//                    }
//                });
    }
}
