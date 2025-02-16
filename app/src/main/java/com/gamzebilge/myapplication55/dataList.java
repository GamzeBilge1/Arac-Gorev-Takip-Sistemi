package com.gamzebilge.myapplication55;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class dataList extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private ListView listView;
    private EditText searchEditText;
    private ArrayList<String> plakaList;
    private ArrayList<String> filteredPlakaList;
    private ArrayAdapter<String> adapter;
    private ImageButton otobusButton, ozelAracButton, activeButton;
    private boolean isOtobusFiltered = false, isOzelAracFiltered = false, isActiveFiltered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_data_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("kontroller");

        listView = findViewById(R.id.listView);
        searchEditText = findViewById(R.id.searchEditText);
        activeButton = findViewById(R.id.activeButton);
        otobusButton = findViewById(R.id.otobusButton);
        ozelAracButton = findViewById(R.id.ozelAracButton);

        plakaList = new ArrayList<>();
        filteredPlakaList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredPlakaList);
        listView.setAdapter(adapter);

        fetchDataFromFirebase();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPlaka = filteredPlakaList.get(position);
            Intent intent = new Intent(dataList.this, plakaDetails.class);
            intent.putExtra("plaka", selectedPlaka);
            startActivity(intent);
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        otobusButton.setOnClickListener(this::toggleOtobusFilter);
        ozelAracButton.setOnClickListener(this::toggleOzelAracFilter);
        activeButton.setOnClickListener(this::toggleActiveFilter);
    }

    private void fetchDataFromFirebase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                plakaList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    plakaList.add(snapshot.getKey());
                }
                filterList(searchEditText.getText().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(dataList.this, "Data loading cancelled: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterList(String query) {
        filteredPlakaList.clear();
        for (String plaka : plakaList) {
            if (plaka.toLowerCase().contains(query.toLowerCase())) {
                filteredPlakaList.add(plaka);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void toggleOtobusFilter(View view) {
        if (isOtobusFiltered) {
            resetAllFilters();
        } else {
            resetAllFilters();
            isOtobusFiltered = true;
            otobusButton.setSelected(true);
            filterByOtobus();
        }
    }

    private void toggleOzelAracFilter(View view) {
        if (isOzelAracFiltered) {
            resetAllFilters();
        } else {
            resetAllFilters();
            isOzelAracFiltered = true;
            ozelAracButton.setSelected(true);
            filterByOzelArac();
        }
    }

    private void toggleActiveFilter(View view) {
        if (isActiveFiltered) {
            resetAllFilters();
        } else {
            resetAllFilters();
            isActiveFiltered = true;
            activeButton.setSelected(true);
            filterByActive();
        }
    }

    private void resetAllFilters() {
        isOtobusFiltered = false;
        isOzelAracFiltered = false;
        isActiveFiltered = false;

        otobusButton.setSelected(false);
        ozelAracButton.setSelected(false);
        activeButton.setSelected(false);

        resetFilter();
    }

    private void resetFilter() {
        filteredPlakaList.clear();
        filteredPlakaList.addAll(plakaList);
        adapter.notifyDataSetChanged();
    }

    private void filterByOtobus() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filteredPlakaList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String aracTuru = snapshot.child("aracTuru").getValue(String.class);
                    if ("Otobüs".equals(aracTuru)) {
                        filteredPlakaList.add(snapshot.getKey());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(dataList.this, "Data loading cancelled: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterByOzelArac() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filteredPlakaList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String aracTuru = snapshot.child("aracTuru").getValue(String.class);
                    if ("Özel Araç".equals(aracTuru)) {
                        filteredPlakaList.add(snapshot.getKey());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(dataList.this, "Data loading cancelled: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterByActive() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filteredPlakaList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Boolean isActive = snapshot.child("Araç Aktif mi?").getValue(Boolean.class);
                    if (isActive != null && isActive) {
                        filteredPlakaList.add(snapshot.getKey());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(dataList.this, "Data loading cancelled: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
