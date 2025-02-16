package com.gamzebilge.myapplication55;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class details extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    TextView detailsTextView;
    private String plaka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        detailsTextView = findViewById(R.id.detailsTextView);
        databaseHelper = new DatabaseHelper(this);

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
            String tarih = cursor.getString(1);
            String donusKm = cursor.getString(3);
            String cikisKm = cursor.getString(4);
            String donusSaati = cursor.getString(5);
            String cikisSaati = cursor.getString(6);
            String katedilenKm = cursor.getString(7);
            String departman = cursor.getString(8);
            String gorev = cursor.getString(9);

            String details = "Tarih: " + tarih + "\n"
                    + "Plaka: " + plaka + "\n"
                    + "Dönüş KM: " + donusKm + "\n"
                    + "Çıkış KM: " + cikisKm + "\n"
                    + "Dönüş Saati: " + donusSaati + "\n"
                    + "Çıkış Saati: " + cikisSaati + "\n"
                    + "Katedilen KM: " + katedilenKm + "\n"
                    + "Departman: " + departman + "\n"
                    + "Görev: " + gorev;

            detailsTextView.setText(details);
        } else {
            Toast.makeText(this, "Veri bulunamadı.", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }
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
            Toast.makeText(details.this, "E-posta gönderim uygulaması bulunamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    public void pdf(View view) {
        Cursor cursor = databaseHelper.getDataByPlaka(plaka);
        StringBuilder details = new StringBuilder();

        if (cursor != null && cursor.moveToFirst()) {
            String tarih = cursor.getString(1);
            String donusKm = cursor.getString(3);
            String cikisKm = cursor.getString(4);
            String donusSaati = cursor.getString(5);
            String cikisSaati = cursor.getString(6);
            String katedilenKm = cursor.getString(7);
            String departman = cursor.getString(8);
            String gorev = cursor.getString(9);

            details.append("Tarih: ").append(tarih).append("\n")
                    .append("Plaka: ").append(plaka).append("\n")
                    .append("Dönüş KM: ").append(donusKm).append("\n")
                    .append("Çıkış KM: ").append(cikisKm).append("\n")
                    .append("Dönüş Saati: ").append(donusSaati).append("\n")
                    .append("Çıkış Saati: ").append(cikisSaati).append("\n")
                    .append("Katedilen KM: ").append(katedilenKm).append("\n")
                    .append("Departman: ").append(departman).append("\n")
                    .append("Görev: ").append(gorev);
        }

        createPdf(details.toString());
    }

    private void createPdf(String content) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        int y = 25;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        String[] lines = content.split("\n");
        for (String line : lines) {
            canvas.drawText(line, 10, y, paint);
            y += paint.descent() - paint.ascent() + 10;
        }

        pdfDocument.finishPage(page);

        File pdfFile = new File(getExternalFilesDir(null), plaka+".pdf");
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

    public void sil(View view) {
        if (plaka != null) {
            boolean isDeleted = databaseHelper.deleteData(plaka);
            if (isDeleted) {
                Toast.makeText(this, "Veri başarıyla silindi.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(details.this, list.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Veri silinemedi.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Plaka bilgisi bulunamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(details.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void excel(View view) {
        Cursor cursor = databaseHelper.getDataByPlaka(plaka); // Sadece seçili plakayı getir
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "Veri bulunamadı.", Toast.LENGTH_SHORT).show();
            return;
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Vehicle Data");

        // Başlık satırını oluşturma
        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "TARIH", "PLAKA", "DONUS_KM", "CIKIS_KM", "DONUS_SAATI", "CIKIS_SAATI", "KATEDILEN_KM", "DEPARTMAN", "GOREV"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Veri satırlarını doldurma
        int rowIndex = 1;
        if (cursor.moveToFirst()) {
            Row row = sheet.createRow(rowIndex++);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(cursor.getString(i));
            }
        }


       sheet.setColumnWidth(0, 4000); // ID sütunu
        sheet.setColumnWidth(1, 6000); // TARIH sütonu
        sheet.setColumnWidth(2, 4000); // PLAKA sütonu
        sheet.setColumnWidth(3, 5000); // DONUS_KM sütonu
        sheet.setColumnWidth(4, 5000); // CIKIS_KM sütonu
        sheet.setColumnWidth(5, 6000); // DONUS_SAATI sütonu
        sheet.setColumnWidth(6, 6000); // CIKIS_SAATI sütonu
        sheet.setColumnWidth(7, 6000); // KATEDILEN_KM sütonu
        sheet.setColumnWidth(8, 6000); // DEPARTMAN sütonu
        sheet.setColumnWidth(9, 6000); // GOREV sütonu




        File excelFile = new File(getExternalFilesDir(null), plaka+".xlsx");
        try (FileOutputStream fos = new FileOutputStream(excelFile)) {
            workbook.write(fos);
            Toast.makeText(this, "Excel başarıyla oluşturuldu", Toast.LENGTH_LONG).show();

            openExcel(excelFile);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Excel oluşturulamadı.", Toast.LENGTH_SHORT).show();
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
    public void ayrintilariGuncelle(View view) {
        Intent intent = new Intent(details.this, detayGuncelle.class);
        intent.putExtra("plaka", plaka);
        startActivity(intent);
    }
    public void silll(View view) {
        boolean isDeleted = databaseHelper.deleteAllData();
        if (isDeleted) {
            Toast.makeText(this, "Tüm veriler başarıyla silindi.", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Veriler silinemedi.", Toast.LENGTH_SHORT).show();
        }
    }
    public void liste(View view){
        Intent intent = new Intent(details.this, dataList.class);
        startActivity(intent);

    }


}
