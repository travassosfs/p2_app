package com.fstravassos.sirast.master.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fstravassos.sirast.R;
import com.fstravassos.sirast.master.models.Slavery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by felip_000 on 06/02/2017.
 */

public class PhonesListAdapter extends BaseAdapter {

    List<Slavery> myList = new ArrayList();
    LayoutInflater inflater;
    Context context;

    public PhonesListAdapter(Context context, List myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Slavery getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_slaver, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        Slavery currentListData = getItem(position);

        mViewHolder.mTvName.setText(currentListData.getName());
        mViewHolder.mTvNumber.setText(currentListData.getNumber());

        return convertView;
    }

    private class MyViewHolder {

        TextView mTvName;
        TextView mTvNumber;
        LinearLayout mLlBg;

        public MyViewHolder(View item) {
            mTvName = (TextView) item.findViewById(R.id.tv_item_adapter_name);
            mTvNumber = (TextView) item.findViewById(R.id.tv_item_adapter_number);
            mLlBg = (LinearLayout) item.findViewById(R.id.ll_bg);
        }
    }
}
