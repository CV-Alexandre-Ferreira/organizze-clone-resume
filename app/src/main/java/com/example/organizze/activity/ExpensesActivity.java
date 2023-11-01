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

public class ExpensesActivity extends AppCompatActivity {

    private EditText fieldValue, fieldDate, fieldCategory, fieldDescription;
    private Transference transference;
    private DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();
    private FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    private Double totalExpenses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_despesas);
        setContentView(R.layout.activity_expenses);

        fieldValue = findViewById(R.id.editValor);
        fieldDate = findViewById(R.id.editData);
        fieldCategory = findViewById(R.id.editCategoria);
        fieldDescription = findViewById(R.id.editDescricao);

        //Fill date field with current date
        fieldDate.setText(DateCustom.currentDate());

        recoverTotalExpenses();
    }

    public void saveExpenses(View view){
        if(validateExpensesField()){

            transference = new Transference();
            String date = fieldDate.getText().toString();
            Double recoveredValue = Double.parseDouble(fieldValue.getText().toString());

            transference.setValue(recoveredValue);
            transference.setCategory(fieldCategory.getText().toString());
            transference.setDescription(fieldDescription.getText().toString());
            transference.setDate(date);
            transference.setType("d");

            Double updatedExpenses = totalExpenses + recoveredValue;
            updateExpenses(updatedExpenses);

            transference.save(date);
            finish();

        }

    }

    public Boolean validateExpensesField(){

        String textValue = fieldValue.getText().toString();
        String textDate = fieldDate.getText().toString();
        String textCategory = fieldCategory.getText().toString();
        String textDescription = fieldDescription.getText().toString();

        if(!textValue.isEmpty() && !textDate.isEmpty() && !textCategory.isEmpty() && !textDescription.isEmpty()){

        }else {
            Toast.makeText(ExpensesActivity.this, "Fill in all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void recoverTotalExpenses(){
        String userEmail = auth.getCurrentUser().getEmail();
        String userId = Base64Custom.encodeBase64(userEmail);
        DatabaseReference userRef = firebaseRef.child("usuariosOrganizze")
                .child(userId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                totalExpenses = user.getTotalExpenses();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateExpenses(Double despesa){
        String emailUsuario = auth.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.encodeBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuariosOrganizze")
                .child(idUsuario);

        usuarioRef.child("totalExpenses").setValue(despesa);
    }

}
