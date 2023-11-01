package com.example.organizze.model;

import com.example.organizze.config.FirebaseConfig;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.helper.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Transference {

    private String date;
    private String category;
    private String description;
    private String type;
    private double value;
    private String key;

    public Transference() {
    }

    public void save(String chosenDate){

        FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
        String userId = Base64Custom.encodeBase64(auth.getCurrentUser().getEmail());
        String monthYear = DateCustom.chosenMonthYearDate(chosenDate);
        DatabaseReference firebase = FirebaseConfig.getFirebaseDatabase();
        firebase.child("movimentacao")
                .child(userId)
                .child(monthYear)
                .push()
                .setValue(this);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
