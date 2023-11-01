package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.config.FirebaseConfig;
import com.example.organizze.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText fieldEmail, fieldPassword;
    private Button buttonEnter;
    private User user;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fieldEmail = findViewById(R.id.editEmail2);
        fieldPassword = findViewById(R.id.editSenha2);
        buttonEnter = findViewById(R.id.buttonEntrar);

        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textEmail= fieldEmail.getText().toString();
                String textPassword = fieldPassword.getText().toString();



                if(!textEmail.isEmpty() && !textPassword.isEmpty()) {

                    user = new User();
                    user.setEmail(textEmail);
                    user.setPassword(textPassword);
                    validateLogin();


                } else {
                    Toast.makeText(LoginActivity.this, "Fill in all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validateLogin(){

        auth = FirebaseConfig.getFirebaseAuth();
        auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            openMainScreen();
                        }
                        else{

                            String exception = "";
                            try {
                                throw task.getException();
                            }
                           catch (FirebaseAuthInvalidCredentialsException e) {
                                exception = "Digite um email e senha válidos";
                            }
                            catch (FirebaseAuthInvalidUserException e){
                                exception = "Usuario não cadastrado";
                            }

                            catch (Exception e){
                                exception = "Erro ao logar usuario " + e.getMessage();
                                e.printStackTrace(); //printar no log
                            }

                            Toast.makeText(LoginActivity.this, exception, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    public void openMainScreen(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}