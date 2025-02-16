package com.gamzebilge.myapplication55;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class form extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseHelper databaseHelper; // SQLite için DatabaseHelper örneği

    EditText plakaNoText, tarihText, editDonusKm, editCikisKm, editDonusSaati, editCikisSaati, editKatedilenKm, editDepartman, editGorev;

    String userName;
    ArrayList<CheckBox> checkBoxList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        databaseHelper = new DatabaseHelper(this);

        tarihText = findViewById(R.id.tarihEditText);
        plakaNoText = findViewById(R.id.plakaEditText);
        editDonusKm=findViewById(R.id.DonusKmEditText);
        editCikisKm=findViewById(R.id.cikisKmEditText);
        editDonusSaati=findViewById(R.id.donusSaatEditText);
        editCikisSaati=findViewById(R.id.cikisSaatEditText);
        editKatedilenKm=findViewById(R.id.katedilenKmEditText);
        editDepartman=findViewById(R.id.departmanEditText);
        editGorev=findViewById(R.id.gorevEditText);


        tarihText.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }

                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        if (mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    tarihText.setText(current);
                    tarihText.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

    public void kaydet(View view) {
        String plaka = plakaNoText.getText().toString();
        String tarih = tarihText.getText().toString();

        String donusKm = editDonusKm.getText().toString();
        String cikisKm = editCikisKm.getText().toString();
        String donusSaati = editDonusSaati.getText().toString();
        String cikisSaati = editCikisSaati.getText().toString();
        String katedilenKm = editKatedilenKm.getText().toString();
        String departman = editDepartman.getText().toString();
        String gorev = editGorev.getText().toString();

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


        boolean isInserted = databaseHelper.insertData(tarih, plaka, donusKm, cikisKm, donusSaati, cikisSaati, katedilenKm, departman, gorev);

      /*  if (isInserted) {
            Toast.makeText(this, "Veriler başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Veri kaydedilemedi", Toast.LENGTH_SHORT).show();
        }*/


        Intent intent = new Intent(form.this, dataList.class);
        startActivity(intent);
    }

    public void liste(View view) {
        Intent intent = new Intent(form.this, dataList.class);
        startActivity(intent);

    }


    public void aracturu(View view) {
        final String[] vehicleTypes = {"Otobüs", "Özel Araç"};
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


}
