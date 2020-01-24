package com.example.armap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.armap.Helper.DBopearations;

public class DestinationDropDown extends AppCompatActivity {

    private Spinner destSpinner;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Button BtnSearch;

    private DBopearations dBopearations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_drop_down);
        dBopearations=new DBopearations(this,getResources().getString(R.string.DBname));
        prefs=getSharedPreferences(getResources().getString(R.string.SHARED_PREF_NAME),MODE_PRIVATE);
        editor=prefs.edit();
        destSpinner=findViewById(R.id.endnode);
        BtnSearch=findViewById(R.id.btnsearch);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,dBopearations.getAllEndNodeLabel());
        destSpinner.setAdapter(dataAdapter);
        destSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Destination=destSpinner.getSelectedItem().toString();
                editor.putString("endpoint",Destination);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        BtnSearch.setOnClickListener(v -> {
            Intent searchActivity=new Intent(DestinationDropDown.this,SearchLocation.class);
            startActivity(searchActivity);
        });


    }
}
