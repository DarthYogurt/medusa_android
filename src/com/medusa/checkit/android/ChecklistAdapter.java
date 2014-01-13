package com.medusa.checkit.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ChecklistAdapter {

//    Context context; 
//    int layoutResourceId;    
//    ArrayList<Deal> data = null;
//    
//    public ChecklistAdapter(Context context, int layoutResourceId, ArrayList<Deal> data) {
//        super(context, layoutResourceId, data);
//        this.layoutResourceId = layoutResourceId;
//        this.context = context;
//        this.data = data;
//    }
//
//    static class ViewHolder
//    {
//    	ImageView imgDealImage;
//    	TextView txtTitle;
//        TextView txtSupporters;
//        TextView txtRegularPrice;
//        TextView txtDiscountPrice;
//    }
//    
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder = null;
//        
//        if(convertView == null)
//        {
//            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
//            convertView = inflater.inflate(layoutResourceId, parent, false);
//            
//            holder = new ViewHolder();
//            holder.imgDealImage = (ImageView)convertView.findViewById(R.id.imgDealImage);
//            holder.txtTitle = (TextView)convertView.findViewById(R.id.txtTitle);
//            holder.txtSupporters = (TextView)convertView.findViewById(R.id.txtSupporters);
//            holder.txtRegularPrice = (TextView)convertView.findViewById(R.id.txtRegularPrice);
//            holder.txtDiscountPrice = (TextView)convertView.findViewById(R.id.txtDiscountPrice);
//            
//            convertView.setTag(holder);
//        }
//        else
//        {
//            holder = (ViewHolder)convertView.getTag();
//        }
//
//        int image = data.get(position).getImage();
//        String title = data.get(position).getTitle();
//        int currentSupporters = data.get(position).getCurrentSupporters();
//        int maxSupporters = data.get(position).getMaxSupporters();
//        double regularPrice = data.get(position).getRegularPrice();
//        double discountPrice = data.get(position).getDiscountPrice();
//        
//        holder.imgDealImage.setImageResource(image);
//        holder.txtTitle.setText(title);
//        holder.txtSupporters.setText(String.valueOf(currentSupporters + "/" + maxSupporters + " Supporters"));
//        holder.txtRegularPrice.setText(String.valueOf("$" + String.format("%.2f", regularPrice)));
//        holder.txtRegularPrice.setPaintFlags(holder.txtRegularPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//        holder.txtDiscountPrice.setText(String.valueOf("$" + String.format("%.2f", discountPrice)));
//        
//        return convertView;
//    }
}
