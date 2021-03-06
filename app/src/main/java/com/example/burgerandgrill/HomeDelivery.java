package com.example.burgerandgrill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
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
import java.util.Locale;
import java.util.Map;

public class HomeDelivery extends AppCompatActivity implements ApplyDiscountDialog.IExampleDialogListener{

    private RecyclerView mRecyclerView;
    private OrderListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RecyclerView mRecyclerViewForCuurentOrder;
    private CurrentOrderListAdapter mAdapterForCuurentOrder;
    private RecyclerView.LayoutManager mLayoutManagerForCuurentOrder;

    Button placeOrder;
    EditText cusName;
    EditText cusPhNumber;
    EditText cusAddress;
    Button discount;
    TextView total;
    EditText deliveryBoyName;
    EditText deliveryBoyNumber;

    private FirebaseFirestore firebaseFirestore;
    ArrayList<MenuItem> exampleList = new ArrayList<>();

    int totalBill = 0;
    int billAfterDiscount = 0;
    String appliedDiscountCode = "";
    boolean lowInventoryIngredient = false;

    final ArrayList<IngredientModel> inventoryList = new ArrayList<>();
    ArrayList<OrderModel> orderList = new ArrayList<>();

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    final String ADMIN_PHONE_NUMBER = "03218488872";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_delivery);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#ffcc0000"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        mRecyclerView = findViewById(R.id.menu_for_order_delivery);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setNestedScrollingEnabled(false);

        mRecyclerViewForCuurentOrder = findViewById(R.id.current_order_list_delivery);
        mLayoutManagerForCuurentOrder = new LinearLayoutManager(this);
        mRecyclerViewForCuurentOrder.setNestedScrollingEnabled(false);

        cusName = findViewById(R.id.customer_name);
        cusPhNumber = findViewById(R.id.customer_number);
        cusAddress = findViewById(R.id.customer_address);
        discount = findViewById(R.id.order_discount_delivery);
        total = findViewById(R.id.order_total_bill_delivery);
        placeOrder = findViewById(R.id.place_order_delivery);
        deliveryBoyName = findViewById(R.id.delivery_boy_name);
        deliveryBoyNumber = findViewById(R.id.delivery_boy_number);

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
                if(checkInputs()){
                    saveOrderInFirebase();
                }
                else{
                    showToast();
                }
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
    private void showToast(){
        Toast.makeText(this,"incomplete info!!", Toast.LENGTH_SHORT).show();
    }
    private void sendOrderDetailsToAdmin(String date, String type, String finalBill){
        String orderDetailsSMS = "Sale\n";

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String d = format.format(Date.parse(date));
        orderDetailsSMS = orderDetailsSMS + ("Dated: " + d + "\n");

        String smsToCustomer1 = "Our Delivery boy will be at your door step within 20 minutes\n";
        smsToCustomer1 = smsToCustomer1 + "In case of any delay, contact on given number:\n";
        smsToCustomer1 = smsToCustomer1 + deliveryBoyName.getText().toString()+"\n";
        smsToCustomer1 = smsToCustomer1 + deliveryBoyNumber.getText().toString()+"\n\n";
        smsToCustomer1 = smsToCustomer1 + "Regards,\nBurger & Grill";

        String smsToCustomer2 = "YOUR ORDER HAS BEEN PLACED!\nOrder Summary\n";
        int i = 0;
        while (i < orderList.size()){
            orderDetailsSMS = orderDetailsSMS + (orderList.get(i).getCount() + " " + orderList.get(i).getProductName() + "\n");
            smsToCustomer2 = smsToCustomer2 + (orderList.get(i).getCount() + " " + orderList.get(i).getProductName() + "\n");
            i++;
            //orderList.remove(i); //for updating order list view
        }
        orderDetailsSMS = orderDetailsSMS + "\n";
        smsToCustomer2 = smsToCustomer2 + ("Dated: " + d + "\n");

        if(appliedDiscountCode.equals("")){
            orderDetailsSMS = orderDetailsSMS + ("Disct: Nill " + "\n");
            orderDetailsSMS = orderDetailsSMS + ("Bill: Rs. " + finalBill + "\n");
            smsToCustomer2 = smsToCustomer2 + ("Bill: Rs. " + finalBill + "\n");
        }else{
            orderDetailsSMS = orderDetailsSMS + ("Discount code: " + appliedDiscountCode + "\n");
            orderDetailsSMS = orderDetailsSMS + ("Bill: Rs. " + billAfterDiscount + "\n");
            smsToCustomer2 = smsToCustomer2 + ("Bill: Rs. " + billAfterDiscount + "\n");
        }

        if(checkPermission(Manifest.permission.SEND_SMS)){
            onSend(orderDetailsSMS);
            sendOrderDetailsToCustomer(smsToCustomer1,smsToCustomer2);
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},SEND_SMS_PERMISSION_REQUEST_CODE);
        }
    }
    private void sendOrderDetailsToCustomer(String sms1, String sms2){
        String cusNumber = cusPhNumber.getText().toString();
        if(!cusNumber.isEmpty()){
            if(checkPermission(Manifest.permission.SEND_SMS)){
                SmsManager smsManager1 = SmsManager.getDefault();
                smsManager1.sendTextMessage(cusNumber,null,sms1,null,null);
                SmsManager smsManager2 = SmsManager.getDefault();
                smsManager2.sendTextMessage(cusNumber,null,sms2,null,null);
            }else{
                Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
            }


            /**
             * Allowing duplicate for time being
             * whenever you need to get list of numbers
             * TODO make sure to remove duplicates
             */
            final Map<String,Object> number = new HashMap<>();
            number.put("number",cusNumber);
            firebaseFirestore = firebaseFirestore.getInstance();
            firebaseFirestore.collection("NUMBERS").add(number);

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
    private boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this,permission);
        return (check == PackageManager.PERMISSION_GRANTED);
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
                                ptemp = ptemp - Integer.parseInt(exampleList.get(position).getText2());
                                orderList.get(i).setPrice(String.valueOf(ptemp));

                                int temp = Integer.parseInt(exampleList.get(position).getText2());
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
        Date cal = Calendar.getInstance().getTime();
        String date = DateFormat.getDateInstance(DateFormat.FULL).format(cal.getTime());
        //As we are in TakeOrder so order type must be "Order", not "Delivery"
        String type = "Delivery";
        String discountGiven = discount.getText().toString();
        if(discountGiven.equals("")){
            discountGiven = "0";
        }
        String customerName = cusName.getText().toString();
        String customerPhoneNumber = cusPhNumber.getText().toString();
        String customerAddress = cusAddress.getText().toString();
        String finalBill;
        if(billAfterDiscount == 0){
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

        DeliverySaveModel deliverySaveModel = new DeliverySaveModel(date,type,appliedDiscountCode,finalBill,customerName,customerPhoneNumber,customerAddress,pn,pt,c,pr);
        firebaseFirestore = firebaseFirestore.getInstance();
        firebaseFirestore.collection("SALES").add(deliverySaveModel)
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
            saveOnGoingDelivery();
        }
        int i = 0;
        while (!orderList.isEmpty()){
            orderList.remove(i); //for updating order list view
        }
        if(orderList.isEmpty()){
            discount.setText("");
            cusName.setText("");
            cusAddress.setText("");
            cusPhNumber.setText("");
            deliveryBoyName.setText("");
            deliveryBoyNumber.setText("");
            discount.setText("Discount ?");
            total.setText("Rs. 0");
            mAdapterForCuurentOrder = new CurrentOrderListAdapter(orderList);
            mRecyclerViewForCuurentOrder.setLayoutManager(mLayoutManagerForCuurentOrder);
            mRecyclerViewForCuurentOrder.setAdapter(mAdapterForCuurentOrder);
        }
    }
    private void saveOnGoingDelivery(){
        Map<String,String> onGoingDeliveryList = new HashMap<>();
        String temp = "";
        int i = 0;
        while (!orderList.isEmpty()){
            temp = temp + (orderList.get(i).getCount() + " " + orderList.get(i).getProductName() + "\n");
            orderList.remove(i);
        }
        onGoingDeliveryList.put("list",temp);
        if(billAfterDiscount == 0){
            String deliveryDetail = cusName.getText().toString() + "\n" + cusPhNumber.getText().toString() + "\n" + cusAddress.getText().toString() + "\n";
            onGoingDeliveryList.put("bill",deliveryDetail + "Rs: " + String.valueOf(totalBill));
        }else{
            String deliveryDetail = cusName.getText().toString() + "\n" + cusPhNumber.getText().toString() + "\n" + cusAddress.getText().toString() + "\n";
            onGoingDeliveryList.put("bill",deliveryDetail + "Rs: " +String.valueOf(billAfterDiscount));
        }
        firebaseFirestore = firebaseFirestore.getInstance();
        firebaseFirestore.collection("ON_GOING_DELIVERY").add(onGoingDeliveryList)
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
                                        /*
                                        if there is no sufficient ingredient quantity available in inventory
                                        then just place 0 and keep placing orders
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
    private boolean checkInputs(){
        if(!TextUtils.isEmpty(cusName.getText().toString())){
            if(!TextUtils.isEmpty(cusPhNumber.getText().toString())){
                if(!TextUtils.isEmpty(cusAddress.getText().toString())){
                    if(!TextUtils.isEmpty(deliveryBoyName.getText().toString())) {
                        if(!TextUtils.isEmpty(deliveryBoyNumber.getText().toString())){
                            return true;
                        }else{
                            Toast.makeText(this,"Enter Delivery boy number!!", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }else{
                        Toast.makeText(this,"Enter Delivery boy name!!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(this,"Enter customer address!!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(this,"Enter customer Phone Number!!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(this,"Enter customer name!!", Toast.LENGTH_SHORT).show();
            return false;
        }
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
