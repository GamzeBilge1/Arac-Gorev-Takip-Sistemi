package com.gamzebilge.myapplication55;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "VehicleForm.db";
    public static final String TABLE_NAME = "form_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "TARIH";
    public static final String COL_3 = "PLAKA";
    public static final String COL_4 = "DONUS_KM";
    public static final String COL_5 = "CIKIS_KM";
    public static final String COL_6 = "DONUS_SAATI";
    public static final String COL_7 = "CIKIS_SAATI";
    public static final String COL_8 = "KATEDILEN_KM";
    public static final String COL_9 = "DEPARTMAN";
    public static final String COL_10 = "GOREV";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TARIH TEXT, PLAKA TEXT, DONUS_KM TEXT, CIKIS_KM TEXT, DONUS_SAATI TEXT, CIKIS_SAATI TEXT, KATEDILEN_KM TEXT, DEPARTMAN TEXT, GOREV TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("ALTER TABLE mytable ADD COLUMN new_column TEXT");
        onCreate(db);

    }

    public boolean insertData(String tarih, String plaka, String donusKm, String cikisKm, String donusSaati, String cikisSaati, String katedilenKm, String departman, String gorev) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, tarih);
        contentValues.put(COL_3, plaka);
        contentValues.put(COL_4, donusKm);
        contentValues.put(COL_5, cikisKm);
        contentValues.put(COL_6, donusSaati);
        contentValues.put(COL_7, cikisSaati);
        contentValues.put(COL_8, katedilenKm);
        contentValues.put(COL_9, departman);
        contentValues.put(COL_10, gorev);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
    public Cursor getDataByPlaka(String plaka) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE PLAKA = ?", new String[]{plaka});
    }
    public boolean deleteData(String plaka) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, "PLAKA = ?", new String[]{plaka});
        return result > 0;
    }
    public boolean deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, "1", null);
        return result > 0;
    }

    public boolean updateData(String plaka, String tarih, String donusKm, String cikisKm, String donusSaati, String cikisSaati, String katedilenKm, String departman, String gorev) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, tarih);
        contentValues.put(COL_4, donusKm);
        contentValues.put(COL_5, cikisKm);
        contentValues.put(COL_6, donusSaati);
        contentValues.put(COL_7, cikisSaati);
        contentValues.put(COL_8, katedilenKm);
        contentValues.put(COL_9, departman);
        contentValues.put(COL_10, gorev);
        db.update(TABLE_NAME, contentValues, "PLAKA = ?", new String[]{plaka});
        return true;
    }


}
