package com.qsa.ferrous_metallurgy_free;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qsa.ferrous_metallurgy_free.Adapters.DownloadVideoAdapter;
import com.qsa.ferrous_metallurgy_free.Adapters.FavoriteVideoAdapter;
import com.qsa.ferrous_metallurgy_free.Adapters.VideoAdapter;
import com.qsa.ferrous_metallurgy_free.Model.Books;

import java.util.ArrayList;
import java.util.List;

public class VideoPlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private String videoUrl, categoryName, type;
    private DatabaseReference dbRef;
    private List<Books> booksList;
    public List<Booksdb> booksdbs;
    ArrayList<Booksdb> dataList;
    private VideoAdapter adapter;
    private FavoriteVideoAdapter favoriteVideoAdapter;
    private DownloadVideoAdapter downloadVideoAdapter;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;
    private YouTubePlayerView playerView;

    private AdView FacebookBannerAdviewVPlayer;
    private LinearLayout linearLayoutFacebookAdVideoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_video_player);

        initViews();
        linearLayoutFacebookAdVideoPlayer = findViewById(R.id.facebookBannerAd_containerVideoPlayer);
        facebooBannerAd();


        if (type.equals("download")) {
            loadData();
        } else if (type.equals("favourite")) {
            getFavBooks();
        } else if (type.equals("normal")) {

            getCurrentCategoryBooks();
        }


    }

    private void facebooBannerAd() {
        FacebookBannerAdviewVPlayer = new AdView(this, getResources().getString(R.string.Facebook_Banner_ad_Unit_Id), AdSize.BANNER_HEIGHT_50);
        linearLayoutFacebookAdVideoPlayer.addView(FacebookBannerAdviewVPlayer);
        FacebookBannerAdviewVPlayer.loadAd();
        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };
        FacebookBannerAdviewVPlayer.loadAd(FacebookBannerAdviewVPlayer.buildLoadAdConfig().withAdListener(adListener).build());

    }

    private void initViews() {

        booksdbs = MainActivity.myappdatabas.myDao().getBooks();

        videoUrl = getIntent().getStringExtra("youtubeUrl");
        categoryName = getIntent().getStringExtra("categoryName");
        type = getIntent().getStringExtra("type");
        Log.d("TAG", "initViews: " + videoUrl);
        Log.d("TAG", "initViews: " + type);
        Log.d("TAG", "initViews: " +
                categoryName);

        playerView = findViewById(R.id.player_view);
        playerView.initialize("AIzaSyCPnANOt2_hII5T2MQqheyhYObbfgY6EuE", this);

        recyclerView = findViewById(R.id.videoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        booksList = new ArrayList<>();
        adapter = new VideoAdapter(this, booksList);
        dataList = new ArrayList<>();
        downloadVideoAdapter = new DownloadVideoAdapter(this, dataList);
        favoriteVideoAdapter = new FavoriteVideoAdapter(this, booksList);

        dbRef = FirebaseDatabase.getInstance().getReference();
    }


    // get Current category videos
    private void getCurrentCategoryBooks() {
        dbRef.child("Books").orderByChild("categoryName")
                .equalTo(categoryName)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Books books = snapshot.getValue(Books.class);
                        booksList.add(books);
                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void getFavBooks() {

        //checking internet connectivity...
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    booksList.clear();
                    if (snapshot.getValue() != null) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                            Books books = snapshot1.getValue(Books.class);

                            sharedPreferences = getSharedPreferences("favourites", Context.MODE_PRIVATE);
                            boolean value = sharedPreferences.getBoolean("task_" + books.getId(), false);

                            if (value) {

                                booksList.add(books);
                                recyclerView.setAdapter(favoriteVideoAdapter);
                            }


                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {

            Toast.makeText(this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();

        }


    }


    private void loadData() {
/*
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("saveData", getContext().MODE_PRIVATE);
        int dataListSize = sharedPreferences.getInt("dataListSize", 0);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Books>>() {
        }.getType();



*/

        String info = "";
        dataList.clear();

        for (Booksdb booksd : booksdbs) {
            dataList.add(booksd);
            String id = booksd.getId();
            String bookName = booksd.getBookName();
            String booksdesc = booksd.getDescription();

            info = info + "\n\n" + "id: " + id + "\n bookname: " + bookName + "\n bookcategory: " + booksdesc;
        }

        recyclerView.setAdapter(downloadVideoAdapter);

        Log.e("TAGDATA", info);


        /*for (int i = 0; i < dataListSize; i++) {
            try {
                Log.i("Order is ", "" + dataListSize); // Log the order
                String json = sharedPreferences.getString("downloadBookData_" + dataList.get(i).getId(), null);
                dataList = gson.fromJson(json, type);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }*/


    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean restored) {
        if (!restored) {
            youTubePlayer.cueVideo(videoUrl);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        Log.d("youtubeError", "onInitializationFailure: " + youTubeInitializationResult.toString());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }
}