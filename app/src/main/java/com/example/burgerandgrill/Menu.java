package com.example.burgerandgrill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity implements AddIngredientDialog.ExampleDialogListener, EditMenuItemDialog.IExampleDialogListener{

    private RecyclerView mRecyclerView;
    private MenuListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    CardView addMenuItem;
    CardView addIngredient;
    EditText ingredientName;
    EditText ingredientQuantity;

    private FirebaseFirestore firebaseFirestore;
    ArrayList<MenuItem> exampleList = new ArrayList<>();
    int editWhichMenuItem = -1; //global to use in applyQuantity

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

        getDataFromFirebase();

    }
    public void checkfun(){
        Toast.makeText(this,"Clicked",Toast.LENGTH_SHORT).show();
    }

    private void getDataFromFirebase(){
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
                            String size = documentSnapshot.get("size").toString();
                            List<String> iname = (List<String>) documentSnapshot.get("iname");
                            List<String> iquantity = (List<String>) documentSnapshot.get("iquantity");
                            List<String> iunit = (List<String>) documentSnapshot.get("iquantity");

                            //TODO for use in display "tap fo details" info
                            //update


                            if(type.contains("Burger")){
                                exampleList.add(new MenuItem(R.drawable.burger, name,  price,type,size,iname,iquantity,iunit));
                            }
                            else if(type.equals("Sandwich")){
                                exampleList.add(new MenuItem(R.drawable.sandwich, name, price ,type,size,iname,iquantity,iunit));
                            }
                            else if(type.equals("Wrap")){
                                exampleList.add(new MenuItem(R.drawable.wrap, name,  price ,type,size,iname,iquantity,iunit));
                            }


                            mAdapter = new MenuListAdapter(exampleList);
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);
                            adapterOnClickListener();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.d("error",e.toString());
            }
        });
    }

    private void adapterOnClickListener(){
        mAdapter.setOnItemClickListener(new MenuListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                checkfun();//TODO display complete info of menu item
            }
            @Override
            public void onEditMenuItemClick(int position){
                editWhichMenuItem = position;
                editMenuItem();
            }
            @Override
            public void onDeleteMenuItemClick(final int position){
                AlertDialog.Builder builder = new AlertDialog.Builder(mRecyclerView.getContext());
                builder.setMessage("This menu item will be permanently deleted. Are you sure ?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteMenuItem(position);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();

            }


        });
    }

    private void editMenuItem(){
        EditMenuItemDialog exampleDialog = new EditMenuItemDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }
    @Override
    public void applyQuantity(final String updatedPrice) {
//        String str = exampleList.get(editWhichMenuItem).getText2();
//        //regex to separate strings and numbers from strings
//        String[] part = str.split("[^A-Z0-9]+|(?<=[A-Z])(?=[0-9])|(?<=[0-9])(?=[A-Z])");
//        //String[] part1 = part[1].split("(?=\\d)(?<=\\D)");
//        int prevPrice = Integer.parseInt(part[1]);
//        int temp_newPrice = prevPrice + Integer.parseInt(updatedQuantity);
//        final String newPrice = String.valueOf(temp_newPrice);

        final CollectionReference collectionReference1 = firebaseFirestore.collection("MENUITEM");
        collectionReference1.whereEqualTo("name",exampleList.get(editWhichMenuItem).getText1())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot: task.getResult()){
                                String docID = documentSnapshot.getId();
                                collectionReference1.document(docID).update("price",updatedPrice);

                                View v = mRecyclerView.getChildAt(editWhichMenuItem);
                                TextView t = v.findViewById(R.id.textView2);
                                t.setText("Rs. " + updatedPrice + " (tap for details)");
                            }
                        }
                    }
                });
    }
    private void deleteMenuItem(int itemNumber){
        //remove from firebase
        final CollectionReference collectionReference1 = firebaseFirestore.collection("MENUITEM");
        collectionReference1.whereEqualTo("name",exampleList.get(itemNumber).getText1())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot: task.getResult()){
                                String docID = documentSnapshot.getId();
                                collectionReference1.document(docID).delete();

                            }
                        }
                    }
                });

        //removed from display
        exampleList.remove(itemNumber);
        mAdapter = new MenuListAdapter(exampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        adapterOnClickListener();
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
