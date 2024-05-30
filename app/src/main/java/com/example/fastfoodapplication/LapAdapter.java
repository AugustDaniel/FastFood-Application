package com.example.fastfoodapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fastfoodlib.util.Lap;

import java.util.Collections;
import java.util.List;

public class LapAdapter extends RecyclerView.Adapter<LapAdapter.ViewHolder> {
    private List<Lap> laps;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(int clickedPosition);
    }

    public LapAdapter(Context context, List<Lap> laps, OnItemClickListener listener) {
        this.laps = laps;
        this.clickListener = listener;
    }

    public LapAdapter(Context context, List<Lap> laps) {
        this.laps = laps;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textViewPlacement;
        public TextView textViewName;
        public TextView textViewscore;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewPlacement = itemView.findViewById(R.id.activity_items_placement);
            textViewName = itemView.findViewById(R.id.activity_item_name);
            textViewscore = itemView.findViewById(R.id.activity_item_score);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(position);
                }
            }
        }
    }


    @Override
    public LapAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View lapView = inflater.inflate(R.layout.activity_item, parent, false);
        return new ViewHolder(lapView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(LapAdapter.ViewHolder holder, int position) {
        Lap lap = laps.get(position);
        String place = String.valueOf(laps.indexOf(lap) + 1);
        if (place == null) {
            place = "0";
        }

        holder.textViewPlacement.setText(place);
        holder.textViewName.setText(lap.getName());
        holder.textViewscore.setText(lap.getLapTime().toString());
    }

    public void setLaps(List<Lap> laps) {
        this.laps = laps;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return laps.size();
    }

}
