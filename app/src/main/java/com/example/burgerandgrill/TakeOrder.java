package com.example.burgerandgrill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TakeOrder extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private OrderListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RecyclerView mRecyclerViewForCuurentOrder;
    private CurrentOrderListAdapter mAdapterForCuurentOrder;
    private RecyclerView.LayoutManager mLayoutManagerForCuurentOrder;

    Button placeOrder;
    EditText discount;
    TextView total;
    Button applyDiscount;

    private FirebaseFirestore firebaseFirestore;
    ArrayList<MenuItem> exampleList = new ArrayList<>();


    /**
     * Store all these attributes directly into firebase , dont need to store in class object
     */
//    String total;
//    String type;
//    String timeOfOrder;
//    String customerName;
//    String customerPhoneNo;
//    String customerAddres;


    int totalBill = 0;
    int billAfterDiscount = 0;


    final ArrayList<IngredientModel> inventoryList = new ArrayList<>();
    final ArrayList<IngredientModel> newIngredientList = new ArrayList<>();
    ArrayList<OrderModel> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.menu_for_order);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setNestedScrollingEnabled(false);

        mRecyclerViewForCuurentOrder = findViewById(R.id.current_order_list);
        mLayoutManagerForCuurentOrder = new LinearLayoutManager(this);
        mRecyclerViewForCuurentOrder.setNestedScrollingEnabled(false);

        discount = findViewById(R.id.order_discount);
        applyDiscount = findViewById(R.id.apple_discount);
        total = findViewById(R.id.order_total_bill);
        placeOrder = findViewById(R.id.place_order);
        placeOrder.setVisibility(View.GONE);



        firebaseFirestore = firebaseFirestore.getInstance();
        CollectionReference Icollection = firebaseFirestore.collection("INGREDIENT");
        Icollection.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            String ingredientName = documentSnapshot.get("name").toString();
                            String ingredientQuantityAvailable = documentSnapshot.get("quantity").toString();
                            String quantityUnit = documentSnapshot.get("unit").toString();
                            inventoryList.add(new IngredientModel(ingredientName,ingredientQuantityAvailable,quantityUnit));
                        }
                    }
                });



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


                            if(type.contains("Burger")){
                                exampleList.add(new MenuItem(R.drawable.burger, name, price, type,size,iname,iquantity,iunit));
                            }
                            else if(type.equals("Sandwich")){
                                exampleList.add(new MenuItem(R.drawable.sandwich, name, price, type,size,iname,iquantity,iunit));
                            }
                            else if(type.equals("Wrap")){
                                exampleList.add(new MenuItem(R.drawable.wrap, name, price, type,size,iname,iquantity,iunit));
                            }


                            mAdapter = new OrderListAdapter(exampleList);
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);
                            adapterClickListener();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.d("error",e.toString());
            }
        });

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrderInFirebase();
            }
        });

        applyDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                computeDiscount();
            }
        });

    }
    private void adapterClickListener(){
        mAdapter.setOnItemClickListener(new OrderListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) { }
            @Override
            public void onAddToOrderListClick(int position){
                String name = exampleList.get(position).getText1();
                String size = exampleList.get(position).getSize();
                String price = exampleList.get(position).getText2();
                String count = "1";
                boolean isPresent = false;
                for(int i = 0; i<orderList.size(); i++){
                    if(orderList.get(i).getProductName().equals(name) && orderList.get(i).getProductType().equals(size)){
                        //product already in order list , just increase count
                        int ctemp = Integer.parseInt(orderList.get(i).getCount());
                        ctemp++;
                        int ptemp = Integer.parseInt(price);
                        ptemp = ctemp*ptemp;

                        int temp = Integer.parseInt(price);
                        totalBill = totalBill + temp;
                        total.setText(String.valueOf(totalBill));

                        count = String.valueOf(ctemp);
                        price = String.valueOf(ptemp);

                        orderList.get(i).setCount(count);
                        orderList.get(i).setPrice(price);
                        isPresent = true;

                    }
                }
                if(!isPresent){
                    orderList.add(new OrderModel(name,size,count,price,exampleList.get(position).getIname(),exampleList.get(position).getIquantity(), exampleList.get(position).getIunit()));
                    int temp = Integer.parseInt(price);
                    totalBill = totalBill + temp;
                    total.setText(String.valueOf(totalBill));
                }
                if(orderList.size() == 1){
                    placeOrder.setVisibility(View.VISIBLE);
                }

                mAdapterForCuurentOrder = new CurrentOrderListAdapter(orderList);
                mRecyclerViewForCuurentOrder.setLayoutManager(mLayoutManagerForCuurentOrder);
                mRecyclerViewForCuurentOrder.setAdapter(mAdapterForCuurentOrder);

            }
            @Override
            public void onRemoveFromOrderListClick(int position){
                if(!orderList.isEmpty()){
                    placeOrder.setVisibility(View.VISIBLE);
                    String name = exampleList.get(position).getText1();
                    String size = exampleList.get(position).getSize();
                    String price = exampleList.get(position).getText2();

                    for(int i = 0; i<orderList.size(); i++){
                        if(orderList.get(i).getProductName().equals(name) && orderList.get(i).getProductType().equals(size)){
                            if(orderList.get(i).getCount().equals("1")){
                                int temp = Integer.parseInt(orderList.get(i).getPrice());
                                totalBill = totalBill - temp;
                                orderList.remove(i);
                                total.setText(String.valueOf(totalBill));
                            }
                            else{
                                int ctemp = Integer.parseInt(orderList.get(i).getCount());
                                ctemp--;
                                orderList.get(i).setCount(String.valueOf(ctemp));

                                int ptemp = Integer.parseInt(orderList.get(i).getPrice());
                                ptemp = ptemp - Integer.parseInt(exampleList.get(i).getText2());
                                orderList.get(i).setPrice(String.valueOf(ptemp));

                                int temp = Integer.parseInt(exampleList.get(i).getText2());
                                totalBill = totalBill - temp;
                                total.setText(String.valueOf(totalBill));
                            }

                        }
                    }
                }
                if(orderList.isEmpty()){
                    placeOrder.setVisibility(View.GONE);
                }
                mAdapterForCuurentOrder = new CurrentOrderListAdapter(orderList);
                mRecyclerViewForCuurentOrder.setLayoutManager(mLayoutManagerForCuurentOrder);
                mRecyclerViewForCuurentOrder.setAdapter(mAdapterForCuurentOrder);
            }
        });
    }
    private void saveOrderInFirebase(){
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        //As we are in TakeOrder so order type must be "Order", not "Delivery"
        String type = "Order";
        if(validateOrder()){
            Toast.makeText(this,"Order Placed",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Order Placed!! Shortage in inventory!!",Toast.LENGTH_SHORT).show();
        }


    }

    private boolean validateOrder(){
        /**
         * Check if there is enough ingredients in inventory to place the order
         * If all ingredients are available then subtract and place order returning true
         * Else no change in inventory quantities and return false
         */
        final boolean[] validate = {true};
        final ArrayList<String> totalQuantity = new ArrayList<>();
        for(int i=0; i<inventoryList.size();i++){
            int sum = 0;
            for(int j=0; j<orderList.size();j++){
                int index = orderList.get(j).getIname().indexOf(inventoryList.get(i).name);
                int count = Integer.parseInt(orderList.get(j).getCount());
                if(index > 0){
                    sum = sum + (count*Integer.parseInt(orderList.get(j).getIquantity().get(index)));
                }
            }
            totalQuantity.add(String.valueOf(sum));
        }

        firebaseFirestore = firebaseFirestore.getInstance();
        final CollectionReference collectionReference1 = firebaseFirestore.collection("INGREDIENT");
        for(int i=0; i<inventoryList.size();i++){
            final int finalI = i;
            collectionReference1.whereEqualTo("name",inventoryList.get(i).name)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(DocumentSnapshot documentSnapshot: task.getResult()){
                                    final String docID = documentSnapshot.getId();
                                    int availableQuantity = Integer.parseInt(inventoryList.get(finalI).getQuantity());
                                    if(availableQuantity >= Integer.parseInt(totalQuantity.get(finalI))){
                                        availableQuantity = availableQuantity - Integer.parseInt(totalQuantity.get(finalI));
                                        collectionReference1.document(docID).update("quantity",availableQuantity);
                                    }else{
                                        /*
                                        if there is no sufficient ingredient quantity available in inventory
                                        then just place 0 and keep placing orders
                                         */
                                        validate[0] = false;
                                        availableQuantity = 0;
                                        collectionReference1.document(docID).update("quantity",availableQuantity);
                                    }
                                }
                            }
                        }
                    });
        }

        return validate[0];

    }

    private void computeDiscount(){
        if(!TextUtils.isEmpty(discount.getText())){
            int disc = Integer.valueOf(discount.getText().toString());
            if(disc > 0 && disc < 100){
                int priceReduce = (int) (((1.0*disc)/100)*totalBill);

                billAfterDiscount = totalBill - priceReduce;
                total.setText("Rs. " + String.valueOf(billAfterDiscount));
            }
            else{
                Toast.makeText(this,"invalid discount value!!",Toast.LENGTH_SHORT);
            }
        }
        else{
            Toast.makeText(this,"Enter discount to apply!!",Toast.LENGTH_SHORT);
        }
    }
}