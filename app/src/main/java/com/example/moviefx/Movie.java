package com.example.moviefx;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Movie extends AppCompatActivity {

    ImageView movie;
    AppCompatButton addBtn;
    StorageReference storageReference;
    int res_image;
    int imgUrl;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        addBtn = findViewById(R.id.add);
        movie = (ImageView) findViewById(R.id.movieImg);
        Bundle bundle = getIntent().getExtras();
        if (bundle!= null){
            res_image = bundle.getInt("my image");
            movie.setImageResource(res_image);

        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                ArrayList<String> arrImages = new ArrayList<>();

                imgUrl = res_image;
                String imgUrlStr = String.valueOf(imgUrl);

                userID = fAuth.getCurrentUser().getUid();

                DocumentReference documentReference = fStore.collection("images").document(userID);
                Map<String,Object> img = new HashMap<>();
                img.put("imgURL", imgUrlStr);
                documentReference.set(img).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("img","onSuccess: image added" + userID);
                    }
                });

            }
        });



    }

}