package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.config.FirebaseConfig;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignUpActivity extends AppCompatActivity {

    private EditText fieldName, fieldEmail, fieldPassword;
    private Button signUpButton;
    private FirebaseAuth auth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setTitle("Cadastro");

        fieldName = findViewById(R.id.editNome);
        fieldEmail = findViewById(R.id.editEmail);
        fieldPassword = findViewById(R.id.editEmail2);
        signUpButton = findViewById(R.id.buttonCadastrar);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textName = fieldName.getText().toString();
                String textEmail= fieldEmail.getText().toString();
                String textPassword = fieldPassword.getText().toString();

                //Validate if fields were filled
                if(!textName.isEmpty() && !textEmail.isEmpty() && !textPassword.isEmpty()) {

                    user = new User();
                    user.setName(textName);
                    user.setEmail(textEmail);
                    user.setPassword(textPassword);
                signUpUser();

                } else {
                    Toast.makeText(SignUpActivity.this, "Fill in all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void signUpUser(){

        auth = FirebaseConfig.getFirebaseAuth();
        auth.createUserWithEmailAndPassword(
                user.getEmail(), user.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    String userId = Base64Custom.encodeBase64(user.getEmail());
                    user.setUserId(userId);
                    user.save();
                    finish();
                }
                else{
                    String exception = "";
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException e){
                        exception = "Digite uma senha mais forte";
                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        exception = "Digite um email válido";
                    }catch (FirebaseAuthUserCollisionException e) {
                        exception = "Essa conta já foi cadastrada";
                    }catch (Exception e){
                        exception = "Erro ao cadastrar usuario " + e.getMessage();
                        e.printStackTrace(); //printar no log
                    }

                    Toast.makeText(SignUpActivity.this, exception, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}