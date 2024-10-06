package com.qsa.ferrous_metallurgy_free;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.qsa.ferrous_metallurgy_free.Adapters.BooksAdapter;
import com.qsa.ferrous_metallurgy_free.Model.Books;

import java.util.ArrayList;
import java.util.List;

public class CategoryResult extends AppCompatActivity {

    //recyclerView
    RecyclerView recyclerView;
    private AdView adView;
    BooksAdapter booksAdapter;
    List<Books> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category_result_adapter);

        recyclerView = findViewById(R.id.recycleView);


        AudienceNetworkAds.initialize(this);

        AudienceNetworkAds.initialize(this);

        adView = new AdView(this, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_50);

// Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

// Add the ad view to your activity layout
        adContainer.addView(adView);

// Request an ad
        adView.loadAd();

        Intent i = getIntent();

        //progressBar
        final ProgressDialog progressDialog = new ProgressDialog(CategoryResult.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        dataList = new ArrayList<>();
        booksAdapter = new BooksAdapter(CategoryResult.this, dataList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(CategoryResult.this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");

        Query query = ref.orderByChild("categoryName").equalTo(i.getStringExtra("name"));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                if (snapshot.getValue() != null) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        progressDialog.dismiss();
                        Books books = snapshot1.getValue(Books.class);
                        dataList.add(books);
                    }
                    recyclerView.setAdapter(booksAdapter);
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}