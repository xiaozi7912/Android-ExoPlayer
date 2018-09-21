package com.xiaozi.android.exoplayer.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaozi.android.exoplayer.R;

import it.sauronsoftware.ftp4j.FTPFile;

/**
 * Created by user on 2018-01-29.
 */

public class FTPFileListAdapter extends BaseAdapter {
    private final String LOG_TAG = getClass().getSimpleName();
    private Activity mActivity = null;
    private LayoutInflater mInflater = null;
    private FTPFile[] mDataList = null;

    public FTPFileListAdapter(Activity activity, FTPFile[] dataList) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList.length;
    }

    @Override
    public Object getItem(int position) {
        return mDataList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(LOG_TAG, "getView");
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_ftp_file_list, null, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FTPFile selectedItem = mDataList[position];
        holder.fileNameTextView.setText(selectedItem.getName());
        holder.fileSizeTextView.setText(String.format("%s MB", selectedItem.getSize() / (1024 * 1024)));
        return convertView;
    }

    private class ViewHolder {
        public TextView fileNameTextView = null;
        public TextView fileSizeTextView = null;

        public ViewHolder(View rootView) {
            fileNameTextView = rootView.findViewById(R.id.item_ftp_file_list_name_text);
            fileSizeTextView = rootView.findViewById(R.id.item_ftp_file_list_size_text);
        }
    }
}
