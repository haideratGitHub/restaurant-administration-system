package com.example.burgerandgrill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.SmsManager;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TakeOrder extends AppCompatActivity implements ApplyDiscountDialog.IExampleDialogListener{

    private RecyclerView mRecyclerView;
    private OrderListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RecyclerView mRecyclerViewForCuurentOrder;
    private CurrentOrderListAdapter mAdapterForCuurentOrder;
    private RecyclerView.LayoutManager mLayoutManagerForCuurentOrder;

    EditText customerNumberOrder;
    Button placeOrder;
    Button discount;
    TextView total;

    private FirebaseFirestore firebaseFirestore;
    ArrayList<MenuItem> exampleList = new ArrayList<>();


    int totalBill = 0;
    int billAfterDiscount = 0;
    String appliedDiscountCode = "";
    boolean lowInventoryIngredient = false;

    final ArrayList<IngredientModel> inventoryList = new ArrayList<>();
    final ArrayList<IngredientModel> newIngredientList = new ArrayList<>();
    ArrayList<OrderModel> orderList = new ArrayList<>();

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    final String ADMIN_PHONE_NUMBER = "03218488872";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#ffcc0000"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        mRecyclerView = findViewById(R.id.menu_for_order);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setNestedScrollingEnabled(false);

        mRecyclerViewForCuurentOrder = findViewById(R.id.current_order_list);
        mLayoutManagerForCuurentOrder = new LinearLayoutManager(this);
        mRecyclerViewForCuurentOrder.setNestedScrollingEnabled(false);

        customerNumberOrder = findViewById(R.id.customer_number_order);
        discount = findViewById(R.id.order_discount);
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

        discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplyDiscountDialog exampleDialog = new ApplyDiscountDialog();
                exampleDialog.show(getSupportFragmentManager(), "example dialog");
            }
        });

    }
    private void sendOrderDetailsToAdmin(String date, String type, String finalBill){
        //String orderDetailsSMS = type + " details" + "\n\n";
        String orderDetailsSMS = "Sale\n";

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String d = format.format(Date.parse(date));
        orderDetailsSMS = orderDetailsSMS + ("Dated: " + d + "\n");
        /**
         * this is the message send to customer when their order is placed
         */
        String smsToCustomer = "YOUR ORDER HAS BEEN PLACED!\n";
        //smsToCustomer = smsToCustomer + "Order Summary\n";
        int i = 0;
        while (i < orderList.size()){
            orderDetailsSMS = orderDetailsSMS + (orderList.get(i).getCount() + " " + orderList.get(i).getProductName() + "\n");
            //smsToCustomer = smsToCustomer + (orderList.get(i).getCount() + " " + orderList.get(i).getProductName() + "\n");
            i++;
            //orderList.remove(i); //for updating order list view
        }
        orderDetailsSMS = orderDetailsSMS + "\n";
        smsToCustomer = smsToCustomer + "\n";


        smsToCustomer = smsToCustomer + ("Dated: " + d + "\n");
        if(appliedDiscountCode.equals("")){
            orderDetailsSMS = orderDetailsSMS + ("Disct: Nill " + "\n");
            orderDetailsSMS = orderDetailsSMS + ("Bill: Rs. " + finalBill + "\n");
            smsToCustomer = smsToCustomer + ("Total Bill: Rs. " + finalBill + "\n");
        }else{
            orderDetailsSMS = orderDetailsSMS + ("Discount code: " + appliedDiscountCode + "\n");
            orderDetailsSMS = orderDetailsSMS + ("Bill: Rs. " + billAfterDiscount + "\n");
            smsToCustomer = smsToCustomer + ("Total Bill: Rs. " + billAfterDiscount+ "\n");
        }

        smsToCustomer = smsToCustomer + "Have a nice day\nEnjoy your meal...\n\n";
        smsToCustomer = smsToCustomer + ("Regards,\nBurger & Grill");
        if(checkPermission(Manifest.permission.SEND_SMS)){
            onSend(orderDetailsSMS);
            sendOrderDetailsToCustomer(smsToCustomer);
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},SEND_SMS_PERMISSION_REQUEST_CODE);
        }
    }
    private void notifyAdmin(){
        /**
         * Notify admin for shortage of stock in inventory
         */
        String message = "ALERT!!! Shortage of stock in inventory!!";
        if(checkPermission(Manifest.permission.SEND_SMS)){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(ADMIN_PHONE_NUMBER,null,message,null,null);
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},SEND_SMS_PERMISSION_REQUEST_CODE);
        }
    }
    private void onSend(String sms){
        if(checkPermission(Manifest.permission.SEND_SMS)){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(ADMIN_PHONE_NUMBER,null,sms,null,null);
            discount.setEnabled(true);
            discount.setTextColor(Color.BLACK);
            discount.setText("Discount ?");
            //Toast.makeText(this,"sent",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
        }
    }
    private void sendOrderDetailsToCustomer(String sms){
        String cusNumber = customerNumberOrder.getText().toString();
        if(!cusNumber.isEmpty()){
            if(checkPermission(Manifest.permission.SEND_SMS)){
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(cusNumber,null,sms,null,null);
            }else{
                Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
            }


            /**
             * Allowing duplicate for time being
             * whenever you need to get list of numbers
             * TODO make sure to remove duplicates
             */
            firebaseFirestore = firebaseFirestore.getInstance();
            final Map<String,Object> number = new HashMap<>();
            number.put("number",cusNumber);
            firebaseFirestore.collection("NUMBERS").add(number);
            success();
        }

    }
    private boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this,permission);
        return (check == PackageManager.PERMISSION_GRANTED);
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
                    total.setText("Rs. " + String.valueOf(totalBill));
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
                                ptemp = ptemp - Integer.parseInt(exampleList.get(position).getText2());
                                orderList.get(i).setPrice(String.valueOf(ptemp));

                                int temp = Integer.parseInt(exampleList.get(position).getText2());
                                totalBill = totalBill - temp;
                                total.setText("Rs. " + String.valueOf(totalBill));
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
        //String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        //As we are in TakeOrder so order type must be "Order", not "Delivery"
        Date cal = Calendar.getInstance().getTime();
        final String date = DateFormat.getDateInstance(DateFormat.FULL).format(cal.getTime());
        final String type = "Order";
