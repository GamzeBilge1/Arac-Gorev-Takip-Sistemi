package com.gamzebilge.myapplication55;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class list extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ArrayList<String> vehicleList;
    private ArrayList<String> filteredList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        databaseHelper = new DatabaseHelper(this);
        vehicleList = new ArrayList<>();
        filteredList = new ArrayList<>();
        ListView listView = findViewById(R.id.listView);
        EditText searchEditText = findViewById(R.id.searchEditText);

        loadData();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlaka = filteredList.get(position);
                Intent intent = new Intent(list.this, details.class);
                intent.putExtra("PLAKA", selectedPlaka);
                startActivity(intent);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        filter("");
    }

    private void loadData() {
        Cursor cursor = databaseHelper.getAllData();
        if (cursor.getCount() == 0) {
            return;
        }

        vehicleList.clear();
        while (cursor.moveToNext()) {
            String plaka = cursor.getString(2); // PLAKA column
            vehicleList.add(plaka);
        }
    }

    private void filter(String text) {
        filteredList.clear();
        for (String plaka : vehicleList) {
            if (plaka.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(plaka);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        filter("");
    }
}
