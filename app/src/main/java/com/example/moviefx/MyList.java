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
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyList extends AppCompatActivity implements AdapterLists.OnMovieListener {

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


    public static RecyclerView recyclerView;
    public static AdapterLists adapter;

    public static List<MovieModel> Myist;
    public static  MovieModel model;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        toolbar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        searchBarIcon = (ImageButton) findViewById(R.id.search_bar);



        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        Myist = new ArrayList<>();



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
                intent = new Intent(MyList.this, Search.class);
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
                        intent = new Intent(MyList.this, Home.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_list:
                        intent = new Intent(MyList.this, MyList.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_tending:
                        intent = new Intent(MyList.this, Trending.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_upcoming:
                        intent = new Intent(MyList.this, Upcoming.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_rating:
                        intent = new Intent(MyList.this, TopRated.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_location:
                        intent = new Intent(MyList.this, Map.class);
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



        // Setting recyclerView for My List movies section

        recyclerView = findViewById(R.id.recyclerviewAction);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new AdapterLists(this, Myist,this);
        recyclerView.setAdapter(adapter);

        

        DocumentReference documentReference2 = fStore.collection("users").document(userId).collection("images").document();

        fStore.collection("users").document(userId).collection("movies").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    model = new MovieModel();

                    model.setId("");
                    model.setName(documentSnapshot.getString("title"));
                    model.setOverview(documentSnapshot.getString("overview"));
                    model.setDate(documentSnapshot.getString("date"));
                    model.setRating(documentSnapshot.getString("rate"));
                    model.setImg(documentSnapshot.getString("imgURL"));

                    Myist.add(model);
                    adapter.notifyDataSetChanged();

                }

            }
        });


    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
        adapter.notifyDataSetChanged();
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
        intent.putExtra("image", Myist.get(position).getImg());
        intent.putExtra("title", Myist.get(position).getName());
        intent.putExtra("date", Myist.get(position).getDate());
        intent.putExtra("overview", Myist.get(position).getOverview());
        intent.putExtra("rate", Myist.get(position).getRating());

        startActivity(intent);

    }
}