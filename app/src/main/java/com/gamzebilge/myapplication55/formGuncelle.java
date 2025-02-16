package com.gamzebilge.myapplication55;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.util.ArrayList;
import java.util.Calendar;

public class formGuncelle extends AppCompatActivity {
    private DatabaseReference mDatabase;
    EditText plakaNoText, tarihText;
    ArrayList<CheckBox> checkBoxList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_guncelle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();

        tarihText = findViewById(R.id.tarihEditText);
        plakaNoText = findViewById(R.id.plakaEditText);

        Intent intent = getIntent();
        String plaka = intent.getStringExtra("plaka");

        if (plaka != null) {
            plakaNoText.setText(plaka);
            loadData(plaka);
        }

        int[] checkboxIds = {
                R.id.temizlikCheckbox, R.id.frenCheckbox, R.id.motorCheckBox, R.id.hasarCheckbox,
                R.id.makasCheckBox, R.id.sesKokuCheckBox, R.id.sizintiCheckBox, R.id.tekerlekCheckBox,
                R.id.havaDeposuCheckBox, R.id.yakitCheckBox, R.id.lambaCheckBox, R.id.akuCheckBox,
                R.id.motorIsiCheckBox, R.id.kornoCheckBox, R.id.vizeCheckBox, R.id.gostergeCheckBox,
                R.id.karoseriCheckBox, R.id.sigortaCheckBox, R.id.emniyetCheckBox, R.id.cekmeTertibatiCheckBox,
                R.id.yanginCheckBox, R.id.avadanlikCheckBox, R.id.evrakCheckBox, R.id.ilkYardimCheckBox
        };

        for (int id : checkboxIds) {
            checkBoxList.add(findViewById(id));
        }
    }

    private void loadData(String plaka) {
        DatabaseReference userRef = mDatabase.child("kontroller").child(plaka);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    tarihText.setText(snapshot.child("tarih").getValue(String.class));
                    String[] checkBoxKeys = {
                            "Temizlik", "Frenler", "Motorun Çalışması", "Hasar Kurcalama",
                            "Makas ve Amortisörler", "Sesler ve Kokular", "Sızıntılar(Genel)", "Tekerlek ve Lastikler",
                            "Hava Deposu Suyunu Akıt", "Yakıt,Yağ,Su", "Lambalar ve Kedi Gözleri", "Akü Suyu Kontrolü",
                            "Motorun Isıtılması", "Korno ve Cam Silecekleri", "Vize Süresi Kontrol", "Göstergeler",
                            "Karoseri ve Teferruatı", "Sigorta Süresi", "Emniyet Cihazları", "Çekme Tertiabtı",
                            "Yangın Söndürme Cihazı", "Avadanlık Takım", "Evraklar", "İlk Yardım"
                    };
                    for (int i = 0; i < checkBoxList.size(); i++) {
                        CheckBox checkBox = checkBoxList.get(i);
                        checkBox.setChecked(snapshot.child(checkBoxKeys[i]).getValue(Boolean.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(formGuncelle.this, "Veri yüklenemedi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void aracturu(View view) {
        final String[] vehicleTypes = {"Otobüs", "Özel Araç", };
        final int[] selectedVehicleTypeIndex = {0};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Araç Türü ve Durumunu Seçin");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_vehicle_type_condition, null);
        builder.setView(dialogView);

        RadioGroup vehicleTypeGroup = dialogView.findViewById(R.id.vehicleTypeGroup);
        for (int i = 0; i < vehicleTypes.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(vehicleTypes[i]);
            radioButton.setId(i);
            vehicleTypeGroup.addView(radioButton);
        }
        vehicleTypeGroup.check(selectedVehicleTypeIndex[0]);

        RadioGroup vehicleConditionGroup = dialogView.findViewById(R.id.vehicleConditionGroup);
        RadioButton activeRadioButton = dialogView.findViewById(R.id.activeRadioButton);
        RadioButton inactiveRadioButton = dialogView.findViewById(R.id.inactiveRadioButton);

        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedVehicleTypeIndex[0] = vehicleTypeGroup.getCheckedRadioButtonId();
                String selectedVehicleType = vehicleTypes[selectedVehicleTypeIndex[0]];

                int selectedConditionId = vehicleConditionGroup.getCheckedRadioButtonId();
                boolean isVehicleActive = selectedConditionId == R.id.activeRadioButton;

                Log.d("Araç Türü", "Seçilen Araç Türü: " + selectedVehicleType);
                Log.d("Araç Durumu", "Araç Aktif mi: " + isVehicleActive);

                DatabaseReference userRef = mDatabase.child("kontroller").child(plakaNoText.getText().toString());
                userRef.child("aracTuru").setValue(selectedVehicleType);
                userRef.child("Araç Aktif mi?").setValue(isVehicleActive);
            }
        });

        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void liste(View view){
        Intent intent = new Intent(formGuncelle.this, dataList.class);
        startActivity(intent);

    }


    public void guncelle(View view) {
        String plaka = plakaNoText.getText().toString();
        String tarih = tarihText.getText().toString();

        if (plaka.isEmpty() || tarih.isEmpty()) {
            Toast.makeText(this, "'Plaka No' ve 'Tarih' girmek zorunludur!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = mDatabase.child("kontroller").child(plaka);
        userRef.child("tarih").setValue(tarih);

        String[] checkBoxKeys = {
                "Temizlik", "Frenler", "Motorun Çalışması", "Hasar Kurcalama",
                "Makas ve Amortisörler", "Sesler ve Kokular", "Sızıntılar(Genel)", "Tekerlek ve Lastikler",
                "Hava Deposu Suyunu Akıt", "Yakıt,Yağ,Su", "Lambalar ve Kedi Gözleri", "Akü Suyu Kontrolü",
                "Motorun Isıtılması", "Korno ve Cam Silecekleri", "Vize Süresi Kontrol", "Göstergeler",
                "Karoseri ve Teferruatı", "Sigorta Süresi", "Emniyet Cihazları", "Çekme Tertiabtı",
                "Yangın Söndürme Cihazı", "Avadanlık Takım", "Evraklar", "İlk Yardım"
        };

        for (int i = 0; i < checkBoxList.size(); i++) {
            CheckBox checkBox = checkBoxList.get(i);
            userRef.child(checkBoxKeys[i]).setValue(checkBox.isChecked());
        }

        Toast.makeText(this, "Kontroller güncellendi", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(formGuncelle.this, plakaDetails.class);
        intent.putExtra("plaka",plaka);
        startActivity(intent);
    }
}
