package com.example.yanhejin.myapplication.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by licheetec on 2015/5/2.
 */
public class CreateSpatialDB extends SQLiteOpenHelper {
    CreateSpatialDB createSpatialDB;
    Context mcontext;

    public CreateSpatialDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mcontext=context;
    }

    public String CreateGPSData="create table GPSData("+"GPSID integer primary key autoincrement,"
            +"x REAL,"
            +"y REAL)";

    public String CreateDLData="create table DLData("+"DLID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";
    public String CreateJMDData="create table JMDData("+"JMDID integer primary key autoincrement,"
            + "LinkAID integer,"
            + "x REAL,"
            + "y REAL)";
    public String CreateSXData="create table SXData("+"SXID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";

    public String CreateGXData="create table GXData("+"GXID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";

    public String CreateZBData="create table ZBData("+"ZBID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";

    public String CreateJJXData="create table JJXData("+"JJXID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";

    public String CreateDMData="create table DMData("+"DMID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";

    public String CreateZJData="create table ZJData("+"ZJID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";

    public String CreatePZJData="create table PZJData("+"PZJID integer primary key autoincrement,"
            +"LinkAID integer,"
            +"x REAL,"
            +"y REAL)";
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateJMDData);
        db.execSQL(CreateDLData);
        db.execSQL(CreateGPSData);
        db.execSQL(CreateSXData);
        db.execSQL(CreateDMData);
        db.execSQL(CreateZBData);
        db.execSQL(CreateGXData);
        db.execSQL(CreateJJXData);
        db.execSQL(CreateZJData);
        db.execSQL(CreatePZJData);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion==1){

        }
    }
}
