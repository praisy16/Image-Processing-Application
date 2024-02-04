package com.emulator.whatsthatdog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView LogOut;
    private ImageView Back;
    private Button DeleteButton;
    String usernameV;
    String emailV;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef;
    private TextView viewUsername;
    private TextView viewEmail;
    private TextView updatedUsername;
    private String inputText = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        viewUsername = findViewById(R.id.username);
        viewEmail = findViewById(R.id.email);

        LogOut = findViewById(R.id.logout);
        Back = (ImageView) findViewById(R.id.imageView2);
        DeleteButton = (Button) findViewById(R.id.button);

        updatedUsername = findViewById(R.id.changeUsername);

        LogOut.setOnClickListener(this);
        Back.setOnClickListener(this);
        DeleteButton.setOnClickListener(this);
        updatedUsername.setOnClickListener(this);
    }


    //retrieve data
    @Override
    protected void onStart() {

        super.onStart();
        if (mUser == null){
            sendUserToLoginActivity();
        }else{
            mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //if user exists, get data
                    if (dataSnapshot.exists()){
                        usernameV = dataSnapshot.child("username").getValue().toString();
                        emailV = dataSnapshot.child("email").getValue().toString();
                        viewUsername.setText(usernameV);
                        viewEmail.setText(emailV);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Data could not be fetched.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void sendUserToLoginActivity(){
        Intent in = new Intent (ProfileActivity.this, MainActivity.class);
        startActivity(in);
        finish();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){


            //log out of the account
            case R.id.logout:
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setMessage("Are you sure you would like to log out of the account?");
            builder.setTitle("Confirm");

            builder.setCancelable(false);

            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                //sign out and go back to first page
                FirebaseAuth.getInstance().signOut();
                Intent logOut = new Intent (ProfileActivity.this, MainActivity.class);
                startActivity(logOut);
                finish();
            });

            builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.cancel();
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            break;



            //back to homepage
            case R.id.imageView2:
                Intent back = new Intent (ProfileActivity.this, homePage.class);
                startActivity(back);
                break;



            //updates username
            case R.id.changeUsername:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("Type in your new username");

                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                builder1.setView(input);

                builder1.setPositiveButton("Update username", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        inputText = input.getText().toString();

                        //save the input text to fb data
                        mUserRef.child(mUser.getUid()).child("username").setValue(inputText);
                        }

                });
                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });

                builder1.show();
                break;


            //delete the account
            case R.id.button:
                AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileActivity.this);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Deleting this account will result in completely removing your account from the system.");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ProfileActivity.this, "Account Deleted", Toast.LENGTH_LONG).show();
                                    Intent DeleteAccount = new Intent (ProfileActivity.this, MainActivity.class);
                                    startActivity(DeleteAccount);

                                } else{
                                    Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

                dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertD = dialog.create();
                alertD.show();
                break;
        }
        }
    }