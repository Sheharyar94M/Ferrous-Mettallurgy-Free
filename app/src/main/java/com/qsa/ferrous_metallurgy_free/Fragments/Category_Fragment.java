package com.qsa.ferrous_metallurgy_free.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qsa.ferrous_metallurgy_free.Adapters.CategoryAdapter;
import com.qsa.ferrous_metallurgy_free.Model.Categories;
import com.qsa.ferrous_metallurgy_free.R;

import java.util.ArrayList;
import java.util.List;

public class Category_Fragment extends Fragment {

    RecyclerView recyclerView;
    List<Categories> dataList;
    CategoryAdapter categoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category_, container, false);

        // progress Dialog
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        //checking internet connectivity...
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network

            dataList = new ArrayList<>();
            recyclerView = view.findViewById(R.id.recycleViewCategory);
            categoryAdapter = new CategoryAdapter(getContext(), dataList);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(gridLayoutManager);

            //firebase
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Category");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dataList.clear();

                    if (snapshot.getValue() != null) {

                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            progressDialog.dismiss();
                            Categories cat = dataSnapshot1.getValue(Categories.class);
                            Log.i("bbb", "onDataChange: " + cat.getCategoryImage());
                            dataList.add(cat);
                        }
                        recyclerView.setAdapter(categoryAdapter);
                    } else {
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Download_Fragment()).commit();

        }

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }
}