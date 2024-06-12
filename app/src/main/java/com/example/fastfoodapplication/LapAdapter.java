package com.example.fastfoodapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.fastfoodlib.util.Lap;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class LapAdapter extends RecyclerView.Adapter<LapAdapter.ViewHolder> {
    private static final String TAG = LapAdapter.class.getName();
    private List<Lap> laps;
    private List<Lap> sortedLaps;

    public LapAdapter(List<Lap> laps) {
        this.laps = laps;
        this.sortedLaps = laps;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewPlacement;
        public TextView textViewName;
        public TextView textViewScore;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewPlacement = itemView.findViewById(R.id.row_lap_rv_placement_text);
            textViewName = itemView.findViewById(R.id.row_lap_rv_name_text);
            textViewScore = itemView.findViewById(R.id.row_lap_rv_score_text);
        }
    }

    @NonNull
    @Override
    public LapAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View lapView = inflater.inflate(R.layout.row_lap_rv, parent, false);
        return new ViewHolder(lapView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(LapAdapter.ViewHolder holder, int position) {
        Lap lap = sortedLaps.get(position);
        String place = String.valueOf(sortedLaps.indexOf(lap) + 1);
        holder.textViewPlacement.setText(place);
        holder.textViewName.setText(lap.getName());
        holder.textViewScore.setText(lap.getLapTimeFormatted());
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
            case "Altijd":
            case "All":
                this.sortedLaps = laps;
                break;

            case "Dag":
            case "Day":
                for (Lap lap : laps) {
                    LocalDate dateOfDrive = lap.getDateOfDrive();
                    long daysBetween = ChronoUnit.DAYS.between(dateOfDrive, currentDate);
                    if (daysBetween < 1) {
                        sortedLaps.add(lap);
                    }
                }
                break;

            case "Week":
                for (Lap lap : laps) {
                    LocalDate dateOfDrive = lap.getDateOfDrive();
                    long daysBetween = ChronoUnit.DAYS.between(dateOfDrive, currentDate);
                    if (daysBetween < 7) {
                        sortedLaps.add(lap);
                    }
                }
                break;

            case "Maand":
            case "Month":
                for (Lap lap : laps) {
                    LocalDate dateOfDrive = lap.getDateOfDrive();
                    long daysBetween = ChronoUnit.DAYS.between(dateOfDrive, currentDate);
                    if (daysBetween < 31) {
                        sortedLaps.add(lap);
                    }
                }
                break;

            case "Jaar":
            case "Year":
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
