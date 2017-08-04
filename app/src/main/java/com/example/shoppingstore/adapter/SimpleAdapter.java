package com.example.shoppingstore.adapter;

import android.content.Context;

import java.util.List;

/**
 * Created by 博 on 2017/7/12.
 */

public abstract class SimpleAdapter<T> extends BaseAdapter< T , BaseViewHolder>{

    public SimpleAdapter(Context context, List<T> mDatas, int resId) {
        super(context, mDatas, resId);
    }
    
}
