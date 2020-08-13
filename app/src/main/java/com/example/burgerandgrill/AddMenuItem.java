package com.example.burgerandgrill;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class AddMenuItem extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private EditText itemName;
    private RadioGroup typeRadioGroup;
    private RadioGroup sizeRadioGroup;
    private EditText itemPrice;
    private Button saveItem;
    private ProgressBar progressBar;


    private FirebaseFirestore firebaseFirestore;
    final ArrayList<IngredientModel> ingredientList = new ArrayList<IngredientModel>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#ffcc0000"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        itemName = findViewById(R.id.item_name);
        typeRadioGroup = findViewById(R.id.type_groupradio);
        sizeRadioGroup = findViewById(R.id.size_groupradio);
        itemPrice = findViewById(R.id.item_price);

        mRecyclerView = findViewById(R.id.ingredient_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        saveItem = findViewById(R.id.save_item);
        progressBar = findViewById(R.id.add_menu_item_progressBar);


        firebaseFirestore = firebaseFirestore.getInstance();
        CollectionReference collectionReference1 = firebaseFirestore.collection("INGREDIENT");
        collectionReference1.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            String ingredientName = documentSnapshot.get("name").toString();
                            //String ingredientQuantityAvailable = documentSnapshot.get("quantity").toString();
                            String ingredientQuantityAvailable = "Quantity";
                            String quantityUnit = documentSnapshot.get("unit").toString();
                            ingredientList.add(new IngredientModel(ingredientName,ingredientQuantityAvailable,quantityUnit));
                            mAdapter = new IngredientListAdapter(ingredientList);
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

        saveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInputs()){
                    saveMenuItemInFirebase();
                }
            }
        });
    }

    private void saveMenuItemInFirebase(){
        firebaseFirestore = firebaseFirestore.getInstance();
        String name = itemName.getText().toString();
        RadioButton typeRadioButton = typeRadioGroup.findViewById(typeRadioGroup.getCheckedRadioButtonId());
        String type = typeRadioButton.getText().toString();
        RadioButton sizeRadioButton = sizeRadioGroup.findViewById(sizeRadioGroup.getCheckedRadioButtonId());
        String size = sizeRadioButton.getText().toString();
        String price = itemPrice.getText().toString();
        //final List<Map<String,String>>  iList = new List<Map<String, String>>()

        //create 3 string arrays - name,quantity,unit
        String[] tn = new String[ingredientList.size()];
        String[] tq = new String[ingredientList.size()];
        String[] tu = new String[ingredientList.size()];
        int index = 0;

        for(int i=0; i<ingredientList.size(); i++){
            View view = mRecyclerView.getChildAt(i);
            EditText iquantity = view.findViewById(R.id.ith_ingredient_quantity);
            TextView iname = view.findViewById(R.id.ith_ingredient);
            TextView iunit = view.findViewById(R.id.ith_ingredient_quantity_unit);

            String iQuantity = iquantity.getText().toString();
            if(iQuantity.equals("")){
                iQuantity = "0";
            }
            if(!iQuantity.equals("")){ //means some quantity must be there
                String iName = iname.getText().toString();
                String iUnit = iunit.getText().toString();
                tn[index] = iName;
                tq[index] = iQuantity;
                tu[index] = iUnit;
                index++;

            }
        }
        List<String> n = Arrays.asList(tn);
        List<String> q = Arrays.asList(tq);
        List<String> u = Arrays.asList(tu);
        MenuModel newMenuItem = new MenuModel(name,price,type,size,n,q,u);
        firebaseFirestore.collection("MENUITEM").add(newMenuItem)
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
                        saveItem.setVisibility(View.VISIBLE);
                        Log.d("error",e.toString());
                    }
                });
    }

    private void success(){
        progressBar.setVisibility(View.GONE);
        saveItem.setVisibility(View.VISIBLE);
        itemName.setText("");
        itemPrice.setText("");
        typeRadioGroup.clearCheck();
        sizeRadioGroup.clearCheck();
        for(int i=0; i<ingredientList.size(); i++){
            View view = mRecyclerView.getChildAt(i);
            EditText iquantity = view.findViewById(R.id.ith_ingredient_quantity);
            iquantity.setText("");
            iquantity.setHint("Quantity");
        }
        Toast.makeText(this,"New Menu Item Added Successfully",Toast.LENGTH_SHORT).show();
    }
    private boolean checkInputs(){
        if(!TextUtils.isEmpty(itemName.getText())){
            if(!TextUtils.isEmpty(itemPrice.getText())){
                int selectedType = typeRadioGroup.getCheckedRadioButtonId();
                if(selectedType != -1){
                    int selectedSize = sizeRadioGroup.getCheckedRadioButtonId();
                    if(selectedSize != -1){
                        return true;
                    }
                    else{
                        Toast.makeText(this,"Select any size option!!!",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                else {
                    Toast.makeText(this,"Select item type!!!",Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            else{
                Toast.makeText(this,"Enter item price!!",Toast.LENGTH_SHORT).show();
                return false;

            }
        }
        else{
            saveItem.setEnabled(false);
            saveItem.setTextColor(Color.GRAY);
            Toast.makeText(this,"Enter item name!!",Toast.LENGTH_SHORT).show();
            return false;

        }
    }
}
