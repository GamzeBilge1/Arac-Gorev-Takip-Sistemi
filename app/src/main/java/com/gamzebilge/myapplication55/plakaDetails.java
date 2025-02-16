package com.gamzebilge.myapplication55;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Environment;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class plakaDetails extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView detailsTextView;
    private String plaka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plaka_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        detailsTextView = findViewById(R.id.detailsTextView);
        plaka = getIntent().getStringExtra("plaka");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("kontroller").child(plaka);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder details = new StringBuilder("Plaka: " + plaka + "\n");
                boolean hasData = false;

                if (dataSnapshot.hasChild("tarih")) {
                    String tarih = dataSnapshot.child("tarih").getValue(String.class);
                    details.append("Tarih: ").append(tarih).append("\n");
                }
                if (dataSnapshot.hasChild("aracTuru")) { // Araç Türü bilgisini kontrol et
                    String aracTuru = dataSnapshot.child("aracTuru").getValue(String.class);
                    details.append("Araç Türü: ").append(aracTuru).append("\n");
                }
                if (dataSnapshot.hasChild("Araç Aktif mi?")) { // Araç durumu bilgisini kontrol et
                    Boolean aracAktif = dataSnapshot.child("Araç Aktif mi?").getValue(Boolean.class);
                    details.append("Araç Aktif mi: ").append(aracAktif ? "Evet" : "Hayır").append("\n");
                }

                // Diğer boolean bilgileri ekleme
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (!key.equals("Araç Aktif mi?")) { // Araç durumu kontrolünü dışarıda bırak
                        Object value = snapshot.getValue();
                        if (value instanceof Boolean && (Boolean) value) {
                            details.append(key).append(": Evet\n");
                            hasData = true;
                        }
                    }
                }

                if (!hasData) {
                    details.append("Veri bulunamadı.");
                }

                detailsTextView.setText(details.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                detailsTextView.setText("Veri alınamadı: " + databaseError.getMessage());
            }
        });
        requestStoragePermission();
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(plakaDetails.this, com.gamzebilge.myapplication55.MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void sendEmail(View view) {
        String emailBody = detailsTextView.getText().toString();

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"alici@ornek.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Araç Kontrol Detay");
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);

        try {
            startActivity(Intent.createChooser(emailIntent, "E-posta Gönder..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(plakaDetails.this, "E-posta gönderim uygulaması bulunamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    public void pdf(View view) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder details = new StringBuilder("Plaka: " + plaka + "\n");
                boolean hasData = false;

                if (dataSnapshot.hasChild("tarih")) {
                    String tarih = dataSnapshot.child("tarih").getValue(String.class);
                    details.append("Tarih: ").append(tarih).append("\n");
                }
                if (dataSnapshot.hasChild("aracTuru")) { // Araç Türü bilgisini PDF içeriğine ekle
                    String aracTuru = dataSnapshot.child("aracTuru").getValue(String.class);
                    details.append("Araç Türü: ").append(aracTuru).append("\n");
                }
                if (dataSnapshot.hasChild("Araç Aktif mi?")) { // Araç durumu bilgisini PDF içeriğine ekle
                    Boolean aracAktif = dataSnapshot.child("Araç Aktif mi?").getValue(Boolean.class);
                    details.append("Araç Aktif mi: ").append(aracAktif ? "Evet" : "Hayır").append("\n");
                }

                // Diğer boolean bilgileri ekleme
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (!key.equals("Araç Aktif mi?")) { // Araç durumu kontrolünü dışarıda bırak
                        Object value = snapshot.getValue();
                        if (value instanceof Boolean && (Boolean) value) {
                            details.append(key).append(": Evet\n");
                            hasData = true;
                        }
                    }
                }

                if (!hasData) {
                    details.append("Veri bulunamadı.");
                }

                createPdf(details.toString());
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(plakaDetails.this, "Veri alınamadı: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

   /* private void createPdf(String content) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        int y = 10;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        String[] lines = content.split("\n");
        for (String line : lines) {
            canvas.drawText(line, 10, y, paint);
            y += paint.descent() - paint.ascent();
        }

        pdfDocument.finishPage(page);

        File pdfFile = new File(getExternalFilesDir(null), "details_report.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(pdfFile));
            Toast.makeText(this, "PDF başarıyla oluşturuldu", Toast.LENGTH_LONG).show();

            openPdf(pdfFile);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "PDF oluşturulamadı.", Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }
    }
*/
    private void openPdf(File file) {
        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, "PDF görüntüleyici uygulama bulunamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    /*private void createExcelFile(String content) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Details");

        String[] lines = content.split("\n");
        int rowNum = 1;

        // Başlık satırını oluştur
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Plaka");
        headerRow.createCell(1).setCellValue("Tarih");
        headerRow.createCell(2).setCellValue("Kontrol");
        headerRow.createCell(3).setCellValue("Durum");

        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 7000);
        sheet.setColumnWidth(3, 4000);

        String plaka = lines[0].split(":")[1].trim();
        String tarih = lines[1].split(":")[1].trim();

        for (int i = 2; i < lines.length; i++) {
            String line = lines[i];
            String[] parts = line.split(":");

            if (parts.length >= 2) {
                Row row = sheet.createRow(rowNum++);

                // İlk satır için Plaka ve Tarih bilgilerini ekle
                if (i == 2) {
                    row.createCell(0).setCellValue(plaka);
                    row.createCell(1).setCellValue(tarih);
                }

                row.createCell(2).setCellValue(parts[0].trim());
                row.createCell(3).setCellValue(parts[1].trim());
            }
        }

        File excelFile = new File(getExternalFilesDir(null), "details_report.xlsx");
        try (FileOutputStream fileOut = new FileOutputStream(excelFile)) {
            workbook.write(fileOut);
            Toast.makeText(this, "Excel dosyası başarıyla oluşturuldu: " + excelFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

            openExcel(excelFile);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Excel dosyası oluşturulamadı.", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
*/ private void createPdf(String content) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        int y = 10;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        String[] lines = content.split("\n");
        for (String line : lines) {
            canvas.drawText(line, 10, y, paint);
            y += paint.descent() - paint.ascent();
        }

        pdfDocument.finishPage(page);

        // File name includes plaka
        File pdfFile = new File(getExternalFilesDir(null), plaka + ".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(pdfFile));
            Toast.makeText(this, "PDF başarıyla oluşturuldu", Toast.LENGTH_LONG).show();

            openPdf(pdfFile);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "PDF oluşturulamadı.", Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }
    }

    private void createExcelFile(String content) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Details");

        String[] lines = content.split("\n");
        int rowNum = 1;

        // Başlık satırını oluştur
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Plaka");
        headerRow.createCell(1).setCellValue("Tarih");
        headerRow.createCell(2).setCellValue("Kontrol");
        headerRow.createCell(3).setCellValue("Durum");

        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 7000);
        sheet.setColumnWidth(3, 4000);

        String plaka = lines[0].split(":")[1].trim();
        String tarih = lines[1].split(":")[1].trim();

        for (int i = 2; i < lines.length; i++) {
            String line = lines[i];
            String[] parts = line.split(":");

            if (parts.length >= 2) {
                Row row = sheet.createRow(rowNum++);

                // İlk satır için Plaka ve Tarih bilgilerini ekle
                if (i == 2) {
                    row.createCell(0).setCellValue(plaka);
                    row.createCell(1).setCellValue(tarih);
                }

                row.createCell(2).setCellValue(parts[0].trim());
                row.createCell(3).setCellValue(parts[1].trim());
            }
        }

        // File name includes plaka
        File excelFile = new File(getExternalFilesDir(null), plaka + ".xlsx");
        try (FileOutputStream fileOut = new FileOutputStream(excelFile)) {
            workbook.write(fileOut);
            Toast.makeText(this, "Excel dosyası başarıyla oluşturuldu " , Toast.LENGTH_LONG).show();

            openExcel(excelFile);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Excel dosyası oluşturulamadı", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openExcel(File file) {
        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, "Excel görüntüleyici uygulama bulunamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    public void excel(View view) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder details = new StringBuilder("Plaka: " + plaka + "\n");
                boolean hasData = false;

                if (dataSnapshot.hasChild("tarih")) {
                    String tarih = dataSnapshot.child("tarih").getValue(String.class);
                    details.append("Tarih: ").append(tarih).append("\n");
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    Object value = snapshot.getValue();
                    if (value instanceof Boolean && (Boolean) value) {
                        details.append(key).append(": Evet\n");
                        hasData = true;
                    }
                }

                if (!hasData) {
                    details.append("Veri bulunamadı.");
                }

                createExcelFile(details.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(plakaDetails.this, "Veri alınamadı: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }
    public void guncelle(View view) {
        Intent intent = new Intent(plakaDetails.this, formGuncelle.class);
        intent.putExtra("plaka", plaka);
        if (detailsTextView.getText().toString().contains("Tarih:")) {
            String tarih = detailsTextView.getText().toString().split("Tarih:")[1].split("\n")[0].trim();
            intent.putExtra("tarih", tarih);
        }
        startActivity(intent);
    }

    public void sil(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Sil")
                .setMessage("Bu plakayı silmek istediğinizden emin misiniz?")
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deletePlaka();
                    }
                })
                .setNegativeButton("Hayır", null)
                .show();

    }

    private void deletePlaka() {
        // Delete from Firebase
        mDatabase.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // Delete from dataList (if needed, adjust the path accordingly)
                    DatabaseReference dataListRef = FirebaseDatabase.getInstance().getReference().child("dataList").child(plaka);
                    dataListRef.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Toast.makeText(plakaDetails.this, "Plaka başarıyla silindi", Toast.LENGTH_SHORT).show();
                                finish(); // Close the activity after deletion
                            } else {
                                Toast.makeText(plakaDetails.this, "Plaka silinirken hata oluştu: " , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(plakaDetails.this, "Plaka silinirken hata oluştu: " , Toast.LENGTH_SHORT).show();
                }
            }
        });
        Intent intent=new Intent(plakaDetails.this,dataList.class);
        startActivity(intent);
    }
    public void detay(View view) {
        Intent intent = new Intent(plakaDetails.this, details.class);
        intent.putExtra("plaka", plaka);
        startActivity(intent);
    }
    public void liste(View view){
        Intent intent = new Intent(plakaDetails.this, dataList.class);
        startActivity(intent);

    }


}
