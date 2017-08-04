package com.example.shoppingstore.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.shoppingstore.MyView.NumControlerView;
import com.example.shoppingstore.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lzy.ninegrid.NineGridView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 博 on 2017/7/12.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private SparseArray<View> views ;
    private View itemView ;
    private BaseAdapter.onItemClickListener listener ;


    public BaseViewHolder(View itemView , BaseAdapter.onItemClickListener listener ) {
        super(itemView);

        this.itemView = itemView ;
        this.views = new SparseArray<View>() ;
        this.listener = listener ;

    }

    public SimpleDraweeView findSimpleDraweeView(int resId){
        return findView(resId) ;
    }

    public ImageView findImageView(int resId){
        return findView(resId) ;
    }

    public Button findButton (int resId){
        return findView(resId) ;
    }

    public TextView findTextView(int resId){
        return findView(resId) ;
    }

    public NineGridView findNineGridView(int resId){
        return findView(resId) ;
    }

    public RadioButton findRadioButton(int resId){
        return findView(resId) ;
    }

    public CheckBox findCheckBox(int resId) {return findView(resId) ; }

    public NumControlerView findNumControlerView(int resId) {return findView(resId) ;}

    private < T extends View> T findView (int resId){

        if ( views.get(resId) == null ){
            View view = itemView.findViewById(resId) ;
            views.put(resId , view);
            view.setOnClickListener(this);
        }

       return (T) views.get(resId) ;
    }

    @Override
    public void onClick(View v) {
        if(listener != null)
            try {
                listener.onClick(v , getLayoutPosition() );
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

}
