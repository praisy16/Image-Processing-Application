package com.emulator.whatsthatdog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView NewAccount, forgotPassword;
    private EditText eLogEmail, eLogPass;
    private Button Login;
    private String Thepass;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        NewAccount = (TextView) findViewById(R.id.NewAccount);
        NewAccount.setOnClickListener(this);

        Login = (Button) findViewById(R.id.Login);
        Login.setOnClickListener(this);

        eLogEmail = (EditText) findViewById(R.id.logEmail);
        eLogPass = (EditText) findViewById(R.id.logPass);
        mAuth = FirebaseAuth.getInstance();

        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.NewAccount:
                startActivity(new Intent(this, registerUser.class));
                break;
            case R.id.Login:
                userLogin();
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
        }
    }


    private void userLogin(){

        String email = eLogEmail.getText().toString().trim();
        String password = eLogPass.getText().toString().trim();

        if(email.isEmpty()){

            eLogEmail.setError("Email is required!");
            eLogEmail.requestFocus();
            return;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            eLogEmail.setError("Please enter a valid email");
            return;
        }
        if(password.isEmpty()){
            eLogPass.setError("Password is required");
            eLogPass.requestFocus();
            return;
        }
        if(password.length() < 6){
            eLogPass.setError("Min password length is 6 characters");
            eLogPass.requestFocus();
            return;

        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //redirect to user page
                    Intent intent = new Intent(MainActivity.this,homePage.class);
                    Thepass = password+"_images";
                    intent.putExtra("key",Thepass);
                    startActivity(intent);
                    //startActivity(new Intent(MainActivity.this, homePage.class));
                }
                else{
                    Toast.makeText(MainActivity.this, "Failed to Login! Please check your credentials", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}