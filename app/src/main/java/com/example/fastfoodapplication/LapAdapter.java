package com.example.fastfoodapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.fastfoodlib.util.Lap;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class LapAdapter extends RecyclerView.Adapter<LapAdapter.ViewHolder> {
    private List<Lap> laps;
    private List<Lap> sortedLaps;
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
        this.sortedLaps = laps;
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
        Lap lap = sortedLaps.get(position);
        String place = String.valueOf(sortedLaps.indexOf(lap) + 1);
        if (place == null) {
            place = "0";
        }

        holder.textViewPlacement.setText(place);
        holder.textViewName.setText(lap.getName());
        holder.textViewscore.setText(lap.getLapTimeFormatted());
    }

    public void setLaps(List<Lap> laps) {
        this.laps = laps;
        this.sortedLaps = laps;
    }

    @Override
    public int getItemCount() {
        return sortedLaps.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sortList(String period) {
        sortedLaps = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        switch (period) {
            case "altijd":
            case "always":
                this.sortedLaps = laps;
                break;

            case "dag":
            case "day":
                for (Lap lap : laps) {
                    LocalDate dateOfDrive = lap.getDateOfDrive();
                    long daysBetween = ChronoUnit.DAYS.between(dateOfDrive, currentDate);
                    if (daysBetween < 1) {
                        sortedLaps.add(lap);
                    }
                }
                break;

            case "week":
                for (Lap lap : laps) {
                    LocalDate dateOfDrive = lap.getDateOfDrive();
                    long daysBetween = ChronoUnit.DAYS.between(dateOfDrive, currentDate);
                    if (daysBetween < 7) {
                        sortedLaps.add(lap);
                    }
                }
                break;

            case "maand":
            case "month":
                for (Lap lap : laps) {
                    LocalDate dateOfDrive = lap.getDateOfDrive();
                    long daysBetween = ChronoUnit.DAYS.between(dateOfDrive, currentDate);
                    if (daysBetween < 31) {
                        sortedLaps.add(lap);
                    }
                }
                break;

            case "jaar":
            case "year":
                for (Lap lap : laps) {
                    LocalDate dateOfDrive = lap.getDateOfDrive();
                    long daysBetween = ChronoUnit.DAYS.between(dateOfDrive, currentDate);
                    if (daysBetween < 365) {
                        sortedLaps.add(lap);
                    }
                }
                break;
        }
    }


}
