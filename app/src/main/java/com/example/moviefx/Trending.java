package com.example.moviefx;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Trending extends AppCompatActivity implements AdapterLists.OnMovieListener {




    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    MaterialToolbar toolbar;
    Intent intent;
    TextView userWelcome;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    ImageButton searchBarIcon;

    RecyclerView recyclerView;
    AdapterLists adapter;
    ProgressBar progressBar;

    List<MovieModel> TrendingMovieList;

    private static String JSON_URL_TRENDING= "https://api.themoviedb.org/3/trending/movie/day?api_key=007d372694c979392339b81489989b16";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending);

        toolbar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        progressBar = findViewById(R.id.progressBar);
        searchBarIcon = (ImageButton) findViewById(R.id.search_bar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();


        TrendingMovieList = new ArrayList<>();


        GetMovieData getData = new GetMovieData();
        getData.execute();

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {


                View header = navigationView.getHeaderView(0);
                userWelcome = (TextView) header.findViewById(R.id.welcomeUser);
                userWelcome.setText("Hi, " + value.getString("username").substring(0, 1).toUpperCase() + value.getString("username").substring(1).toLowerCase() + "!");

            }
        });


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        searchBarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Trending.this, Search.class);
                startActivity(intent);
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id)
                {
                    case R.id.home:
                        intent = new Intent(Trending.this, Home.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_list:
                        intent = new Intent(Trending.this, MyList.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_tending:
                        intent = new Intent(Trending.this, Trending.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_upcoming:
                        intent = new Intent(Trending.this, Upcoming.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_rating:
                        intent = new Intent(Trending.this, TopRated.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_location:
                        intent = new Intent(Trending.this, Map.class);
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });






    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieClick(int position) {

        Intent intent = new Intent(getApplicationContext(), Movie.class);
        intent.putExtra("image", TrendingMovieList.get(position).getImg());
        intent.putExtra("title", TrendingMovieList.get(position).getName());
        intent.putExtra("date", TrendingMovieList.get(position).getDate());
        intent.putExtra("overview", TrendingMovieList.get(position).getOverview());
        intent.putExtra("rate", TrendingMovieList.get(position).getRating());

        startActivity(intent);

    }

    public class GetMovieData extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... strings) {

            String current= "";

            try{
                URL url;
                HttpURLConnection urlConnection = null;

                try {
                    url = new URL(JSON_URL_TRENDING);
                    urlConnection = (HttpURLConnection) url.openConnection();


                    InputStream is = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);

                    int data = isr.read();
                    while(data != -1){
                        current += (char) data;
                        data = isr.read();

                    }
                    return current;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(urlConnection != null){
                        urlConnection.disconnect();
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            return current;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);

        }


        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray   = jsonObject.getJSONArray("results");

                for (int i = 0 ; i < jsonArray.length() ; i++){

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);



                    MovieModel model = new MovieModel();
                    model.setId(jsonObject1.getString("id"));
                    model.setName(jsonObject1.getString("title"));
                    model.setOverview(jsonObject1.getString("overview"));
                    model.setDate(jsonObject1.getString("release_date"));
                    model.setRating(jsonObject1.getString("vote_average"));
                    model.setImg("https://image.tmdb.org/t/p/w500" + jsonObject1.getString("poster_path"));

                    TrendingMovieList.add(model);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            addMovieIntoRecyclerView(TrendingMovieList);
            progressBar.setVisibility(View.GONE);

        }
    }

    private void addMovieIntoRecyclerView(List<MovieModel> movieList){


        recyclerView = findViewById(R.id.recyclerviewAction);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new AdapterLists(this, movieList, this);
        recyclerView.setAdapter(adapter);
    }

}