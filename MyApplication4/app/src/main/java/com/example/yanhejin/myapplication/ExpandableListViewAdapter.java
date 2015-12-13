package com.example.yanhejin.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by licheetec on 2015/5/2.
 */
public class ExpandableListViewAdapter extends BaseExpandableListAdapter {
    Context context;

    List<String> groupArray=new ArrayList<String>();
    List<List<String>> childArray=new ArrayList<List<String>>();
    int posGroup=0;
    int posChild=0;
    LayoutInflater inflater;
    public ExpandableListViewAdapter(AppCompatActivity appCompatActivity,List<String> group,List<List<String>> child){
        super();
        this.context=appCompatActivity;
        inflater=LayoutInflater.from(context);
        groupArray=group;
        childArray=child;
    }
    @Override
    public int getGroupCount() {
        return groupArray.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childArray.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupArray.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childArray.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        posGroup=groupPosition;
        posChild=childPosition;
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String string=groupArray.get(groupPosition);
        GroupHolder groupHolder=null;
        if (convertView==null){
            groupHolder=new GroupHolder();
            convertView= inflater.inflate(R.layout.group,null);
            groupHolder.textView= (TextView) convertView.findViewById(R.id.grouptextview);
            groupHolder.imageView= (ImageView) convertView.findViewById(R.id.groupimage);
            groupHolder.textView.setTextSize(15);
            groupHolder.textView.setPadding(40,0,0,0);
            convertView.setTag(groupHolder);
        }else {
            groupHolder= (GroupHolder) convertView.getTag();
        }
        groupHolder.textView.setText(getGroup(groupPosition).toString());
        if (isExpanded){
            groupHolder.imageView.setImageResource(R.drawable.collapse);
        }else {
            groupHolder.imageView.setImageResource(R.drawable.expanded);
        }
        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String string=childArray.get(groupPosition).get(childPosition);
        View view=getGenericView(string);
        TextView textView= (TextView) view;
        if (this.posGroup==groupPosition&&this.posChild==childPosition){
            textView.setTextColor(Color.WHITE);
        }
       /* if (convertView==null){
            convertView=inflater.inflate(R.layout.item,null);
        }
        TextView textView= (TextView) convertView.findViewById(R.id.itemview);
        textView.setTextSize(13);
        textView.setPadding(40,0,0,0);
        textView.setText(getChild(groupPosition,childPosition).toString());*/

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public TextView getGenericView(String string){
        AbsListView.LayoutParams lp=new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //lp.height=80;
        TextView textView=new TextView(context);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setPadding(36, 0, 0, 0);
        textView.setTextColor(Color.BLACK);
        textView.setText(string);
        textView.setTextSize(20);
        return textView;
    }

    class GroupHolder{
        TextView textView;
        ImageView imageView;
    }
}
