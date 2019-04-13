package com.codingwithmitch.retrofitcaching;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SearchView searchView;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private ServiceGenerator serviceGenerator = ServiceGenerator.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view);
        searchView = findViewById(R.id.search_view);

        initRecyclerView();
        initSearchView();
    }

    private void searchApi(String url){
        hideKeyboard();

        if(url.contains("/")){
            String id = url.substring(url.indexOf("/") + 1);
            serviceGenerator.getApi().searchPhoto(id)
                    .enqueue(new Callback<Photo>() {
                        @Override
                        public void onResponse(Call<Photo> call, Response<Photo> response) {
                            Log.e(TAG, "log: -----------------------------");
                            Log.d(TAG, "onResponse: " + response.body());

                            if(response.raw().networkResponse() != null){
                                Log.d(TAG, "onResponse: response is from NETWORK...");
                            }
                            else if(response.raw().cacheResponse() != null
                                && response.raw().networkResponse() == null){
                                Log.d(TAG, "onResponse: response is from CACHE...");
                            }

                            List<Photo> photos = new ArrayList<>();
                            photos.add(response.body());
                            adapter.setPhotos(photos);
                        }

                        @Override
                        public void onFailure(Call<Photo> call, Throwable t) {
                            Log.e(TAG, "onFailure: ", t);
                            adapter.setPhotos(new ArrayList<Photo>());
                        }
                    });
        }
        else{
            serviceGenerator.getApi().searchPhotos(url)
                    .enqueue(new Callback<List<Photo>>() {
                        @Override
                        public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                            Log.d(TAG, "onResponse: " + response.body());
                            if(response.raw().networkResponse() != null){
                                Log.d(TAG, "onResponse: response is from NETWORK...");
                            }
                            else if(response.raw().cacheResponse() != null
                                    && response.raw().networkResponse() == null){
                                Log.d(TAG, "onResponse: response is from CACHE...");
                            }

                            if(response.body() == null){
                                adapter.setPhotos(new ArrayList<Photo>());
                            }
                            else{
                                adapter.setPhotos(response.body());
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Photo>> call, Throwable t) {
                            Log.e(TAG, "onFailure: ", t);
                            adapter.setPhotos(new ArrayList<Photo>());
                        }
                    });
        }
    }

    private void initSearchView(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchApi(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void initRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(20);
        recyclerView.addItemDecoration(itemDecorator);
        adapter = new RecyclerAdapter(initGlide());
        recyclerView.setAdapter(adapter);
    }

    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}


























