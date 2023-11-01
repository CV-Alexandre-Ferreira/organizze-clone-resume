package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.config.FirebaseConfig;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.helper.DateCustom;
import com.example.organizze.model.Transference;
import com.example.organizze.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class IncomeActivity extends AppCompatActivity {

    private EditText fieldValue, fieldDate, fieldCategory, fieldDescription;
    private Transference transference;
    private DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();
    private FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    private Double totalIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_receitas);
        setContentView(R.layout.activity_income);

        fieldValue = findViewById(R.id.editValorR);
        fieldDate = findViewById(R.id.editDataR);
        fieldCategory = findViewById(R.id.editCategoriaR);
        fieldDescription = findViewById(R.id.editDescricaoR);

        //fill in date field with current date
        fieldDate.setText(DateCustom.currentDate());

        recoverTotalIncome();
    }


    public void saveIncome(View view){
        if(validateIncomeFields()){

            transference = new Transference();
            String date = fieldDate.getText().toString();
            Double recoveredValue = Double.parseDouble(fieldValue.getText().toString());

            transference.setValue(recoveredValue);
            transference.setCategory(fieldCategory.getText().toString());
            transference.setDescription(fieldDescription.getText().toString());
            transference.setDate(date);
            transference.setType("r");

            Double updatedIncome = totalIncome + recoveredValue;
            updateIncome(updatedIncome);

            transference.save(date);
            finish();

        }

    }

    public Boolean validateIncomeFields(){

        String textValue = fieldValue.getText().toString();
        String textDate = fieldDate.getText().toString();
        String textCategory = fieldCategory.getText().toString();
        String textDescription = fieldDescription.getText().toString();

        if(!textValue.isEmpty() && !textDate.isEmpty() && !textCategory.isEmpty() && !textDescription.isEmpty()){

        }else {
            Toast.makeText(IncomeActivity.this, "Fill in all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    public void recoverTotalIncome(){
        String userEmail = auth.getCurrentUser().getEmail();
        String userId = Base64Custom.encodeBase64(userEmail);
        DatabaseReference userRef = firebaseRef.child("usuariosOrganizze")
                .child(userId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                totalIncome = user.getTotalIncome();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateIncome(Double receita){
        String userEmail = auth.getCurrentUser().getEmail();
        String userId = Base64Custom.encodeBase64(userEmail);
        DatabaseReference userRef = firebaseRef.child("usuariosOrganizze")
                .child(userId);

        userRef.child("totalIncome").setValue(receita);
    }
}