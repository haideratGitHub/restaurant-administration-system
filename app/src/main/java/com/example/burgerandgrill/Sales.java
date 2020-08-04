package com.example.burgerandgrill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Sales extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private RecyclerView mRecyclerView;
    private SalesListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button date;

    private FirebaseFirestore firebaseFirestore;
    ArrayList<SalesModel> exampleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRecyclerView = findViewById(R.id.sales_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        date = findViewById(R.id.date_picker);
        setCurrentDate();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

//        firebaseFirestore = firebaseFirestore.getInstance();
//        CollectionReference collectionReference2 = firebaseFirestore.collection("SALES");
//        collectionReference2.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
//                            String sDate = documentSnapshot.get("date").toString();
//                            if(sDate.equals(date.getText().toString())) {
//                                String sType = documentSnapshot.get("type").toString();
//                                String sBill = documentSnapshot.get("finalBill").toString();
//                                exampleList.add(new SalesModel(sType, sDate, sBill));
//                            }
//                            mAdapter = new SalesListAdapter(exampleList);
//                            mRecyclerView.setLayoutManager(mLayoutManager);
//                            mRecyclerView.setAdapter(mAdapter);
//
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });


    }
    private void getSalesDataFromFirebase(){
        int i = 0;
        while (!exampleList.isEmpty()){
            exampleList.remove(i);
            mAdapter = new SalesListAdapter(exampleList);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
        }
        firebaseFirestore = firebaseFirestore.getInstance();
        CollectionReference collectionReference2 = firebaseFirestore.collection("SALES");
        collectionReference2.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            String sDate = documentSnapshot.get("date").toString();
                            if(sDate.equals(date.getText().toString())) {
                                String sType = documentSnapshot.get("type").toString();
                                String sBill = documentSnapshot.get("finalBill").toString();
                                exampleList.add(new SalesModel(sType, sDate, sBill));
                            }
                            mAdapter = new SalesListAdapter(exampleList);
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private void setCurrentDate(){
        Date c = Calendar.getInstance().getTime();
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        //SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        //String formattedDate = df.format(c);
        //String d = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        date.setText(currentDateString);
        getSalesDataFromFirebase();
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        date.setText(currentDateString);
        getSalesDataFromFirebase();
    }
}
