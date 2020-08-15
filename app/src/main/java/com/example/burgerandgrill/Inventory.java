package com.example.burgerandgrill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Map;

public class Inventory extends AppCompatActivity implements AddQuantityInventoryDialog.IExampleDialogListener{

    private RecyclerView mRecyclerView;
    private InventoryListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FirebaseFirestore firebaseFirestore;
    final ArrayList<IngredientModel> inventoryList = new ArrayList<IngredientModel>();

    //for updation global variable
    String tempName;
    String tempQuantity;
    String tempUnit;
    int ingredientNumberInList = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#ffcc0000"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        //ingredient_list will later be replaced with inventory list with additional add feature
        mRecyclerView = findViewById(R.id.inventory_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        firebaseFirestore = firebaseFirestore.getInstance();
        CollectionReference collectionReference1 = firebaseFirestore.collection("INGREDIENT");
        collectionReference1.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            String ingredientName = documentSnapshot.get("name").toString();
                            //String ingredientQuantityAvailable = documentSnapshot.get("quantity").toString();
                            String ingredientQuantityAvailable = documentSnapshot.get("quantity").toString();
                            String quantityUnit = documentSnapshot.get("unit").toString();
                            inventoryList.add(new IngredientModel(ingredientName,ingredientQuantityAvailable,quantityUnit));
                            mAdapter = new InventoryListAdapter(inventoryList); //make inventoryListAdapter
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);

                            mAdapter.setOnItemClickListener(new InventoryListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {

                                }

                                @Override
                                public void onAddQuantityClick(int position) {
                                    tempName = inventoryList.get(position).name;
                                    tempQuantity = inventoryList.get(position).quantity;
                                    tempUnit = inventoryList.get(position).unit;
                                    ingredientNumberInList = position;
                                    openDialog(position);
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.d("error",e.toString());
            }
        });
    }


    private void openDialog(int position){
        //Toast.makeText(this,"Open dialog to update quantity",Toast.LENGTH_SHORT).show();
        AddQuantityInventoryDialog exampleDialog = new AddQuantityInventoryDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyQuantity(String updatedQuantity, boolean isAdd) {
        //Updating quantity in firestore
        firebaseFirestore = firebaseFirestore.getInstance();
        int addQuantity = 0;
        if(isAdd){
            int prevQuantity = Integer.parseInt(tempQuantity);
            addQuantity = prevQuantity + Integer.parseInt(updatedQuantity);
            inventoryList.get(ingredientNumberInList).setQuantity(String.valueOf(addQuantity));
        }else{
            int prevQuantity = Integer.parseInt(inventoryList.get(ingredientNumberInList).getQuantity());
            addQuantity = prevQuantity - Integer.parseInt(updatedQuantity);
        }
        tempQuantity = String.valueOf(addQuantity);
        //IngredientModel ingredientModel = new IngredientModel(tempName,tempQuantity,tempUnit);
        final CollectionReference collectionReference1 = firebaseFirestore.collection("INGREDIENT");
        collectionReference1.whereEqualTo("name",tempName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot: task.getResult()){
                                String docID = documentSnapshot.getId();
                                collectionReference1.document(docID).update("quantity",tempQuantity);

                                //also update quantity on view
                                View v = mRecyclerView.getChildAt(ingredientNumberInList);
                                TextView quantityTextView = v.findViewById(R.id.ith_ingredient_quantity_inventory);
                                quantityTextView.setText(tempQuantity);
                            }
                        }
                    }
                });
        //String a1 = collectionReference1.document().getId();
//        collectionReference1.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
//                            String ingredientName = documentSnapshot.get("name").toString();
//                            String ingredientQuantityAvailable = documentSnapshot.get("quantity").toString();
//                            String quantityUnit = documentSnapshot.get("unit").toString();
//                            String a2 = collectionReference1.document().getId();
//                            if(ingredientName.equals(tempName)){
//                                String a = collectionReference1.document().getId();
//                                collectionReference1.document().set(tempQuantity, SetOptions.merge());
//                            }
//
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                //Log.d("error",e.toString());
//            }
//        });
    }

}
