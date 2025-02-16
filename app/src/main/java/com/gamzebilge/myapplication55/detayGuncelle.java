package com.gamzebilge.myapplication55;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class detayGuncelle extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText plakaEditText ,tarihEditText, donusKmEditText, cikisKmEditText, donusSaatiEditText, cikisSaatiEditText, katedilenKmEditText, departmanEditText, gorevEditText;
    private String plaka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detay_guncelle);
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);
        plakaEditText = findViewById(R.id.plakaEditText);
        tarihEditText = findViewById(R.id.tarihEditText);
        donusKmEditText = findViewById(R.id.DonusKmEditText);
        cikisKmEditText = findViewById(R.id.cikisKmEditText);
        donusSaatiEditText = findViewById(R.id.donusSaatEditText);
        cikisSaatiEditText = findViewById(R.id.cikisSaatEditText);
        katedilenKmEditText = findViewById(R.id.katedilenKmEditText);
        departmanEditText = findViewById(R.id.departmanEditText);
        gorevEditText = findViewById(R.id.gorevEditText);

        plaka = getIntent().getStringExtra("plaka");
        if (plaka != null) {
            loadDetails(plaka);
        } else {
            Toast.makeText(this, "Plaka bilgisi bulunamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDetails(String plaka) {
        Cursor cursor = databaseHelper.getDataByPlaka(plaka);

        if (cursor != null && cursor.moveToFirst()) {
            plakaEditText.setText(plaka);
            tarihEditText.setText(cursor.getString(1));
            donusKmEditText.setText(cursor.getString(3));
            cikisKmEditText.setText(cursor.getString(4));
            donusSaatiEditText.setText(cursor.getString(5));
            cikisSaatiEditText.setText(cursor.getString(6));
            katedilenKmEditText.setText(cursor.getString(7));
            departmanEditText.setText(cursor.getString(8));
            gorevEditText.setText(cursor.getString(9));
        }

        if (cursor != null) {
            cursor.close();
        }
    }
    public void liste(View view){
        Intent intent = new Intent(detayGuncelle.this, dataList.class);
        startActivity(intent);

    }
    public void detayGuncelle(View view) {
        String tarih = tarihEditText.getText().toString();
        String donusKm = donusKmEditText.getText().toString();
        String cikisKm = cikisKmEditText.getText().toString();
        String donusSaati = donusSaatiEditText.getText().toString();
        String cikisSaati = cikisSaatiEditText.getText().toString();
        String katedilenKm = katedilenKmEditText.getText().toString();
        String departman = departmanEditText.getText().toString();
        String gorev = gorevEditText.getText().toString();

        boolean isUpdated = databaseHelper.updateData(plaka, tarih, donusKm, cikisKm, donusSaati, cikisSaati, katedilenKm, departman, gorev);

        if (isUpdated) {
            Toast.makeText(this, "Ayrıntılar güncellendi.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ayrıntılar güncellenemedi.", Toast.LENGTH_SHORT).show();
        }
    }
}
