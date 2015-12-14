package com.example.yanhejin.myapplication.ChildActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;

import com.example.yanhejin.myapplication.Database.CreateSpatialDB;
import com.example.yanhejin.myapplication.Database.CreateSurveyDB;
import com.example.yanhejin.myapplication.ExpandableListViewAdapter;
import com.example.yanhejin.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class FeatureType extends AppCompatActivity {

    CreateSurveyDB createSurveyDB;
    CreateSpatialDB createSpatialDB;
    ExpandableListView expandableListView;
    View featureview;
    Dialog featureDialog;
    List<String> groupArray=new ArrayList<String>();
    List<List<String>> childArray=new ArrayList<List<String>>();
    ExpandableListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_type);
        inidate();
    }

    /*
   * 居民地属性信息
   * */
    public void setjumindi(){
        View fwview = getLayoutInflater().inflate(R.layout.jumindi, null, false);
        final EditText fwcstext = (EditText) fwview.findViewById(R.id.fwcs);
        final EditText fwcztext = (EditText) fwview.findViewById(R.id.fwcz);
        final EditText fygztext = (EditText) fwview.findViewById(R.id.fygz);
        final EditText bztext = (EditText) fwview.findViewById(R.id.bz);
        AlertDialog.Builder fwbuilder = new AlertDialog.Builder(FeatureType.this);
        fwbuilder.setView(fwview);
        fwbuilder.setTitle("填写房屋调绘属性表");
        fwbuilder.setNegativeButton("取消", null);
        fwbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase fwdb = createSurveyDB.getReadableDatabase();
                ContentValues values = new ContentValues();
                values.put("FWCS", Integer.valueOf(fwcstext.getText().toString()));
                values.put("FWCZ", fwcztext.getText().toString());
                values.put("FYGZ", Float.parseFloat(fygztext.getText().toString()));
                values.put("BZ", bztext.getText().toString());
                fwdb.insert("JMDData", null, values);
                fwdb.close();
            }
        });
        fwbuilder.create().show();
    }
    /*
    * 道路属性信息*/
    public void setdaolu(){
        final View daoluview = getLayoutInflater().inflate(R.layout.daolu_layout,null,false);
        final RadioButton tielubtn = (RadioButton) daoluview.findViewById(R.id.tielu);
        final RadioButton gonglubtn = (RadioButton) daoluview.findViewById(R.id.gonglu);
        final RadioButton jigengbtn = (RadioButton) daoluview.findViewById(R.id.jigenglu);
        RadioButton xiangcunbtn = (RadioButton) daoluview.findViewById(R.id.xiangcunlu);
        RadioButton xiaolubtn = (RadioButton) daoluview.findViewById(R.id.xiaolu);
        RadioButton neibulubtn = (RadioButton) daoluview.findViewById(R.id.neibudaolu);

        final View daolu = getLayoutInflater().inflate(R.layout.daolu, null, false);
        final EditText dlmctext = (EditText) daolu.findViewById(R.id.dlmc);
        final EditText dlxhlutext = (EditText) daolu.findViewById(R.id.dlxh);
        final EditText djdmtext = (EditText) daolu.findViewById(R.id.djdm);
        final EditText bztext= (EditText) daolu.findViewById(R.id.bz);
        AlertDialog.Builder tielubuider=new AlertDialog.Builder(FeatureType.this);
        tielubuider.setTitle("填写道路属性");
        tielubuider.setView(daolu);
        tielubuider.setPositiveButton("取消", null);
        tielubuider.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase daoludb = createSurveyDB.getReadableDatabase();
                ContentValues daoluValues = new ContentValues();
                daoluValues.put("DLMC", dlmctext.getText().toString());
                daoluValues.put("DLXH", Integer.valueOf(dlxhlutext.getText().toString()));
                daoluValues.put("DJDM", djdmtext.getText().toString());
                daoluValues.put("BZ", bztext.getText().toString());
                daoludb.insert("DLData", null, daoluValues);
                daoludb.close();
            }
        });
        tielubuider.create().show();
    }
    /*
    * 获取可展开列表的数据，判断类型
    * */
    public void getfeaturelayout(){
        expandableListView= (ExpandableListView) findViewById(R.id.featurelist);
        adapter=new  ExpandableListViewAdapter(FeatureType.this,groupArray,childArray);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                String feature = adapter.getGroup(groupPosition).toString();
                switch (feature) {
                    case "居民地":
                        setjumindi();
                        break;
                    case "道路":
                        setdaolu();
                        break;
                    case "管线、垣栅":
                        break;
                    case "水系":
                        break;
                    case "植被":
                        break;
                    case "境界线":
                        break;
                    case "地貌和土质":
                        break;
                }
                return false;
            }
        });
    }

    /*
    * 初始化采集要素数据
    * */
    public void inidate(){
        groupArray=new ArrayList<String>();
        childArray=new ArrayList<List<String>>();
        groupArray.add("居民地");
        groupArray.add("道路");
        groupArray.add("管线、垣栅");
        groupArray.add("水系");
        groupArray.add("植被");
        groupArray.add("境界线");
        groupArray.add("地貌和土质");
        groupArray.add("地理名称和注记");
        for (int i=0;i<groupArray.size();i++){
            ArrayList<String> childItem=new ArrayList<String>();
            if (i==0){
                childItem.add("独立房屋");
                childItem.add("街区式居民地");
                childItem.add("散列式居民地");
                childItem.add("窑洞式居民地");
                childItem.add("其他");
            }else if (i==1){
                childItem.add("铁路");
                childItem.add("公路");
                childItem.add("机耕路");
                childItem.add("乡村路");
                childItem.add("小路");
                childItem.add("内部道路");
            }else if (i==2){
                childItem.add("管线");
                childItem.add("电力线");
                childItem.add("通讯线");
                childItem.add("管道");
                childItem.add("垣栅");
                childItem.add("城墙");
                childItem.add("围墙");
                childItem.add("栅栏");
                childItem.add("铁丝网");
                childItem.add("篱笆");
                childItem.add("堤");
            }else if (i==3){
                childItem.add("河流");
                childItem.add("湖泊");
                childItem.add("水库");
                childItem.add("池塘");
                childItem.add("沟渠");
                childItem.add("水源");
                childItem.add("沼泽");
                childItem.add("海岸线");
                childItem.add("瀑布");
                childItem.add("石滩");
                childItem.add("陡岸");
            }else if (i==4){
                childItem.add("森林");
                childItem.add("疏林");
                childItem.add("苗圃");
                childItem.add("狭长树木");
                childItem.add("行数");
                childItem.add("零星树木");
                childItem.add("经济林");
                childItem.add("草地");
                childItem.add("高草地");
                childItem.add("半荒植物地");
                childItem.add("荒草地");
                childItem.add("经济作物地");
                childItem.add("水生作物地");
                childItem.add("耕地");
                childItem.add("菜地");
            }else if (i==5){
                childItem.add("国界");
                childItem.add("国内各种境界");
            }else if (i==6){
                childItem.add("干河床");
                childItem.add("干涸湖");
                childItem.add("冲沟");
                childItem.add("陡崖");
                childItem.add("崩崖");
                childItem.add("滑坡");
                childItem.add("溶洞");
                childItem.add("熔岩漏斗");
                childItem.add("岩峰");
                childItem.add("梯田坎");
                childItem.add("山隘");
                childItem.add("陡石山");
                childItem.add("露岩地");
                childItem.add("石块地");
                childItem.add("戈壁滩");
                childItem.add("盐碱地");
                childItem.add("龟裂地");
                childItem.add("沙地");
                childItem.add("雪山");
            }
            childArray.add(childItem);
        }
    }
}
