/**
 * Gretar Ævarsson
 * gretar80@gmail.com
 * © 2016
 */

package com.example.s198586.minvin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {
    // tabell navn
    private static final String TABELLNAVN = "Vintabell";

    // navn på kolonner i database-tabellen
    private static final String KEY_ID = "_ID";
    private static final String KEY_NAVN = "Navn";
    private static final String KEY_POENG = "Poeng";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_LAND = "Land";
    private static final String KEY_ALKOHOL = "Alkohol";
    private static final String KEY_ARGANG = "Argang";
    private static final String KEY_PRIS = "Pris";
    private static final String KEY_NOTATER = "Notater";
    private static final String KEY_FIGUR = "Figur";

    static int DATABASE_VERSION = 1;
    static String DATABASE_NAVN = "DB_VINLIST";

    // konstruktører
    public DB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DB(Context context){
        super(context, DATABASE_NAVN, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // lag tabell
        String sql = "CREATE TABLE " + TABELLNAVN + " (" + KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_NAVN + " TEXT, " + KEY_POENG + " REAL, " + KEY_TYPE + " TEXT, " +
                KEY_LAND + " TEXT, " + KEY_ALKOHOL + " REAL, " + KEY_ARGANG + " INTEGER, " +
                KEY_PRIS + " REAL, " + KEY_NOTATER + " TEXT, " + KEY_FIGUR + " BLOB)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL("DROP TABLE IF EXISTS " + TABELLNAVN);
            onCreate(db);
        }
        catch (Exception e){
            Log.d("DATABASE", "onUpgrade feil: " + e.toString());
        }
    }

    // legg til nytt vin
    public boolean leggTilVin(Vin vin){
        try{
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues cv = new  ContentValues();
            cv.put(KEY_NAVN, vin.getNavn());
            cv.put(KEY_POENG, vin.getPoeng());
            cv.put(KEY_TYPE, vin.getType());
            cv.put(KEY_LAND, vin.getLand());
            cv.put(KEY_ALKOHOL, vin.getAlkohol());
            cv.put(KEY_ARGANG, vin.getArgang());
            cv.put(KEY_PRIS, vin.getPris());
            cv.put(KEY_NOTATER, vin.getNotater());
            cv.put(KEY_FIGUR, vin.getFigur());

            db.insert( TABELLNAVN, null, cv );
            db.close();
            return true;
        }
        catch (Exception e){
            Log.d("DATABASE", "leggTilVin feil: " + e.toString());
            return false;
        }
    }

    // list alle rødvin
    public ArrayList<Vin> listAlleVin(String type){
        // vinliste
        ArrayList<Vin> vinListe = new ArrayList<>();
        String sql = "SELECT * FROM " + TABELLNAVN + " WHERE Type = '" + type + "'";
        Log.d("DB", "listAlleVin, sql: " + sql);
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if(cursor.moveToFirst()){
                do{
                    Vin vin = new Vin();
                    vin.set_ID(cursor.getInt(0));
                    vin.setNavn(cursor.getString(1));
                    vin.setPoeng(cursor.getDouble(2));
                    vin.setType(cursor.getString(3));
                    vin.setLand(cursor.getString(4));
                    vin.setAlkohol(cursor.getDouble(5));
                    vin.setArgang(cursor.getInt(6));
                    vin.setPris(cursor.getDouble(7));
                    vin.setNotater(cursor.getString(8));
                    vin.setFigur(cursor.getBlob(9));
                    vinListe.add(vin);
                }while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            Log.d("DB", "listAlleVin, antall: " + vinListe.size());
            return vinListe;
        }
        catch (Exception e){
            Log.d("DATABASE", "listAlleVin feil: " + e.toString());
            return null;
        }
    }

    // endre vin
    public int endreVin(Vin vin){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_NAVN, vin.getNavn());
            values.put(KEY_POENG, vin.getPoeng());
            values.put(KEY_TYPE, vin.getType());
            values.put(KEY_LAND, vin.getLand());
            values.put(KEY_ALKOHOL, vin.getAlkohol());
            values.put(KEY_ARGANG, vin.getArgang());
            values.put(KEY_PRIS, vin.getPris());
            values.put(KEY_NOTATER, vin.getNotater());
            values.put(KEY_FIGUR, vin.getFigur());

            int endret = db.update(TABELLNAVN, values, KEY_ID + "=?", new String[]{String.valueOf(vin.get_ID())});
            db.close();
            return endret;
        }
        catch (Exception e){
            Log.d("DATABASE", "endreVin feil: " + e.toString());
            return -1;
        }
    }

    // slette vin
    public boolean slettVin(Vin vin){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABELLNAVN, KEY_ID + "=?", new String[]{String.valueOf(vin.get_ID())});
            db.close();
            return true;
        }
        catch (Exception e){
            Log.d("DATABASE", "slettVin feil: " + e.toString());
            return false;
        }
    }

    // finn vin
    public Vin finnVin(int id){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABELLNAVN, new String[]{KEY_ID, KEY_NAVN, KEY_POENG,
                    KEY_TYPE, KEY_LAND, KEY_ALKOHOL, KEY_ARGANG, KEY_PRIS, KEY_NOTATER, KEY_FIGUR},
                    KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

            if(cursor != null)
                cursor.moveToFirst();

            Vin vin = new Vin(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getDouble(5),
                    cursor.getInt(6),
                    cursor.getDouble(7),
                    cursor.getString(8),
                    cursor.getBlob(9)
            );

            cursor.close();
            db.close();
            return vin;
        }
        catch (Exception e){
            Log.d("DATABASE", "finnVin feil: " + e.toString());
            return null;
        }
    }
}
