package com.fstravassos.sirast.slaver.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fstravassos.sirast.slaver.models.Master;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by felip_000 on 14/02/2017.
 */

public class SlaverDataBase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "slaverDb";
    private static final String TABLE = "master";
    private static final String ID = "ID";
    private static final String NAME = "NAME";
    private static final String NUMBER = "NUMBER";
    private static Context mContext;

    private static final String[] COLUMNS = {ID, NAME, NUMBER};

    public SlaverDataBase (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    public void addMaster(Master item){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMNS[1], item.getName());
        values.put(COLUMNS[2], item.getNumber());

        long result = db.insert(TABLE, null, values);
        if (result == -1) {
            Log.i("add", "ERROR :(");
        } else {
            Log.i("add", "SUCESSS");
        }

        db.close();
    }

    public Master getMaster(String number) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE,
                COLUMNS, " NUMBER = ?", new String[] { String.valueOf(number) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        else return null;
        try {
            Master item = new Master();
            item.setId(Integer.parseInt(cursor.getString(0)));
            item.setName(cursor.getString(1));
            item.setNumber(cursor.getString(2));
            return item;
        }catch (CursorIndexOutOfBoundsException e) {

        }
        return null;
    }

    public Master getMaster(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE,
                COLUMNS, " id = ?", new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Master item = new Master();
        item.setId(Integer.parseInt(cursor.getString(0)));
        item.setName(cursor.getString(1));
        item.setNumber(cursor.getString(2));

        return item;
    }

    public List getAllMasters() {
        List<Master> items = new ArrayList<>();

        String query = "SELECT  * FROM " + TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Master item = null;
        if (cursor.moveToFirst()) {
            do {
                item = new Master();
                item.setId(Integer.parseInt(cursor.getString(0)));
                item.setName(cursor.getString(1));
                item.setNumber(cursor.getString(2));

                items.add(item);
            } while (cursor.moveToNext());
        }
        return items;
    }

    public void deleteAll() {
        List<Master> items = getAllMasters();
        List<String> list = new ArrayList();
        for (Master element : items) {
            list.add(element.getId() + "");
        }

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE, ID + " = ?", (String[]) list.toArray());
        db.close();
    }

    public void deleteMaster(Master history) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE, ID + " = ?", new String[] { String.valueOf(history.getId()) });
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOK_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT, "
                + NUMBER + " TEXT)";
        db.execSQL(CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        this.onCreate(db);
    }

}
