package com.example.burgerandgrill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewAllUsers extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private CurrentOrderListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<OrderModel> exampleList = new ArrayList<>();
    private static final String COLLECTION = "USERS";
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_users);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#ffcc0000"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        mRecyclerView = findViewById(R.id.all_users_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

//        usersList = getIntent().getStringArrayListExtra("usersList");

//        ArrayAdapter adapter = new ArrayAdapter<String>(this,
//                R.layout.user_listview, usersList);

        firebaseFirestore = firebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection(COLLECTION);
        final int[] count = {1};
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                            String userEmail = documentSnapshot.get("email").toString();
                            String userPassword = documentSnapshot.get("password").toString();
                            String userType = documentSnapshot.get("type").toString();

                            exampleList.add(new OrderModel(userEmail,userPassword,String.valueOf(count[0]),userType,null,null,null));
                            count[0]++;

                            mAdapter = new CurrentOrderListAdapter(exampleList);
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setAdapter(mAdapter);

                        }
                    }
                });


    }
}
