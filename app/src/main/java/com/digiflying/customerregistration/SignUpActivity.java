package com.digiflying.customerregistration;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.ed_username)
    EditText userName;

    @BindView(R.id.ed_email)
    EditText userEmail;

    @BindView(R.id.ed_password)
    EditText userPassword;

    public FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        auth=FirebaseAuth.getInstance();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }


    @OnClick(R.id.btn_sign)
    public void onClick() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (email.isEmpty()) {
            userName.setError("Enter Email");
            userName.requestFocus();
        } else if (password.isEmpty()) {
            userPassword.setError("Enter Password");
            userPassword.requestFocus();
        } else if (!(email.isEmpty() && password.isEmpty())) {

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "ERROR", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Successfully SignIn", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                    });
        }
    }

    @OnClick(R.id.txt_login)
    public void setlogin(){
        Intent intent=new Intent(SignUpActivity.this,LogInActivity.class);
        startActivity(intent);
        finish();
    }
}