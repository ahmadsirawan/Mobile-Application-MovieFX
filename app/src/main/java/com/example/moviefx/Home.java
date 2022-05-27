package com.example.moviefx;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;

public class Home extends AppCompatActivity implements Adapter.OnMovieListener {

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
    TextView userWelcome;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button veryBtn;
    TextView veryMsg;






    public int imgAction[] = {
            R.drawable.action_1,
            R.drawable.action_2,
            R.drawable.action_3,
            R.drawable.action_4,

    };

    public int imgDrama[] = {
            R.drawable.drama_1,
            R.drawable.drama_2,
            R.drawable.drama_3,
            R.drawable.drama_4
    };

    public int imgComedy[] = {
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



        // Setting recyclerView for Action movies section
        LinearLayoutManager layoutManagerAction
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewAction = findViewById(R.id.recyclerviewAction);
        recyclerViewAction.setLayoutManager(layoutManagerAction);

        adapterAction = new Adapter(this, imgAction, this);
        recyclerViewAction.setAdapter(adapterAction);


        // Setting recyclerView for Drama movies section
        LinearLayoutManager layoutManagerDrama
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewDrama = findViewById(R.id.recyclerviewDrama);
        recyclerViewDrama.setLayoutManager(layoutManagerDrama);

        adapterDrama = new Adapter(this, imgDrama, this);
        recyclerViewDrama.setAdapter(adapterDrama);

        // Setting recyclerView for Comedy movies section
        LinearLayoutManager layoutManagerComedy
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewComedy = findViewById(R.id.recyclerviewComedy);
        recyclerViewComedy.setLayoutManager(layoutManagerComedy);

        adapterComedy = new Adapter(this, imgComedy, this);
        recyclerViewComedy.setAdapter(adapterComedy);


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

            intent.putExtra("my image", imgAction[position]);


        startActivity(intent);

    }
}