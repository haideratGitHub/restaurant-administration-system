package com.example.burgerandgrill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import java.util.HashMap;
import java.util.Map;

public class CouponCode extends AppCompatActivity implements AddCouponCodeDialog.IExampleDialogListener{


    Button addCouponCode;

    private RecyclerView mRecyclerView;
    private CurrentOrderListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<OrderModel> exampleList = new ArrayList<>();
    private static final String COLLECTION = "DISCOUNT";
    private FirebaseFirestore firebaseFirestore;

    public int count = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_code);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#ffcc0000"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        mRecyclerView = findViewById(R.id.coupon_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);


        displayCouponCodeList();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallBack);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        addCouponCode = findViewById(R.id.add_coupon_code);
        addCouponCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCouponCodeDialog();
            }
        });
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mRecyclerView.getContext(),R.style.DialogeTheme);
            builder.setMessage("Coupon code will be permanently deleted. You want to continue ?")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteCode(viewHolder.getAdapterPosition());
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAdapter = new CurrentOrderListAdapter(exampleList);
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    }).show();


        }
    };
    private void deleteCode(int position){
        //remove from firebase
        final CollectionReference collectionReference1 = firebaseFirestore.collection(COLLECTION);
        collectionReference1.whereEqualTo("code",exampleList.get(position).getProductName())
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
        exampleList.remove(position);
        mAdapter.notifyDataSetChanged();
    }

    private void displayCouponCodeList(){
        firebaseFirestore = firebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection(COLLECTION);
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            String code = documentSnapshot.get("code").toString();
                            String discount = documentSnapshot.get("discount").toString();

                            exampleList.add(new OrderModel(code,"",String.valueOf(count), discount,null,null,null));
                            count++;

                            mAdapter = new CurrentOrderListAdapter(exampleList);
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);

                        }
                    }
                });
    }

    private void addCouponCodeDialog(){
        AddCouponCodeDialog addCouponCodeDialog = new AddCouponCodeDialog();
        addCouponCodeDialog.show(getSupportFragmentManager(), "example dialog");
    }
    @Override
    public void applyQuantity(String newCouponCode, int discount) {
        if(!TextUtils.isEmpty(newCouponCode)){
            firebaseFirestore = firebaseFirestore.getInstance();
            Map<String,Object> coupon = new HashMap<>();
            coupon.put("code",newCouponCode);
            coupon.put("discount",String.valueOf(discount));
            firebaseFirestore.collection(COLLECTION).add(coupon)
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
            exampleList.add(new OrderModel(newCouponCode,"",String.valueOf(count),String.valueOf(discount),null,null,null));

            mAdapter = new CurrentOrderListAdapter(exampleList);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

}
