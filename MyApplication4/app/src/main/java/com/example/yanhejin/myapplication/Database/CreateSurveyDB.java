package com.example.yanhejin.myapplication.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by licheetec on 2015/5/2.
 */
public class CreateSurveyDB extends SQLiteOpenHelper {

    Context mcontext;


    public CreateSurveyDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name,factory, version);
        mcontext=context;
    }


    public static final String CreateUserDB="create table user("+"ID integer primary key autoincrement,"
            +"UserName varchar(50),"
            +"PassWord varchar(50),"
            +"Permission integer,"
            +"LogTime varchar(50))";

    public static final String CreatePhotoDB="create table PhotoData("+"PhotoID integer primary key autoincrement,"
            +"photoname varchar(50),"
            +"photodescrible varchar(100),"
            +"phototime varchar(50))";

    public static final String CreateSurveyDB="create table surveydk("+"FID integer primary key,"
            +"BSM integer,"
            +"YSDM varchar(50),"
            +"YSBM varchar(50),"
            +"DKMC varchar(50),"
            +"SYQXZ varchar(50),"
            +"DKLB varchar(50),"
            +"TDSYLX varchar(50),"
            +"DLDJ varchar(50),"
            +"TDYT varchar(50),"
            +"SFJBNT varchar(50),"
            +"SCMJ REAL,"
            +"DKDZ varchar(50),"
            +"DKXZ varchar(50),"
            +"DKNZ varchar(50),"
            +"DKBZ varchar(50),"
            +"DKBZXX varchar(50),"
            +"ZJRXM varchar(50))";

    public static final String CreateDKDataDB="create table DKData("+"pointID integer,"
            +"x REAL,"
            +"y REAL,"
            +"graphicID integer,"
            +"DKDataID integer primary key(pointID,graphicID))";



    public static final String CreateJMDDB="create table JMDData("+"ID integer primary key autoincrement,"
            +"LinkID integer,"
            +"FWCS integer,"
            +"FWCZ varchar(50),"
            +"FYGZ REAL,"
            +"BZ varchar(100))";

    public  static final String CreateDLDB="create table DLData("+"ID integer primary key autoincrement,"
            +"LinkSID integer,"
            +"DLMC varchar(50),"
            +"DLXH integer,"
            +"DJDM varchar(50),"
            +"BZ varchar(100))";



    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CreateUserDB);
        db.execSQL(CreateSurveyDB);
        //db.execSQL(CreateDKDataDB);
        db.execSQL(CreatePhotoDB);
        db.execSQL(CreateJMDDB);
        db.execSQL(CreateDLDB);
        Toast.makeText(mcontext, "create success!", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user");
        db.execSQL("drop table if exists surveydk");
        db.execSQL("drop table if exists DKData");
        db.execSQL("drop table if exists GPSData");
        db.execSQL("drop table if exists PhotoData");
        onCreate(db);
    }
}
