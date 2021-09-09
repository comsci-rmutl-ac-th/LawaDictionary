package com.example.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DbHelp<ConText> extends SQLiteOpenHelper {

    private static String dbName;
    private final Context context;
    private String dbPath=null;
    private String TABLE_NAME="LawaDictionary";
    private String COL_WORD="Word";




    @SuppressLint("SdCardPath")
    public DbHelp(Context mcontext, String name, int version){
        super(mcontext,name,null,version);

        this.context = mcontext;
        this.dbName = name;
       this.dbPath = "/data/data/"+mcontext.getPackageName()+"/"+"databases/";


    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void CheckDb(){
        SQLiteDatabase checkDb = null;

        try{
            String filePath = dbPath + dbName;

            checkDb = SQLiteDatabase.openDatabase(filePath,null,0);


        }catch (Exception e){}

        if(checkDb != null) {
            Log.d("checkDb", "Database already exists");
            checkDb.close();
        }else {
            CopyDatabase();
        }
    }

    public void CopyDatabase(){
        this.getReadableDatabase();

        try{
            InputStream is = context.getAssets().open(dbName);
            OutputStream os = new FileOutputStream(dbPath + dbName);

            byte[] buffer = new byte[1024];

            int len;
            while((len = is.read(buffer))>0){
                os.write(buffer,0,len);
            }
            os.flush();
            is.close();
            os.close();
        }catch (Exception e){e.printStackTrace();}

        Log.d("CopyDb","Database Copied");
    }

    public void OpenDatabase(){
        String filepath = dbPath + dbName;

        SQLiteDatabase.openDatabase(filepath,null,0);

    }
    //สร้างอาเรย์สำหรับเลือกคำจากตาราง
    public ArrayList<String> getWord(String query){
        ArrayList<String> wordList= new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
      //String sql = "SELECT WORD FROM LawaDictionary where Like ?";
        Cursor cursor = sqLiteDatabase.query(
                TABLE_NAME, new String[]{COL_WORD},
                COL_WORD + " LIKE ?",
                new String[]{query + "%"},
            null,null, COL_WORD);
        int index = cursor.getColumnIndex(COL_WORD);

            while (cursor.moveToNext()) {
                wordList.add(cursor.getString(index));
            }

        //sqLiteDatabase.close();
        cursor.close();
        return wordList;
    }
    public String GetDefinition(String word){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String definition = null;
        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE Word = '"+word+"'",null
        );
        while(cursor.moveToNext()){
            definition = cursor.getString(cursor.getColumnIndex("Definition"));
        }
        return definition;
    }
    public String GetSpell(String word){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String spell = null;
        Cursor curSpell = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE Word = '"+word+"'",null
        );
        while(curSpell.moveToNext()){
            spell = curSpell.getString(curSpell.getColumnIndex("Spell"));
        }
        return spell;
    }
    public String GetSpellTH(String word){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String spellTH = null;
        Cursor curSpellTH = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE Word = '"+word+"'",null
        );
        while(curSpellTH.moveToNext()){
            spellTH = curSpellTH.getString(curSpellTH.getColumnIndex("Spell_TH"));
        }
        return spellTH;
    }

}
