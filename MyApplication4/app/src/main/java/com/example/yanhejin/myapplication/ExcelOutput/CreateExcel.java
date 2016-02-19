package com.example.yanhejin.myapplication.ExcelOutput;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.example.yanhejin.myapplication.Database.CreateSpatialDB;
import com.example.yanhejin.myapplication.Database.CreateSurveyDB;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by licheetec on 2015/5/2.
 */
public class CreateExcel extends Activity {

    String dbpath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ArcGISSurvey";
    String attributedb = "AttributeSurveyDB.db";
    String spatialdb = "SpatialSurveyDB.db";
    CreateSurveyDB createSurveyDB;
    CreateSpatialDB createSpatialDB;
   /* SQLiteDatabase attribute = SQLiteDatabase.openOrCreateDatabase(dbpath + "/" + attributedb, null);
    SQLiteDatabase spatial = SQLiteDatabase.openOrCreateDatabase(dbpath + "/" + spatialdb, null);*/

    CreateSurveyDB dataattdb =new CreateSurveyDB(this,dbpath + "/" + attributedb, null,2);
    CreateSurveyDB dataspatdb =new CreateSurveyDB(this,dbpath + "/" + spatialdb, null,2);
    SQLiteDatabase attribute=dataattdb.getReadableDatabase();
    SQLiteDatabase spatial=dataspatdb.getReadableDatabase();
    WritableSheet sheet;
    WritableWorkbook workbook;
    String[] title = {"居民地", "道路", "水系", "土质地貌", "管线和电力线", "境界线"};
    String excelPath;
    File excelFile;
   /* public CreateExcel(String name) throws IOException, WriteException {
        WriteToAttributeDLExcel(name);
    }*/

    public String getExcelDir() {
        // SD卡指定文件夹
        String sdcardPath = Environment.getExternalStorageDirectory().toString();
        File dir = new File(sdcardPath + File.separator + "ArcGISSurvey");
        if (dir.exists()) {
            return dir.toString();

        } else {
            dir.mkdirs();
            Log.d("BAG", "保存路径不存在,");
            return dir.toString();
        }
    }