//        String discountGiven = discount.getText().toString();
//        if(discountGiven.equals("")){
//            discountGiven = "0";
//        }
        final String finalBill;
        if(appliedDiscountCode.equals("")){
            //there is no discount given and we can save total bill as final
            finalBill = String.valueOf(totalBill);
        }else{
            //there is discount given so now we will save total bill after discount in final bill
            finalBill = String.valueOf(billAfterDiscount);
        }

        sendOrderDetailsToAdmin(date,type,finalBill);
        if(validateOrder()){
            Toast.makeText(this,"Order Placed",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Order Placed!! Shortage in inventory!!",Toast.LENGTH_SHORT).show();
            notifyAdmin();
        }

        String[] productName = new String[orderList.size()];
        String[] productType = new String[orderList.size()];
        String[] count = new String[orderList.size()];
        String[] price = new String[orderList.size()];

        for(int i=0; i<orderList.size();i++){
            productName[i] = orderList.get(i).getProductName();
            productType[i] = orderList.get(i).getProductType();
            count[i] = orderList.get(i).getCount();
            price[i] = orderList.get(i).getPrice();
        }

        List<String> pn = Arrays.asList(productName);
        List<String> pt = Arrays.asList(productType);
        List<String> c = Arrays.asList(count);
        List<String> pr = Arrays.asList(price);

        OrderSaveModel orderSaveModel = new OrderSaveModel(date,type,appliedDiscountCode,finalBill,pn,pt,c,pr);
        firebaseFirestore = firebaseFirestore.getInstance();
        firebaseFirestore.collection("SALES").add(orderSaveModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        success();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("error",e.toString());
                    }
                });

    }
    private void success(){
        if(!orderList.isEmpty()){
            saveOnGoingOrder();
        }

        String list = "";
        int i = 0;
        while (!orderList.isEmpty()){
            list = list + (orderList.get(i).getCount() + " " + orderList.get(i).getProductName() + "\n");
            orderList.remove(i);
        }
        String bill = "";
        if (billAfterDiscount == 0){
            bill = String.valueOf(totalBill);
        }else{
            bill = String.valueOf(billAfterDiscount);
        }
        if(orderList.isEmpty()){
            discount.setText("");
            customerNumberOrder.setText("");
            discount.setText("Discount ?");
            total.setText("Rs. 0");
            mAdapterForCuurentOrder = new CurrentOrderListAdapter(orderList);
            mRecyclerViewForCuurentOrder.setLayoutManager(mLayoutManagerForCuurentOrder);
            mRecyclerViewForCuurentOrder.setAdapter(mAdapterForCuurentOrder);
        }
    }
    private void saveOnGoingOrder(){
        /**
         * Not recommended
         * This is jst an over head for extra writes, reads and deletes on firebase
         * Using intent put extra as an alternative
         */
        Map<String,String> onGoingOrdersList = new HashMap<>();
        String temp = "";
        int i = 0;
        while (!orderList.isEmpty()){
            temp = temp + (orderList.get(i).getCount() + " " + orderList.get(i).getProductName() + "\n");
            orderList.remove(i);
        }
        onGoingOrdersList.put("list",temp);
        if(billAfterDiscount == 0){
            onGoingOrdersList.put("bill",String.valueOf(totalBill));
        }else{
            onGoingOrdersList.put("bill",String.valueOf(billAfterDiscount));
        }
        if(customerNumberOrder.getText().toString().equals("")){
            onGoingOrdersList.put("number", "0");
        }else{
            onGoingOrdersList.put("number", customerNumberOrder.getText().toString());
        }

        firebaseFirestore = firebaseFirestore.getInstance();
        firebaseFirestore.collection("ON_GOING_ORDER").add(onGoingOrdersList)
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
                if(index >= 0){
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
                                        /**
                                         * if there is no sufficient ingredient quantity available in inventory
                                         * then just place 0 and keep placing orders
                                         */
                                        validate[0] = false;
                                        notifyAdmin();
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
    private void computeDiscount(int disc){
        if(totalBill > 0){
            int priceReduce = (int) (((1.0*disc)/100)*totalBill);
            billAfterDiscount = totalBill - priceReduce;
            total.setText("Rs. " + String.valueOf(billAfterDiscount));
            discount.setEnabled(false);
        }
    }
    @Override
    public void applyQuantity(String discountCode) {
        if(!discountCode.equals("")){
            firebaseFirestore = firebaseFirestore.getInstance();
            final CollectionReference collection = firebaseFirestore.collection("DISCOUNT");
            collection.whereEqualTo("code",discountCode)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(DocumentSnapshot documentSnapshot: task.getResult()){
                                    //String docID = documentSnapshot.getId();
                                    appliedDiscountCode = documentSnapshot.get("code").toString();
                                    int disc = Integer.parseInt(documentSnapshot.get("discount").toString());
                                    appliedDiscountCode = appliedDiscountCode+"-"+String.valueOf(disc)+"%";
                                    computeDiscount(disc);

                                }
                            }else{
                                makeToastNoCodeFound();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        }
                    });
        }

    }
    private void makeToastNoCodeFound(){
        Toast.makeText(this,"Wrong discount code!! Enter again!!",Toast.LENGTH_SHORT).show();
    }
}
