package com.example.yanhejin.myapplication.OfflineEdit;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.yanhejin.myapplication.featurechecked;

import java.util.List;

/**
 * Created by licheetec on 2015/5/2.
 */
public class layerlistadapter extends BaseAdapter {
    private List<String> features;
    private Context c;

    public layerlistadapter(Context c, List<String> features) {
        super();
        this.c = c;
        this.features = features;
    }

    @Override
    public int getCount() {
        return features.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        //返回每一条Item的Id
        return arg0;
    }

    @Override
    public boolean hasStableIds() {
        //getCheckedItemIds()方法要求此处返回为真
        return true;
    }
    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {

        featurechecked featurechecked = new featurechecked(c, null);
        featurechecked.setTitle(features.get(arg0));
        return featurechecked;
    }
}