    public void createExcel(String name) {
        try {
            String filepath = android.os.Environment.getDataDirectory().getAbsolutePath() + "/" + "ArcGISSurvey";
            File file = new File(filepath, "attributedb.xls");
            if (file.exists()) {
                file.createNewFile();
            }
            excelPath = getExcelDir()+File.separator+name+".xls";
            excelFile = new File(excelPath);
            if (excelFile.exists()){
                excelPath=getExcelDir()+File.separator+name+"(1).xls";
                excelFile = new File(excelPath);
            }
            workbook = Workbook.createWorkbook(file);
            sheet = workbook.createSheet("居民地", 0);
            sheet = workbook.createSheet("道路", 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToAttributeJMDExcel() throws IOException, WriteException {

        WritableWorkbook workbook = null;
        int ID;
        String FTName;
        int LinkID;
        int FWCS;
        String FWCZ;
        double FYGZ;
        String ZJTIME;
        String BZ;
        String filepath = android.os.Environment.getDataDirectory().getAbsolutePath() + "/" + "ArcGISSurvey";
        File file = new File(filepath, "attributedb.xls");
        if (file.exists()) {
            file.createNewFile();
        }
        workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet("居民地", 0);
        attribute = createSurveyDB.getReadableDatabase();
        Cursor jmdCursor = attribute.rawQuery("select * from JMDData", null);
        while (jmdCursor.moveToFirst()) for (int i = 0; i < jmdCursor.getCount(); i++) {
            ID = jmdCursor.getInt(0);
            FTName = jmdCursor.getString(1);
            LinkID = jmdCursor.getInt(2);
            FWCS = jmdCursor.getInt(3);
            FWCZ = jmdCursor.getString(4);
            FYGZ = jmdCursor.getDouble(5);
            ZJTIME = jmdCursor.getString(6);
            BZ = jmdCursor.getString(7);
            sheet.addCell(new Number(0, i, ID));
            sheet.addCell(new Label(1, i, FTName));
            sheet.addCell(new Number(2, i, LinkID));
            sheet.addCell(new Number(3, i, FWCS));
            sheet.addCell(new Label(4, i, FWCZ));
            sheet.addCell(new Number(5, i, FYGZ));
            sheet.addCell(new Label(6, i, ZJTIME));
            sheet.addCell(new Label(7, i, BZ));
        }
        jmdCursor.close();
        workbook.write();
        if (workbook != null) {
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void WriteToAttributeDLExcel(String name) throws IOException, WriteException {
        int ID;
        String FTName;
        int LinkID;
        String DLMC;
        String DLXH;
        String DJDM;
        String ZJTIME;
        String BZ;
        excelPath = getExcelDir()+File.separator+name+".xls";
        excelFile = new File(excelPath);
        if (excelFile.exists()){
            excelPath=getExcelDir()+File.separator+name+"(1).xls";
            excelFile = new File(excelPath);
        }
        WorkbookSettings wbsetting=new WorkbookSettings();
        wbsetting.setLocale(new Locale("en","EN"));
        WritableWorkbook workbook1;
        workbook1=Workbook.createWorkbook(excelFile,wbsetting);

        sheet = workbook1.createSheet("道路", 1);
        attribute = createSurveyDB.getReadableDatabase();
        Cursor dlCursor = attribute.rawQuery("select * from DLData", null);
        while (dlCursor.moveToFirst()) {
            for (int i = 0; i < dlCursor.getCount(); i++) {
                ID = dlCursor.getInt(0);
                FTName = dlCursor.getString(1);
                LinkID = dlCursor.getInt(2);
                DLMC = dlCursor.getString(3);
                DLXH = dlCursor.getString(4);
                DJDM = dlCursor.getString(5);
                ZJTIME = dlCursor.getString(6);
                BZ = dlCursor.getString(7);
                sheet.addCell(new Number(0, i, ID));
                sheet.addCell(new Label(1, i, FTName));
                sheet.addCell(new Number(2, i, LinkID));
                sheet.addCell(new Label(3, i, DLMC));
                sheet.addCell(new Label(4, i, DLXH));
                sheet.addCell(new Label(5, i, DJDM));
                sheet.addCell(new Label(6, i, ZJTIME));
                sheet.addCell(new Label(7, i, BZ));
            }
            workbook.write();
            dlCursor.close();
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void WriteToAttributeSXExcel() throws IOException, WriteException {
        WritableWorkbook workbook = null;
        int ID;
        String FTName;
        int LinkID;
        String YSMC;
        String FSSS;
        String ZJTIME;
        String BZ;
        String filepath = android.os.Environment.getDataDirectory().getAbsolutePath() + "/" + "ArcGISSurvey";
        File file = new File(filepath, "attributedb.xls");
        if (file.exists()) {
            file.createNewFile();
        }
        workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet("水系", 2);
        attribute = createSurveyDB.getReadableDatabase();
        Cursor sxCursor = attribute.rawQuery("select * from SXData", null);
        while (!sxCursor.moveToFirst()) {
            for (int i = 0; i < sxCursor.getCount(); i++) {
                ID = sxCursor.getInt(0);
                FTName = sxCursor.getString(1);
                LinkID = sxCursor.getInt(2);
                YSMC = sxCursor.getString(3);
                FSSS = sxCursor.getString(4);
                ZJTIME = sxCursor.getString(5);
                BZ = sxCursor.getString(6);
                sheet.addCell(new Number(0, i, ID));
                sheet.addCell(new Label(1, i, FTName));
                sheet.addCell(new Number(2, i, LinkID));
                sheet.addCell(new Label(3, i, YSMC));
                sheet.addCell(new Label(4, i, FSSS));
                sheet.addCell(new Label(5, i, ZJTIME));
                sheet.addCell(new Label(6, i, BZ));
            }
            sxCursor.close();
            workbook.write();
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void WriteToAttributeDMExcel() throws IOException, WriteException {
        WritableWorkbook workbook = null;
        int ID;
        String FTName;
        int LinkID;
        String DMMC;
        String ZJTIME;
        String BZ;
        String filepath = android.os.Environment.getDataDirectory().getAbsolutePath() + "/" + "ArcGISSurvey";
        File file = new File(filepath, "attributedb.xls");
        if (file.exists()) {
            file.createNewFile();
        }
        workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet("土质地貌", 3);
        attribute = createSurveyDB.getReadableDatabase();
        Cursor dmCursor = attribute.rawQuery("select * from DMData", null);
        while (dmCursor.moveToFirst()) {
            for (int i = 0; i < dmCursor.getCount(); i++) {
                ID = dmCursor.getInt(0);
                FTName = dmCursor.getString(1);
                LinkID = dmCursor.getInt(2);
                DMMC = dmCursor.getString(3);
                ZJTIME = dmCursor.getString(4);
                BZ = dmCursor.getString(5);
                sheet.addCell(new Number(0, i, ID));
                sheet.addCell(new Label(1, i, FTName));
                sheet.addCell(new Number(2, i, LinkID));
                sheet.addCell(new Label(3, i, DMMC));
                sheet.addCell(new Label(4, i, ZJTIME));
                sheet.addCell(new Label(5, i, BZ));
            }
            dmCursor.close();
            workbook.write();
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void WriteToAttributeZBExcel() throws IOException, WriteException {
        WritableWorkbook workbook = null;
        int ID;
        String FTName;
        int LinkID;
        String YSMC;
        String YSZL;
        String SSLC;
        String ZJTIME;
        String BZ;
        String filepath = android.os.Environment.getDataDirectory().getAbsolutePath() + "/" + "ArcGISSurvey";
        File file = new File(filepath, "attributedb.xls");
        if (file.exists()) {
            file.createNewFile();
        }
        workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet("植被", 4);
        attribute = createSurveyDB.getReadableDatabase();
        Cursor zbCursor = attribute.rawQuery("select * from ZBData", null);
        while (zbCursor.moveToFirst()) {
            for (int i = 0; i < zbCursor.getCount(); i++) {
                ID = zbCursor.getInt(0);
                FTName = zbCursor.getString(1);
                LinkID = zbCursor.getInt(2);
                YSMC = zbCursor.getString(3);
                YSZL = zbCursor.getString(4);
                SSLC = zbCursor.getString(5);
                ZJTIME = zbCursor.getString(6);
                BZ = zbCursor.getString(7);
                sheet.addCell(new Number(0, i, ID));
                sheet.addCell(new Label(1, i, FTName));
                sheet.addCell(new Number(2, i, LinkID));
                sheet.addCell(new Label(3, i, YSMC));
                sheet.addCell(new Label(4, i, YSZL));
                sheet.addCell(new Label(5, i, SSLC));
                sheet.addCell(new Label(6, i, ZJTIME));
                sheet.addCell(new Label(7, i, BZ));
            }
            zbCursor.close();
            workbook.write();
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void WriteToAttributeGXExcel() throws IOException, WriteException {
        WritableWorkbook workbook = null;
        int ID;
        String FTName;
        int LinkID;
        String DLXZX;
        String DLXFS;
        String ZJTIME;
        String BZ;
        String filepath = android.os.Environment.getDataDirectory().getAbsolutePath() + "/" + "ArcGISSurvey";
        File file = new File(filepath, "attributedb.xls");
        if (file.exists()) {
            file.createNewFile();
        }
        workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet("管线和电力线", 5);
        attribute = createSurveyDB.getReadableDatabase();
        Cursor gxCursor = attribute.rawQuery("select * from GXData", null);
        while (gxCursor.moveToFirst()) {
            for (int i = 0; i < gxCursor.getCount(); i++) {
                ID = gxCursor.getInt(0);
                FTName = gxCursor.getString(1);
                LinkID = gxCursor.getInt(2);
                DLXZX = gxCursor.getString(3);
                DLXFS = gxCursor.getString(4);
                ZJTIME = gxCursor.getString(5);
                BZ = gxCursor.getString(6);
                sheet.addCell(new Number(0, i, ID));
                sheet.addCell(new Label(1, i, FTName));
                sheet.addCell(new Number(2, i, LinkID));
                sheet.addCell(new Label(3, i, DLXZX));
                sheet.addCell(new Label(4, i, DLXFS));
                sheet.addCell(new Label(5, i, ZJTIME));
                sheet.addCell(new Label(6, i, BZ));
            }
            gxCursor.close();
            workbook.write();
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void WriteToAttributeJJXExcel() throws IOException, WriteException {
        WritableWorkbook workbook = null;
        int ID;
        String FTName;
        int LinkID;
        String GJ;
        String NBJJX;
        String ZJTIME;
        String BZ;
        String filepath = android.os.Environment.getDataDirectory().getAbsolutePath() + "/" + "ArcGISSurvey";
        File file = new File(filepath, "attributedb.xls");
        if (file.exists()) {
            file.createNewFile();
        }
        workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet("境界线", 6);
        attribute = createSurveyDB.getReadableDatabase();
        Cursor jjxCursor = attribute.rawQuery("select * from JJXData", null);
        while (jjxCursor.moveToFirst()) {
            for (int i = 0; i < jjxCursor.getCount(); i++) {
                ID = jjxCursor.getInt(0);
                FTName = jjxCursor.getString(1);
                LinkID = jjxCursor.getInt(2);
                GJ = jjxCursor.getString(3);
                NBJJX = jjxCursor.getString(4);
                ZJTIME = jjxCursor.getString(5);
                BZ = jjxCursor.getString(6);
                sheet.addCell(new Number(0, i, ID));
                sheet.addCell(new Label(1, i, FTName));
                sheet.addCell(new Number(2, i, LinkID));
                sheet.addCell(new Label(3, i, GJ));
                sheet.addCell(new Label(4, i, NBJJX));
                sheet.addCell(new Label(5, i, ZJTIME));
                sheet.addCell(new Label(6, i, BZ));
            }
            jjxCursor.close();
            workbook.write();
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void WriteToAttributeExcel() throws IOException, WriteException {
        WritableWorkbook workbook = null;
        int ID;
        int LinkID;
        String YSName;
        String YSType;
        String ZJTIME;
        String BZ;
        String filepath = android.os.Environment.getDataDirectory().getAbsolutePath() + "/" + "ArcGISSurvey";
        File file = new File(filepath, "attributedb.xls");
        if (file.exists()) {
            file.createNewFile();
        }
        workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet("地物注记", 7);
        attribute = createSurveyDB.getReadableDatabase();
        Cursor dwCursor=attribute.rawQuery("select * from PZJData",null);
        while (dwCursor.moveToFirst()){
            for (int i=0;i<dwCursor.getCount();i++){
                ID = dwCursor.getInt(0);
                LinkID = dwCursor.getInt(1);
                YSName = dwCursor.getString(2);
                YSType = dwCursor.getString(3);
                ZJTIME = dwCursor.getString(4);
                BZ = dwCursor.getString(5);
                sheet.addCell(new Number(0, i, ID));
                sheet.addCell(new Number(1, i, LinkID));
                sheet.addCell(new Label(2, i, YSName));
                sheet.addCell(new Label(3, i, YSType));
                sheet.addCell(new Label(4, i, ZJTIME));
                sheet.addCell(new Label(5, i, BZ));
            }
            dwCursor.close();
            workbook.write();
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void WriteToAttributeWZExcel(String name) throws IOException, WriteException {
        WritableWorkbook workbook = null;
        int ID;
        int LinkID;
        int YSDM;
        String YSName;
        String YSType;
        String ZJTIME;
        String BZ;
        String filepath = android.os.Environment.getDataDirectory().getAbsolutePath() + "/" + "ArcGISSurvey";
        File file = new File(filepath, name+".xls");
        if (file.exists()) {
            file.createNewFile();
        }
        workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet("文字注记", 8);
        attribute = createSurveyDB.getReadableDatabase();
        Cursor wzCursor= attribute.rawQuery("select * from WZBZData",null);
        while (wzCursor.moveToFirst()){
            for (int i=0;i<wzCursor.getCount();i++){
                ID = wzCursor.getInt(0);
                LinkID = wzCursor.getInt(1);
                YSDM=wzCursor.getInt(2);
                YSName = wzCursor.getString(3);
                YSType = wzCursor.getString(4);
                ZJTIME = wzCursor.getString(5);
                BZ = wzCursor.getString(6);
                sheet.addCell(new Number(0, i, ID));
                sheet.addCell(new Number(1, i, LinkID));
                sheet.addCell(new Number(2,i,YSDM));
                sheet.addCell(new Label(3, i, YSName));
                sheet.addCell(new Label(4, i, YSType));
                sheet.addCell(new Label(5, i, ZJTIME));
                sheet.addCell(new Label(6, i, BZ));
            }
            wzCursor.close();
            workbook.write();
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void WriteToAttributePhotoExcel() throws IOException, WriteException {
        WritableWorkbook workbook = null;
        int ID;
        String photoname;
        String photodescrible;
        String phototime;
        String filepath = android.os.Environment.getDataDirectory().getAbsolutePath() + "/" + "ArcGISSurvey";
        File file = new File(filepath, "attributedb.xls");
        if (file.exists()) {
            file.createNewFile();
        }
        workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet("拍照记录", 9);
        WritableImage image=new WritableImage(1,4,6,18,file);
        sheet.addImage(image);
        workbook.write();
        attribute = createSurveyDB.getReadableDatabase();
        Cursor imageCursor=attribute.rawQuery("select * from PhotoData",null);
        while (imageCursor.moveToFirst()){
            for (int i=0;i<imageCursor.getCount();i++){
                ID = imageCursor.getInt(0);
                photoname=imageCursor.getString(1);
                photodescrible = imageCursor.getString(2);
                phototime = imageCursor.getString(3);
                sheet.addCell(new Number(0, i, ID));
                sheet.addCell(new Label(1, i, photoname));
                sheet.addCell(new Label(2, i, photodescrible));
                sheet.addCell(new Label(3, i, phototime));
            }
            imageCursor.close();
            workbook.write();
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


