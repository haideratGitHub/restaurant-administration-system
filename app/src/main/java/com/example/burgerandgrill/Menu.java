package com.example.burgerandgrill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Menu extends AppCompatActivity implements AddIngredientDialog.ExampleDialogListener{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    CardView addMenuItem;
    CardView addIngredient;
    EditText ingredientName;
    EditText ingredientQuantity;

    private FirebaseFirestore firebaseFirestore;
    ArrayList<MenuItem> exampleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        addMenuItem = findViewById(R.id.add_menu_item);
        addMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAddMenuItem();
            }
        });

        addIngredient = findViewById(R.id.add_ingredient);
        ingredientName = findViewById(R.id.ingredient_name);
        ingredientQuantity = findViewById(R.id.ingredient_quantity);
        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        firebaseFirestore = firebaseFirestore.getInstance();
        CollectionReference collectionReference2 = firebaseFirestore.collection("MENUITEM");
        collectionReference2.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            String name = documentSnapshot.get("name").toString();
                            String price = documentSnapshot.get("price").toString();
                            String type = documentSnapshot.get("type").toString();
                            //TODO
                            //ingredients yet to be retrieved
                            //for use in display "tap fo details" info
                            //update
                            //delete

                            if(type.contains("Burger")){
                                exampleList.add(new MenuItem(R.drawable.burger, name, "Rs. " + price + " (tap for details)"));
                            }
                            else if(type.equals("Sandwich")){
                                exampleList.add(new MenuItem(R.drawable.sandwich, name, "Rs. " + price + " (tap for details)"));
                            }
                            else if(type.equals("Wrap")){
                                exampleList.add(new MenuItem(R.drawable.wrap, name, "Rs. " + price + " (tap for details)"));
                            }


                            mAdapter = new MenuListAdapter(exampleList);
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.d("error",e.toString());
            }
        });

        //exampleList.add(new MenuItem(R.drawable.burger, "The Ultimate mold", "Rs. 350 (tap for details)"));

    }

    private void gotoAddMenuItem(){

        Intent intent = new Intent(this,AddMenuItem.class);
        startActivity(intent);
    }
    private void openDialog(){
        AddIngredientDialog exampleDialog = new AddIngredientDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }
    @Override
    public void applyTexts(String name, String quantity, String unit) {
        //save ingredient info in firebase
        firebaseFirestore = firebaseFirestore.getInstance();
        IngredientModel ingredientModel = new IngredientModel(name,quantity,unit);
        firebaseFirestore.collection("INGREDIENT").add(ingredientModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("error",e.toString());
                    }
                });
    }
}
