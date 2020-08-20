package com.example.burgerandgrill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserDashboard extends AppCompatActivity {

    private CardView takeOrder;
    private CardView homeDelivery;

    private TextView onGoingOrders;
    private RecyclerView mRecyclerViewOnGoingOrdersList;
    private CurrentOrderListAdapter mAdapterOnGoingOrdersList;
    private RecyclerView.LayoutManager mLayoutManagerOnGoingOrdersList;

    private TextView onGoingDeliveries;
    private RecyclerView mRecyclerViewOnGoingDeliveriesList;
    private CurrentOrderListAdapter mAdapterOnGoingDeliveriesList;
    private RecyclerView.LayoutManager mLayoutManagerOnGoingDeliveriesList;

    private FirebaseFirestore firebaseFirestore;
    ArrayList<OrderModel> exampleList = new ArrayList<>(); //for orders
    ArrayList<OrderModel> exampleList1 = new ArrayList<>(); //for deliveries

    /**
     * this will be used to keep track if refreshing on-going order or deliveries list
     */
    public static final int ORDER_ACTIVITY = 1234;
    public static final int DELIVERY_ACTIVITY = 7890;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        takeOrder = findViewById(R.id.take_order_user);
        homeDelivery = findViewById(R.id.home_delivery_user);

        onGoingOrders = findViewById(R.id.on_going_orders);
        onGoingOrders.setVisibility(View.GONE);
        mRecyclerViewOnGoingOrdersList = findViewById(R.id.on_going_orders_list);
        mRecyclerViewOnGoingOrdersList.setHasFixedSize(true);
        mLayoutManagerOnGoingOrdersList = new LinearLayoutManager(this);
        ItemTouchHelper itemTouchHelper1 = new ItemTouchHelper(itemTouchHelperCallBack1);
        itemTouchHelper1.attachToRecyclerView(mRecyclerViewOnGoingOrdersList);

        checkOnGoingOrders();


        onGoingDeliveries = findViewById(R.id.on_going_deliveries);
        onGoingDeliveries.setVisibility(View.GONE);
        mRecyclerViewOnGoingDeliveriesList = findViewById(R.id.on_going_deliveries_list);
        mRecyclerViewOnGoingDeliveriesList.setHasFixedSize(true);
        mLayoutManagerOnGoingDeliveriesList = new LinearLayoutManager(this);
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(itemTouchHelperCallBack2);
        itemTouchHelper2.attachToRecyclerView(mRecyclerViewOnGoingDeliveriesList);

        checkOnGoingDeliveries();


        takeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTakeOrder();
            }
        });
        homeDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoHomeDelivery();
            }
        });
    }

    private void checkOnGoingOrders(){
        final int[] count = {1};
        firebaseFirestore = firebaseFirestore.getInstance();
        firebaseFirestore.collection("ON_GOING_ORDER").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            if(documentSnapshot.exists()){
                                onGoingOrders.setVisibility(View.VISIBLE);
                                String list = documentSnapshot.get("list").toString();
                                String bill = documentSnapshot.get("bill").toString();
                                exampleList.add(new OrderModel(list,"on_going_order",String.valueOf(count[0]),bill,null,null,null));
                                count[0]++;

                                mAdapterOnGoingOrdersList = new CurrentOrderListAdapter(exampleList);
                                mRecyclerViewOnGoingOrdersList.setLayoutManager(mLayoutManagerOnGoingOrdersList);
                                mRecyclerViewOnGoingOrdersList.setAdapter(mAdapterOnGoingOrdersList);
                            }else{
                                onGoingOrders.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }
    private void checkOnGoingDeliveries(){
        final int[] count = {1};
        firebaseFirestore = firebaseFirestore.getInstance();
        firebaseFirestore.collection("ON_GOING_DELIVERY").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            if(documentSnapshot.exists()){
                                onGoingDeliveries.setVisibility(View.VISIBLE);
                                String list = documentSnapshot.get("list").toString();
                                String bill = documentSnapshot.get("bill").toString();
                                exampleList1.add(new OrderModel(list,"on_going_delivery",String.valueOf(count[0]),bill,null,null,null));
                                count[0]++;

                                mAdapterOnGoingDeliveriesList = new CurrentOrderListAdapter(exampleList1);
                                mRecyclerViewOnGoingDeliveriesList.setLayoutManager(mLayoutManagerOnGoingDeliveriesList);
                                mRecyclerViewOnGoingDeliveriesList.setAdapter(mAdapterOnGoingDeliveriesList);
                            }else{
                                onGoingDeliveries.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }
    private void gotoTakeOrder(){
        final Intent intent = new Intent(this,TakeOrder.class);
        mRecyclerViewOnGoingOrdersList.setAdapter(null);
        int i = 0;
        while (!exampleList.isEmpty()){
            exampleList.remove(i);
        }
        startActivityForResult(intent,ORDER_ACTIVITY);
        //startActivity(intent);
    }
    ItemTouchHelper.SimpleCallback itemTouchHelperCallBack1 = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mRecyclerViewOnGoingOrdersList.getContext(),R.style.DialogeTheme);
            builder.setMessage("Order is cleared ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteOrder(viewHolder.getAdapterPosition());
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAdapterOnGoingOrdersList = new CurrentOrderListAdapter(exampleList);
                            mRecyclerViewOnGoingOrdersList.setLayoutManager(mLayoutManagerOnGoingOrdersList);
                            mRecyclerViewOnGoingOrdersList.setAdapter(mAdapterOnGoingOrdersList);
                        }
                    }).show();


        }
    };
    private void deleteOrder(int position){
        final String[] customerNumber = {""};
        final CollectionReference collectionReference1 = firebaseFirestore.collection("ON_GOING_ORDER");
        collectionReference1.whereEqualTo("list",exampleList.get(position).getProductName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot: task.getResult()){
                                String docID = documentSnapshot.getId();
//                                collectionReference1.document(docID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//
//                                    }
//                                });
                                customerNumber[0] = documentSnapshot.get("number").toString();
                                collectionReference1.document(docID).delete();
                                sendThanksToCustomer(customerNumber[0]);

                            }
                        }
                    }
                });
        exampleList.remove(position);
        mAdapterOnGoingOrdersList.notifyDataSetChanged();

    }
    private boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this,permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }
    private void onSend(String sms, String number){
        if(checkPermission(Manifest.permission.SEND_SMS)){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number,null,sms,null,null);
        }else{
            Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
        }
    }
    private void sendThanksToCustomer(String number){
        if (!number.equals("") && !number.equals("0")){
            String sms = "It's been pleasure to have you here at Burger & Grill. Thanks for coming.\nPlease come again.\n\nBest regards,\nBurger & Grill";
            onSend(sms,number);
        }
    }
    private void gotoHomeDelivery(){
        final Intent intent = new Intent(this,HomeDelivery.class);
        mRecyclerViewOnGoingDeliveriesList.setAdapter(null);
        int i = 0;
        while (!exampleList1.isEmpty()){
            exampleList1.remove(i);
        }
        startActivityForResult(intent,DELIVERY_ACTIVITY);
        //startActivity(intent);
    }
    ItemTouchHelper.SimpleCallback itemTouchHelperCallBack2 = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mRecyclerViewOnGoingDeliveriesList.getContext(),R.style.DialogeTheme);
            builder.setMessage("Delivery is cleared ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteDelivery(viewHolder.getAdapterPosition());
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAdapterOnGoingDeliveriesList = new CurrentOrderListAdapter(exampleList1);
                            mRecyclerViewOnGoingDeliveriesList.setLayoutManager(mLayoutManagerOnGoingDeliveriesList);
                            mRecyclerViewOnGoingDeliveriesList.setAdapter(mAdapterOnGoingDeliveriesList);
                        }
                    }).show();

        }
    };
    private void deleteDelivery(int position){
        final CollectionReference collectionReference1 = firebaseFirestore.collection("ON_GOING_DELIVERY");
        collectionReference1.whereEqualTo("list",exampleList1.get(position).getProductName())
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
        exampleList1.remove(position);
        mAdapterOnGoingDeliveriesList.notifyDataSetChanged();

    }
    @Override
    protected void onRestart() {
        super.onRestart();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DELIVERY_ACTIVITY) {
            checkOnGoingDeliveries();
        }
        if (requestCode == ORDER_ACTIVITY){
            checkOnGoingOrders();
        }
    }
    @Override protected void onResume() {
        super.onResume();
    }

}
