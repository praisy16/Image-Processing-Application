package com.emulator.whatsthatdog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class registerUser extends AppCompatActivity implements View.OnClickListener {

    private TextView rBanner, Register;
    private EditText UsernameRegister, PasswordRegister, AgeRegister, EmailRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        rBanner = (TextView) findViewById(R.id.rBanner);
        rBanner.setOnClickListener(this);

        Register = (Button) findViewById(R.id.Register);
        Register.setOnClickListener(this);


        UsernameRegister = (EditText) findViewById(R.id.UsernameRegister);
        //UsernameRegister.setOnClickListener(this);

        PasswordRegister = (EditText) findViewById(R.id.PasswordRegister);
        //PasswordRegister.setOnClickListener(this);

        AgeRegister = (EditText) findViewById(R.id.AgeRegister);
        //AgeRegister.setOnClickListener(this);

        EmailRegister = (EditText) findViewById(R.id.EmailRegister);
        //EmailRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rBanner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.Register:
                Register();

        }
    }

    private void Register(){
        String email = EmailRegister.getText().toString().trim();
        String Age = AgeRegister.getText().toString().trim();
        String Pass = PasswordRegister.getText().toString().trim();
        String userN = UsernameRegister.getText().toString().trim();

        if(userN.isEmpty()){

            UsernameRegister.setError("Username Required");
            UsernameRegister.requestFocus();
            return;
        }
        if(Pass.isEmpty()){

            PasswordRegister.setError("Password Required");
            PasswordRegister.requestFocus();
            return;
        }
        if(Pass.length() < 6){

            PasswordRegister.setError("Minimum Password Length is 6");
            PasswordRegister.requestFocus();
            return;
        }
        if(Age.isEmpty()){

            AgeRegister.setError("Age Required");
            AgeRegister.requestFocus();
            return;
        }
        if(Integer.valueOf(Age) < 18){

            AgeRegister.setError("Must Be 18 or Older");
            AgeRegister.requestFocus();
            return;
        }
        if(email.isEmpty()){

            EmailRegister.setError("Email Required");
            EmailRegister.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            EmailRegister.setError("Provide Valid Email");
            EmailRegister.requestFocus();
            return;

        }
        //Log.d("NOTHINGALLTHING", "CHECK");
         mAuth.createUserWithEmailAndPassword(email, Pass)
                 .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if(task.isSuccessful()){
                             User user = new User(userN, Age, email);

                             FirebaseDatabase.getInstance().getReference("Users")
                                     .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                     .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             if(task.isSuccessful()){
                                               Toast.makeText(registerUser.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                               //Log.d("SuccessALLTHING", "IT WORKED??");
                                               //redirect

                                             }
                                             else{
                                                Toast.makeText(registerUser.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                                 Log.d("FailALLTHING", "IT DIDNT WORK 1??");
                                             }
                                         }
                                     });

                         }
                         else{
                             Toast.makeText(registerUser.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                             //Log.d("FailALLTHING", "IT DIDNT WORK 2??");
                         }
                     }
                 });

    }

}