package com.example.moviefx;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Movie extends AppCompatActivity {

    ImageView movie;
    TextView title, overview, date, rate;

    AppCompatButton addBtn, removeBtn;
    StorageReference storageReference;
    String res_image;
    String imgUrl;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    String docID;
    Bundle bundle;

    private static String JSON_URL= "https://api.themoviedb.org/3/movie/now_playing?api_key=007d372694c979392339b81489989b16";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();


        addBtn = findViewById(R.id.add);
        removeBtn = findViewById(R.id.removeBtn);
        movie = (ImageView) findViewById(R.id.movieImg);

        title = (TextView) findViewById(R.id.movieTitle);
        overview = (TextView) findViewById(R.id.movieOverview);
        date = (TextView) findViewById(R.id.movieDate);
        rate = (TextView) findViewById(R.id.movieRate);
        userID = fAuth.getCurrentUser().getUid();





         bundle = getIntent().getExtras();
        if (bundle!= null){
            res_image = bundle.getString("image");
            title.setText(bundle.getString("title"));
            overview.setText(bundle.getString("overview"));
            date.setText(bundle.getString("date"));
            rate.setText(bundle.getString("rate"));
            Glide.with(Movie.this).load(res_image).into(movie);



            fStore.collection("users").document(userID).collection("movies").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){


                        if (bundle.getString("title").equalsIgnoreCase(documentSnapshot.getString("title"))){
//                            Toast.makeText(Movie.this, "A verification email has been sent, Please click on verify to be able to login " , Toast.LENGTH_LONG).show();
                            addBtn.setVisibility(View.GONE);
                            removeBtn.setVisibility(View.VISIBLE);
                            docID = documentSnapshot.getId();


                        }



                    }

                }
            });



        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                imgUrl = res_image;
                String imgUrlStr = String.valueOf(imgUrl);


                Map<String,Object> movie = new HashMap<>();
                movie.put("title", bundle.getString("title"));
                movie.put("overview", bundle.getString("overview"));
                movie.put("date", bundle.getString("date"));
                movie.put("rate", bundle.getString("rate"));
                movie.put("imgURL", imgUrlStr);
               fStore.collection("users").document(userID).collection("movies").add(movie).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                   @Override
                   public void onSuccess(DocumentReference documentReference) {
                       Toast.makeText(Movie.this, "Movie Added To Your List", Toast.LENGTH_SHORT).show();
                       addBtn.setVisibility(View.GONE);
                       removeBtn.setVisibility(View.VISIBLE);
                       GetMovieData getData = new GetMovieData();
                       getData.execute();


                   }
               });


            }

        });


        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fStore.collection("users").document(userID).collection("movies").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){


                            if (bundle.getString("title").equalsIgnoreCase(documentSnapshot.getString("title"))){


                                docID = documentSnapshot.getId();

                                fStore.collection("users").document(userID).collection("movies").document(docID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Movie.this, "Movie Removed From My List " , Toast.LENGTH_LONG).show();
                                        removeBtn.setVisibility(View.GONE);
                                        addBtn.setVisibility(View.VISIBLE);
//                                        MyList.Myist.remove(MyList.model);
//                                        MyList.adapter.notifyDataSetChanged();
                                    }
                                });


                            }



                        }

                    }
                });





            }
        });

}

    public class GetMovieData extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... strings) {

            String current= "";

            try{
                URL url;
                HttpURLConnection urlConnection = null;

                try {
                    url = new URL(JSON_URL);
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


                    if(jsonObject1.getString("title").equalsIgnoreCase(bundle.getString("title"))){



                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
                            NotificationManager manager = getSystemService(NotificationManager.class);
                            manager.createNotificationChannel(channel);
                        }


                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(Movie.this, "My Notification");
                        builder.setContentTitle("Now Playing");
                        builder.setContentText(jsonObject1.getString("title") + " is now playing in theaters");
                        builder.setSmallIcon(R.drawable.moviefx_logo);
                        builder.setAutoCancel(true);

                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(Movie.this);
                        managerCompat.notify(1, builder.build());
                    }



                }


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }


    }

