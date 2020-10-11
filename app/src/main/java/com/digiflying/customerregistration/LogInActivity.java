package com.digiflying.customerregistration;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogInActivity extends AppCompatActivity {

    @BindView(R.id.ed_login_username)
    EditText userName;

    @BindView(R.id.ed_login_password)
    EditText userPassword;

    @BindView(R.id.txt_forgotpassword)
    TextView forgetPassword;

    public FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);
        auth=FirebaseAuth.getInstance();
        TextView login=findViewById(R.id.btn_login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click();
            }
        });
    }

    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

//            if (firebaseUser == null) {
//                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
//                startActivity(intent);
//                finish();
//            }
            if (firebaseUser != null) {
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };


    public void Click(){
        String email=userName.getText().toString();
        String password=userPassword.getText().toString();

        if(email.isEmpty()){
            userName.setError("Enter Email");
            userName.requestFocus();
        }else if (password.isEmpty()){
            userPassword.setError("Enter Password");
            userPassword.requestFocus();
        }else if(!(email.isEmpty() && password.isEmpty())){

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LogInActivity.this, "ERROR",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(LogInActivity.this, "LogIn Successful",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LogInActivity.this, MainActivity.class));
                        finish();
                    }
                }
            });

        }
    }

    @OnClick(R.id.txt_signup)
    public void setSignUp(){
        Intent intent=new Intent(LogInActivity.this,SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.txt_forgotpassword)
    public void setForgetPassword(){
        Intent intent=new Intent(LogInActivity.this,ForgotPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }
}