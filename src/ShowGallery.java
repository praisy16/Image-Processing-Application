package com.emulator.whatsthatdog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowGallery extends AppCompatActivity {

    private Button button;
    private RecyclerView TheRecycle;
    private ArrayList<String> urlList = new ArrayList<>();
    private Adapt imageAdapter;
    private String Thepass;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_gallery);
        getSupportActionBar().hide();

        Bundle grab = getIntent().getExtras();
        if(grab != null)
        {
            Thepass = grab.getString("key");
        }

        button = findViewById(R.id.TheBack);
        initialize();
        load();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowGallery.this,homePage.class);
                intent.putExtra("key",Thepass);
                startActivity(intent);
            }
        });

    }
    private void initialize()
    {
        TheRecycle = findViewById(R.id.recycle);
        TheRecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        imageAdapter = new Adapt(urlList,this);
        TheRecycle.setAdapter(imageAdapter);
    }
    private void load(){
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null && snapshot.hasChildren()){
                    for(DataSnapshot d : snapshot.getChildren()){
                        urlList.add(d.getValue().toString());
                    }
                    imageAdapter.setData(urlList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        DatabaseReference dataref = FirebaseDatabase.getInstance().getReference().child(Thepass);
        dataref.addValueEventListener(listener);
    }
}