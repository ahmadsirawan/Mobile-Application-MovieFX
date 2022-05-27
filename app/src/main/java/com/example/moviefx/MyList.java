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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;

public class MyList extends AppCompatActivity implements AdapterLists.OnMovieListener {

    RecyclerView recyclerViewAction;
    AdapterLists adapterAction;


    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    MaterialToolbar toolbar;
    Intent intent;
    TextView userWelcome;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;



    int imgAction[] = {
            R.drawable.mylist_1,
            R.drawable.mylist_2,
            R.drawable.mylist_3,
            R.drawable.mylist_4
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        toolbar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {


                View header = navigationView.getHeaderView(0);
                userWelcome = (TextView) header.findViewById(R.id.welcomeUser);
                userWelcome.setText("Hi, " + value.getString("username") + "!");


            }
        });


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
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
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
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
        LinearLayoutManager layoutManagerAction
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewAction = findViewById(R.id.recyclerviewAction);
        recyclerViewAction.setLayoutManager(new GridLayoutManager(this, 2));

        adapterAction = new AdapterLists(this, imgAction,this);
        recyclerViewAction.setAdapter(adapterAction);

        DocumentReference documentReference2 = fStore.collection("images").document(userId);
        documentReference2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                imgAction[0] = Integer.valueOf(value.getString("imgURL"));
                adapterAction.notifyDataSetChanged();


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


        Intent intent = new Intent(MyList.this, Movie.class);
        intent.putExtra("my image", imgAction[position]);



        startActivity(intent);

    }
}