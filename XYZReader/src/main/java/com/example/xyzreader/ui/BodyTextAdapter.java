package com.example.xyzreader.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francesco on 18/05/2018.
 */

public class BodyTextAdapter extends RecyclerView.Adapter<BodyTextAdapter.ViewHolder> {
    List<String> bodyTexts = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.article_body_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String textPart = bodyTexts.get(position);
        holder.bodyTextTv.setText(textPart);
    }

    @Override
    public int getItemCount() {
        if(bodyTexts != null){
            return bodyTexts.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView bodyTextTv;

        public ViewHolder(View itemView) {
            super(itemView);
            bodyTextTv = itemView.findViewById(R.id.article_body);
        }
    }

    public void setBodyTexts(List<String> bodyTexts) {
        this.bodyTexts = bodyTexts;
    }
}
