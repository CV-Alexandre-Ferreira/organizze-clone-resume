package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.organizze.MainActivity;
import com.example.organizze.R;
import com.example.organizze.adapter.AdapterTransference;
import com.example.organizze.config.FirebaseConfig;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.model.Transference;
import com.example.organizze.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private TextView textSalutation, textBalance;
    private FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
    private DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();

    private AdapterTransference adapterTransference;
    private List<Transference> transferences = new ArrayList<>();
    private Transference transference;

    private ValueEventListener valueEventListenerUser;
    private ValueEventListener valueEventListenerTransferences;

    private DatabaseReference userRef;
    private Double totalExpenses = 0.0;
    private Double totalIncome = 0.0;
    private Double userSummary = 0.0;
    private DatabaseReference transferenceRef;
    private String selectedMonthYear;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_principal);
        setContentView(R.layout.activity_principal);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        calendarView = findViewById(R.id.calendarView);
        textBalance = findViewById(R.id.textSaldo);
        textSalutation = findViewById(R.id.textSaudacao);
        recyclerView = findViewById(R.id.recyclerMovimentos);

        configCalendar();
        swipe();

        //adapter Config
        adapterTransference = new AdapterTransference(transferences, this);

        //recyclerView config
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterTransference);

    }

    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE; //bota o evento drag inativo(idle)
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                deleteTransference(viewHolder);

            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);

    }

    public void deleteTransference(RecyclerView.ViewHolder viewHolder){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Delete transaction");
        alertDialog.setMessage("Do you really want to delete the transaction?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int position = viewHolder.getAdapterPosition();
                transference = transferences.get(position);

                String userEmail = auth.getCurrentUser().getEmail();
                String userId = Base64Custom.encodeBase64(userEmail);
                transferenceRef = firebaseRef.child("movimentacao").child(userId)
                        .child(selectedMonthYear);

                transferenceRef.child(transference.getKey()).removeValue();
                adapterTransference.notifyItemRemoved(position);
                updateBalance();

                
            }
        });
        
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(PrincipalActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                adapterTransference.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        getSummary();
        getTransferences();
    }

    public void updateBalance(){

        String userEmail = auth.getCurrentUser().getEmail();
        String userId = Base64Custom.encodeBase64(userEmail);
        userRef = firebaseRef.child("usuariosOrganizze")
                .child(userId);

        if(transference.getType().equals("r")){
            totalIncome = totalIncome - transference.getValue();
            userRef.child("receitaTotal").setValue(totalIncome);
        }

        if(transference.getType().equals("d")){
            totalExpenses = totalExpenses -  transference.getValue();
            userRef.child("despesaTotal").setValue(totalExpenses);
        }

    }

    public void getTransferences(){
        String userEmail = auth.getCurrentUser().getEmail();
        String userId = Base64Custom.encodeBase64(userEmail);
        transferenceRef = firebaseRef.child("movimentacao").child(userId)
                .child(selectedMonthYear);

        valueEventListenerTransferences = transferenceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                transferences.clear();

                for(DataSnapshot data: snapshot.getChildren()) {
                    Transference transference = data.getValue(Transference.class);
                    transference.setKey(data.getKey());
                    transferences.add(transference);

                }
                
                adapterTransference.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getSummary(){

        String userEmail = auth.getCurrentUser().getEmail();
        String userId = Base64Custom.encodeBase64(userEmail);
        userRef = firebaseRef.child("usuariosOrganizze")
                .child(userId);

        Log.i("Evento", "Listener Adicionado");

        valueEventListenerUser = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                totalExpenses = user.getTotalExpenses();
                totalIncome = user.getTotalIncome();
                userSummary = totalIncome - totalExpenses;

                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resultFormatted = decimalFormat.format(userSummary);

                textSalutation.setText("Hello, " + user.getName());
                textBalance.setText("$ "+resultFormatted);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                auth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addExpenses(View view){
        startActivity(new Intent(this, ExpensesActivity.class));
    }
    public void addIncome(View view){

        startActivity(new Intent(this, IncomeActivity.class));

    }
    public void configCalendar(){
        CalendarDay currentDate = calendarView.getCurrentDate();
        String selectedMonth = String.format("%02d", (currentDate.getMonth()+ 1));
        selectedMonthYear = String.valueOf( selectedMonth + "" + currentDate.getYear() );
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

                String monthSelected = String.format("%02d", (date.getMonth()+ 1));
                selectedMonthYear = String.valueOf( monthSelected + "" + date.getYear() );

                transferenceRef.removeEventListener(valueEventListenerTransferences);
                getTransferences();

            }
        });
    }

    @Override
    protected void onStop() {
        userRef.removeEventListener(valueEventListenerUser);
        transferenceRef.removeEventListener(valueEventListenerTransferences);
        Log.i("Evento", "ValueEventListener Retirado");
        super.onStop();
    }
}