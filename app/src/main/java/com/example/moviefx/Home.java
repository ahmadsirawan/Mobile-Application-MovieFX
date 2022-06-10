package com.example.moviefx;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class Home extends AppCompatActivity implements AdapterAction.OnMovieListener, AdapterDrama.OnMovieListener, AdapterComedy.OnMovieListener {

    RecyclerView recyclerViewAction;
    RecyclerView recyclerViewDrama;
    RecyclerView recyclerViewComedy;
    AdapterAction adapterAction;
    AdapterDrama adapterDrama;
    AdapterComedy adapterComedy;
    ImageButton searchBarIcon;


    List<MovieModel> ActionMovieList;
    List<MovieModel> DramaMovieList;
    List<MovieModel> ComedyMovieList;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    MaterialToolbar toolbar;
    Intent intent;
    TextView userWelcome;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button veryBtn;
    TextView veryMsg;
    ProgressBar progressBar;
    ProgressBar progressBar2;
    ProgressBar progressBar3;

    private JsonArrayRequest request ;
    private RequestQueue requestQueue ;


    private static String JSON_URL_ACTION = "https://api.themoviedb.org/3/discover/movie?api_key=007d372694c979392339b81489989b16&with_genres=28";

    private static String JSON_URL_DRAMA = "https://api.themoviedb.org/3/discover/movie?api_key=007d372694c979392339b81489989b16&with_genres=18";

    private static String JSON_URL_COMEDY = "https://api.themoviedb.org/3/discover/movie?api_key=007d372694c979392339b81489989b16&with_genres=35";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        searchBarIcon = (ImageButton) findViewById(R.id.search_bar);




        ActionMovieList = new ArrayList<>();
       DramaMovieList = new ArrayList<>();
       ComedyMovieList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar3 = findViewById(R.id.progressBar3);




        GetActionData getActionData = new GetActionData();
        getActionData.execute();

        GetDramaData getDramaData = new GetDramaData();
        getDramaData.execute();

        GetComedyData getComedyData = new GetComedyData();
        getComedyData.execute();



        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        veryBtn = findViewById(R.id.verBtn);
        veryMsg = findViewById(R.id.verMsg);

        final FirebaseUser user = fAuth.getCurrentUser();

        if (!user.isEmailVerified()){
            veryMsg.setVisibility(View.VISIBLE);
            veryBtn.setVisibility(View.VISIBLE);

            veryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Home.this, "A verification email has been sent, Please click on verify to be able to login " , Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("fail", "onFailure: Email was not sent!");
                        }
                    });
                }
            });
        }

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
                intent = new Intent(Home.this, Search.class);
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
                  intent = new Intent(Home.this, Home.class);
                  startActivity(intent);
                  break;
              case R.id.nav_list:
                   intent = new Intent(Home.this, MyList.class);
                  startActivity(intent);
                  break;
              case R.id.nav_tending:
                  intent = new Intent(Home.this, Trending.class);
                  startActivity(intent);
                  break;
              case R.id.nav_upcoming:
                  intent = new Intent(Home.this, Upcoming.class);
                  startActivity(intent);
                  break;
              case R.id.nav_rating:
                  intent = new Intent(Home.this, TopRated.class);
                  startActivity(intent);
                  break;
              case R.id.nav_location:
                  intent = new Intent(Home.this, Map.class);
                  startActivity(intent);
                  break;
              case R.id.logout:
                  intent = new Intent(Home.this, Login.class);
                  startActivity(intent);
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

        Intent intent = new Intent(Home.this, Movie.class);
        intent.putExtra("image", ActionMovieList.get(position).getImg());
        intent.putExtra("title", ActionMovieList.get(position).getName());
        intent.putExtra("date", ActionMovieList.get(position).getDate());
        intent.putExtra("overview", ActionMovieList.get(position).getOverview());
        intent.putExtra("rate", ActionMovieList.get(position).getRating());

        startActivity(intent);

    }

    @Override
    public void onMovieClick2(int position) {

        Intent intent = new Intent(Home.this, Movie.class);
        intent.putExtra("image", DramaMovieList.get(position).getImg());
        intent.putExtra("title", DramaMovieList.get(position).getName());
        intent.putExtra("date", DramaMovieList.get(position).getDate());
        intent.putExtra("overview", DramaMovieList.get(position).getOverview());
        intent.putExtra("rate", DramaMovieList.get(position).getRating());

        startActivity(intent);

    }

    @Override
    public void onMovieClick3(int position) {

        Intent intent = new Intent(Home.this, Movie.class);
        intent.putExtra("image", ComedyMovieList.get(position).getImg());
        intent.putExtra("title", ComedyMovieList.get(position).getName());
        intent.putExtra("date", ComedyMovieList.get(position).getDate());
        intent.putExtra("overview", ComedyMovieList.get(position).getOverview());
        intent.putExtra("rate", ComedyMovieList.get(position).getRating());
        startActivity(intent);

    }

    public class GetActionData extends AsyncTask<String, String, String> {



        @Override
        protected String doInBackground(String... strings) {

            String current= "";

            try{
                URL url;
                HttpURLConnection urlConnection = null;

                try {
                    url = new URL(JSON_URL_ACTION);
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

                    ActionMovieList.add(model);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            addActionIntoRecyclerView(ActionMovieList);
            progressBar.setVisibility(View.GONE);

        }
    }



    public class GetDramaData extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... strings) {

            String current= "";

            try{
                URL url;
                HttpURLConnection urlConnection = null;

                try {
                    url = new URL(JSON_URL_DRAMA);
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
            progressBar2.setVisibility(View.VISIBLE);
            progressBar2.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);

        }


        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray   = jsonObject.getJSONArray("results");

                for (int i = 0 ; i< jsonArray.length() ; i++){

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                    MovieModel model = new MovieModel();
                    model.setId(jsonObject1.getString("id"));
                    model.setName(jsonObject1.getString("title"));
                    model.setOverview(jsonObject1.getString("overview"));
                    model.setDate(jsonObject1.getString("release_date"));
                    model.setRating(jsonObject1.getString("vote_average"));
                    model.setImg("https://image.tmdb.org/t/p/w500" + jsonObject1.getString("poster_path"));

                    DramaMovieList.add(model);


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            addDramaIntoRecyclerView(DramaMovieList);
            progressBar2.setVisibility(View.GONE);

        }
    }


    public class GetComedyData extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... strings) {

            String current= "";

            try{
                URL url;
                HttpURLConnection urlConnection = null;

                try {
                    url = new URL(JSON_URL_COMEDY);
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
            progressBar3.setVisibility(View.VISIBLE);
            progressBar3.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);

        }


        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray   = jsonObject.getJSONArray("results");

                for (int i = 0 ; i< jsonArray.length() ; i++){

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                    MovieModel model = new MovieModel();
                    model.setId(jsonObject1.getString("id"));
                    model.setName(jsonObject1.getString("title"));
                    model.setOverview(jsonObject1.getString("overview"));
                    model.setDate(jsonObject1.getString("release_date"));
                    model.setRating(jsonObject1.getString("vote_average"));
                    model.setImg("https://image.tmdb.org/t/p/w500" + jsonObject1.getString("poster_path"));

                    ComedyMovieList.add(model);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            addComedyIntoRecyclerView(ComedyMovieList);
            progressBar3.setVisibility(View.GONE);

        }
    }




    private void addActionIntoRecyclerView(List<MovieModel> actionMovieList){


        LinearLayoutManager layoutManagerAction
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewAction = findViewById(R.id.recyclerviewAction);
        recyclerViewAction.setLayoutManager(layoutManagerAction);

        adapterAction = new AdapterAction(this, actionMovieList, this);
        recyclerViewAction.setAdapter(adapterAction);
    }

    private void addDramaIntoRecyclerView(List<MovieModel> dramaMovieList){

        // Setting recyclerView for Drama movies section
        LinearLayoutManager layoutManagerDrama
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewDrama = findViewById(R.id.recyclerviewDrama);
        recyclerViewDrama.setLayoutManager(layoutManagerDrama);

        adapterDrama = new AdapterDrama(this, dramaMovieList, this);
        recyclerViewDrama.setAdapter(adapterDrama);

    }

    private void addComedyIntoRecyclerView(List<MovieModel> comedyMovieList){

        // Setting recyclerView for Drama movies section
        LinearLayoutManager layoutManagerComedy
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewComedy = findViewById(R.id.recyclerviewComedy);
        recyclerViewComedy.setLayoutManager(layoutManagerComedy);

        adapterComedy = new AdapterComedy(this, comedyMovieList, this);
        recyclerViewComedy.setAdapter(adapterComedy);

    }


}