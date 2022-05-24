package com.example.moviefx;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    RecyclerView recyclerViewAction;
    RecyclerView recyclerViewDrama;
    RecyclerView recyclerViewComedy;
    Adapter adapterAction;
    Adapter adapterDrama;
    Adapter adapterComedy;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    MaterialToolbar toolbar;
    Intent intent;



    int imgAction[] = {
            R.drawable.action_1,
            R.drawable.action_2,
            R.drawable.action_3,
            R.drawable.action_4
    };

    int imgDrama[] = {
            R.drawable.drama_1,
            R.drawable.drama_2,
            R.drawable.drama_3,
            R.drawable.drama_4
    };

    int imgComedy[] = {
            R.drawable.comedy_1,
            R.drawable.comedy_2,
            R.drawable.comedy_3,
            R.drawable.comedy_4
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.topAppbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

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
              default:
                  return true;
          }
          return true;
        }
    });



        // Setting recyclerView for Action movies section
        LinearLayoutManager layoutManagerAction
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewAction = findViewById(R.id.recyclerviewAction);
        recyclerViewAction.setLayoutManager(layoutManagerAction);

        adapterAction = new Adapter(this, imgAction);
        recyclerViewAction.setAdapter(adapterAction);

        // Setting recyclerView for Drama movies section
        LinearLayoutManager layoutManagerDrama
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewDrama = findViewById(R.id.recyclerviewDrama);
        recyclerViewDrama.setLayoutManager(layoutManagerDrama);

        adapterDrama = new Adapter(this, imgDrama);
        recyclerViewDrama.setAdapter(adapterDrama);

        // Setting recyclerView for Comedy movies section
        LinearLayoutManager layoutManagerComedy
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewComedy = findViewById(R.id.recyclerviewComedy);
        recyclerViewComedy.setLayoutManager(layoutManagerComedy);

        adapterComedy = new Adapter(this, imgComedy);
        recyclerViewComedy.setAdapter(adapterComedy);


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}