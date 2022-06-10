package com.example.moviefx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class Search extends AppCompatActivity implements AdapterLists.OnMovieListener {

    EditText search;
    FloatingActionButton backBtn;

    Intent intent;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;


    RecyclerView recyclerView;
    AdapterLists adapter;
    ProgressBar progressBar;

    List<MovieModel> TopMovieList;

    static String query;

    private static String JSON_URL_TOP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search = (EditText) findViewById(R.id.searchInput);
        backBtn = (FloatingActionButton) findViewById(R.id.back);

        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();


        TopMovieList = new ArrayList<>();


        search.requestFocus();
        search.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(TextUtils.isEmpty(search.getText())){
                        Toast.makeText(Search.this, "Search Field Cannot Be Empty!" , Toast.LENGTH_SHORT).show();

                    } else {
                        TopMovieList.clear();
                        query = search.getText().toString();
                        JSON_URL_TOP = "https://api.themoviedb.org/3/search/movie?api_key=007d372694c979392339b81489989b16&query=" + query;
                        GetMovieData getData = new GetMovieData();
                        getData.execute();

                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(search.getWindowToken(), 0);
                    }
                    handled = true;

                }
                return handled;
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }


        @Override
        public void onMovieClick ( int position){

            Intent intent = new Intent(getApplicationContext(), Movie.class);
            intent.putExtra("image", TopMovieList.get(position).getImg());
            intent.putExtra("title", TopMovieList.get(position).getName());
            intent.putExtra("date", TopMovieList.get(position).getDate());
            intent.putExtra("overview", TopMovieList.get(position).getOverview());
            intent.putExtra("rate", TopMovieList.get(position).getRating());

            startActivity(intent);
        }


        public class GetMovieData extends AsyncTask<String, String, String> {


            @Override
            protected String doInBackground(String... strings) {

                String current = "";

                try {
                    URL url;
                    HttpURLConnection urlConnection = null;

                    try {
                        url = new URL(JSON_URL_TOP);
                        urlConnection = (HttpURLConnection) url.openConnection();


                        InputStream is = urlConnection.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);

                        int data = isr.read();
                        while (data != -1) {
                            current += (char) data;
                            data = isr.read();

                        }
                        return current;

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                return current;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
                progressBar.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);

            }


            @Override
            protected void onPostExecute(String s) {

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                        MovieModel model = new MovieModel();
                        model.setId(jsonObject1.getString("id"));
                        model.setName(jsonObject1.getString("title"));
                        model.setOverview(jsonObject1.getString("overview"));
                        model.setDate(jsonObject1.getString("release_date"));
                        model.setRating(jsonObject1.getString("vote_average"));
                        model.setImg("https://image.tmdb.org/t/p/w500" + jsonObject1.getString("poster_path"));

                        TopMovieList.add(model);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                addMovieIntoRecyclerView(TopMovieList);
                progressBar.setVisibility(View.GONE);

            }
        }

            private void addMovieIntoRecyclerView(List<MovieModel> movieList) {


                recyclerView = findViewById(R.id.recyclerviewAction);
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

                adapter = new AdapterLists(this, movieList, this);
                recyclerView.setAdapter(adapter);


        }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//        MenuItem menuItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) menuItem.getActionView();
//
//        searchView.setQueryHint("Type here to search");
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
//
//
//        return super.onCreateOptionsMenu(menu);
//    }
    }
