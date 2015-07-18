package com.robotemplates.cookbook.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Movie;
import android.util.Log;

import com.robotemplates.cookbook.logging.L;
import com.robotemplates.cookbook.pojo.SubReddit;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by willy on 17/07/15.
*/
public class DBApp {
    public static final int BOX_OFFICE = 0;
    public static final int UPCOMING = 1;
    private AppHelper mHelper;
    private SQLiteDatabase mDatabase;

    public DBApp(Context context) {
        Log.d("before   app helper", "Inside the shite ( context )");
        mHelper = new AppHelper(context);
        mDatabase = mHelper.getWritableDatabase();

        Log.d("after creatig app hlper", "Heeylloooo, we have to generate DBApp.. new DBApp( context )");

        L.m(mDatabase.toString());
    }

    public void insertSubReddits(int table, ArrayList<SubReddit> listSubReddits, boolean clearPrevious) {
//        if (clearPrevious) {
//            deleteMovies(table);
//        }

        L.m("before inserting entries loop. Entry to be inserted: " + listSubReddits.size() + new Date(System.currentTimeMillis()));


        //create a sql prepared statement
        String sql = "INSERT INTO " +  AppHelper.TABLE_SUB_REDDIT + " VALUES (?,?,?);";
        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listSubReddits.size(); i++) {
            SubReddit currentSubReddit = listSubReddits.get(i);
            statement.clearBindings();
            //for a given column index, simply bind the data to be put inside that index
            statement.bindLong(1, currentSubReddit.getId());
            statement.bindString(2, currentSubReddit.getName());
            statement.bindString(3, currentSubReddit.getUrlImage());

            statement.execute();
        }
        //set the transaction as successful and end the transaction
        L.m("inserting entries " + listSubReddits.size() + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }


    public ArrayList<SubReddit> readMovies(int table) {
        ArrayList<SubReddit> listSubReddits = new ArrayList<>();

        //get a list of columns to be retrieved, we need all of them
        String[] columns = {
                AppHelper.COLUMN_SERVER_ID,
                AppHelper.COLUMN_NAME,
                AppHelper.COLUMN_URL_IMAGE
        };
        Cursor cursor = mDatabase.query(AppHelper.TABLE_SUB_REDDIT, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            L.m("loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new movie object and retrieve the data from the cursor to be stored in this movie object
                SubReddit subReddit = new SubReddit();
                //each step is a 2 part process, find the index of the column first, find the data of that column using
                //that index and finally set our blank movie object to contain our data
                subReddit.setId(cursor.getLong(cursor.getColumnIndex(AppHelper.COLUMN_SERVER_ID)));
                subReddit.setName(cursor.getString(cursor.getColumnIndex(AppHelper.COLUMN_NAME)));
                subReddit.setUrlImage(cursor.getString(cursor.getColumnIndex(AppHelper.COLUMN_URL_IMAGE)));

                listSubReddits.add(subReddit);
            }
            while (cursor.moveToNext());
        }
        return listSubReddits;
    }

    public void deleteMovies(int table) {
        mDatabase.delete( AppHelper.TABLE_SUB_REDDIT, null, null);
    }

    private static class AppHelper extends SQLiteOpenHelper {
        public static final String TABLE_SUB_REDDIT = " nsfw_sub_reddit";
        public static final String TABLE_IMAGE = "nsfw_image";
        public static final String COLUMN_UID = "_id";

        public static final String COLUMN_SERVER_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_URL_IMAGE = "url_image";
        public static final String COLUMN_IMAGE_URL = "url";

        private static final String CREATE_TABLE_SUB_REDDIT = "CREATE TABLE " + TABLE_SUB_REDDIT + " (" +
                COLUMN_SERVER_ID + " INTEGER  ," +
                COLUMN_NAME + " TEXT," +
                COLUMN_URL_IMAGE + " TEXT " +
                ");";

        private static final String DB_NAME = "nsfw_m_db";
        private static final int DB_VERSION = 5;
        private Context mContext;

        public AppHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_SUB_REDDIT);
                Log.d("onCreate in DBApp: ", " NULL");
            } catch (SQLiteException exception) {
                L.t(mContext, exception + "");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                L.m("upgrade table box office executed");
                Log.d("onUpgrade in DBApp: ", " NULL");
                db.execSQL(" DROP TABLE " + CREATE_TABLE_SUB_REDDIT + " IF EXISTS;");
                onCreate(db);
            } catch (SQLiteException exception) {
                L.t(mContext, exception + "");
            }
        }
    }
}

