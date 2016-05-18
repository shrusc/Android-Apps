package edu.sdsu.cs.shruti.assignment3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLiteDB {

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_OFFICE ="office";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_AVG_RATING = "averageRating";
    public static final String KEY_TOTAL_RATING = "totalRating";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_DATE = "date";
    public static final String KEY_INSTRUCTOR_ID = "instructorId";

    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "SQLiteDBInstructors.db";
    private static final int DATABASE_VERSION = 8;
    private static final String TABLE_INSTRUCTORS = "allInstructors";
    private static final String TABLE_DETAILS = "allInstructorDetails";
    private static final String TABLE_COMMENTS ="allInstructorComments";
    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public SQLiteDB(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_INSTRUCTORS_TABLE = "CREATE TABLE " + TABLE_INSTRUCTORS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT" + ")";
            db.execSQL(CREATE_INSTRUCTORS_TABLE);

            String CREATE_INSTRUCTOR_DETAILS_TABLE = "CREATE TABLE " + TABLE_DETAILS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY," + KEY_INSTRUCTOR_ID +" TEXT," + KEY_NAME + " TEXT," + KEY_OFFICE +
                    " TEXT," + KEY_PHONE + " TEXT," + KEY_EMAIL + " TEXT," + KEY_AVG_RATING + " TEXT,"
                    + KEY_TOTAL_RATING + " TEXT" + ")";
            db.execSQL(CREATE_INSTRUCTOR_DETAILS_TABLE);

            String CREATE_INSTRUCTOR_COMMENTS_TABLE = "CREATE TABLE " + TABLE_COMMENTS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY," + KEY_INSTRUCTOR_ID + " TEXT," + KEY_COMMENT + " TEXT," + KEY_DATE + " TEXT" +")";
            db.execSQL(CREATE_INSTRUCTOR_COMMENTS_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS allInstructors");
            db.execSQL("DROP TABLE IF EXISTS allInstructorDetails");
            db.execSQL("DROP TABLE IF EXISTS allInstructorComments");
            onCreate(db);
        }
    }

    //---open SQLite DB---
    public SQLiteDB open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---close SQLite DB---
    public void close() {
        DBHelper.close();
    }

    //---insert data into SQLite DB---
    public void insertInstructor(String id, String name) {
        open();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ID, id);
        initialValues.put(KEY_NAME, name);
        db.insert(TABLE_INSTRUCTORS, null, initialValues);
        close();
    }

    public void insertInstructorDetails(String id, String name, String office, String phone,
                                        String email, String avgRating, String totalRating) {
        open();
        ContentValues values = new ContentValues();
        values.put(KEY_INSTRUCTOR_ID, id);
        values.put(KEY_NAME, name);
        values.put(KEY_OFFICE, office);
        values.put(KEY_PHONE, phone);
        values.put(KEY_EMAIL, email);
        values.put(KEY_AVG_RATING, avgRating);
        values.put(KEY_TOTAL_RATING,totalRating);
        db.insert(TABLE_DETAILS, null, values);
        close();
    }

    public void insertInstructorComments(String id, String comment, String date) {
        open();
        ContentValues values = new ContentValues();
        values.put(KEY_INSTRUCTOR_ID, id);
        values.put(KEY_COMMENT, comment);
        values.put(KEY_DATE, date);
        db.insert(TABLE_COMMENTS, null, values);
        close();
    }

    //---Delete All Data from table in SQLite DB---
    public void deleteAll() {
        open();
        db.delete(TABLE_INSTRUCTORS, null, null);
        close();
    }

    public void deleteAllDetails() {
        open();
        db.delete(TABLE_DETAILS, null, null);
        close();
    }

    public void deleteInstructorDetails(String id) {
        open();
        db.delete(TABLE_DETAILS, KEY_INSTRUCTOR_ID +" = ?", new String[] {id});
        close();
    }

    public void deleteInstructorComments(String id) {
        open();
        db.delete(TABLE_COMMENTS, KEY_INSTRUCTOR_ID +" = ?", new String[] {id});
        close();
    }

    public void deleteAllComments() {
        open();
        db.delete(TABLE_COMMENTS, null, null);
        close();
    }

    // Getting All Instructors
    public List<HashMap<String,String>> getAllInstructors() {
        List<HashMap<String,String>> instructorList = new ArrayList<HashMap<String,String>>();
        open();
        String selectQuery = "SELECT  * FROM " + TABLE_INSTRUCTORS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> instructor = new HashMap<String, String>();
                    instructor.put("id", cursor.getString(0));
                    instructor.put("fullName", cursor.getString(1));
                    instructorList.add(instructor);
                } while (cursor.moveToNext());
            }
        }
        close();
        return instructorList;
    }

    public List<HashMap<String,String>> getAllInstructorDetails(String id) {
        List<HashMap<String,String>> instructorDetailList = new ArrayList<HashMap<String,String>>();
        open();
        String selectQuery = "SELECT  * FROM " + TABLE_DETAILS + " WHERE "+ KEY_INSTRUCTOR_ID + " = " + id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> instructorName = new HashMap<String, String>(2);
                    instructorName.put("title", "Name");
                    instructorName.put("subtitle", cursor.getString(2));
                    instructorDetailList.add(instructorName);
                    HashMap<String, String> instructorOffice = new HashMap<String, String>(2);
                    instructorOffice.put("title", "Office");
                    instructorOffice.put("subtitle", cursor.getString(3));
                    instructorDetailList.add(instructorOffice);
                    HashMap<String, String> instructorPhone = new HashMap<String, String>(2);
                    instructorPhone.put("title", "Phone");
                    instructorPhone.put("subtitle", cursor.getString(4));
                    instructorDetailList.add(instructorPhone);
                    HashMap<String, String> instructorEmail = new HashMap<String, String>(2);
                    instructorEmail.put("title", "Email");
                    instructorEmail.put("subtitle", cursor.getString(5));
                    instructorDetailList.add(instructorEmail);
                    HashMap<String, String> instructorAvgRating = new HashMap<String, String>(2);
                    instructorAvgRating.put("title", "Average Rating");
                    instructorAvgRating.put("subtitle", cursor.getString(6));
                    instructorDetailList.add(instructorAvgRating);
                    HashMap<String, String> instructorTotalRating = new HashMap<String, String>(2);
                    instructorTotalRating.put("title", "Total Rating");
                    instructorTotalRating.put("subtitle", cursor.getString(7));
                    instructorDetailList.add(instructorTotalRating);

                } while (cursor.moveToNext());
            }
        }
        close();
        return instructorDetailList;
    }

    public List<HashMap<String,String>> getAllInstructorComments(String id) {
        List<HashMap<String,String>> instructorCommentsList = new ArrayList<HashMap<String,String>>();
        open();
        String selectQuery = "SELECT  * FROM " + TABLE_COMMENTS + " WHERE " + KEY_INSTRUCTOR_ID + " = " + id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> instructorComments = new HashMap<String, String>(2);
                    instructorComments.put("title", cursor.getString(2));
                    instructorComments.put("subtitle", cursor.getString(3));
                    instructorCommentsList.add(instructorComments);
                } while (cursor.moveToNext());
            }
        }
        close();
        return instructorCommentsList;
    }
}