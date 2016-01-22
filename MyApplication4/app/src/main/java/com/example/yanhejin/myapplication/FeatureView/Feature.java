package com.example.yanhejin.myapplication.FeatureView;

/**
 * Created by licheetec on 2015/5/2.
 */
public class Feature {
    String datetime;
    double x;
    double y;
    int linkID;
    String name;
    int fwcs;
    String fwcz;
    double fygz;
    String bz;

    public void setdatetime(String datetime){
        this.datetime=datetime;
    }

    public String getdatetimee(){
        return datetime;
    }
    public void setname(String name){
        this.name=name;
    }

    public String getname(){
        return name;
    }

    public void setX(double x){
        this.x=x;
    }

    public double getX(){
        return x;
    }

    public void setY(double y){
        this.x=y;
    }

    public double getY(){
        return y;
    }

    public void setID(int id){
        this.linkID=id;
    }

    public int getID(){
        return linkID;
    }

    public void setFWCS(int fwcs){
        this.fwcs=fwcs;
    }

    public int getFWCS(){
        return fwcs;
    }

    public void setFWCZ(String fwcz){
        this.fwcz=fwcz;
    }

    public String getFWCZ(){
        return fwcz;
    }

    public void setFYGZ(double fygz){
        this.fygz=fygz;
    }

    public double getFYGZ(){
        return fygz;
    }

    public void setBZ(String bz){
        this.bz=bz;
    }

    public String getBZ(){
        return bz;
    }





}
