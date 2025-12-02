package com.example.smart_air_app.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smart_air_app.R;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> { //holder is bassically here so I dont do a query for every single row of the data from the databse

    private ArrayList<CheckInFields> lst;


    public HistoryAdapter(ArrayList<CheckInFields> list) {
        this.lst = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {  /// truns the text in XML to layout

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_hist, parent, false);   ////there is an XML file for each row and the othe XML one (activity one) is for the whole thing
                                                                                            ///  idk how many rows are goona be there
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {  //after getting the entire things that fit the date from data base gets that and puts it in position in the rows
        //basically puts together the data of the position row into its holder
        CheckInFields item = lst.get(position);


        if (item.date != null) {
            String date = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    .format(new Date(item.date));
            holder.dateText.setText(date);
        }


        holder.authorText.setText(item.author); //the author

        ArrayList<String> sympt = new ArrayList<String>();


        if (item.cough) sympt.add("Cough/Wheeze");
        if (item.nightWaking) sympt.add("Night Waking");
        if (item.activityLimits) sympt.add("Activity Limits");

        String symptStr = "None";   //assume the didn't pick any symptoms

        if ( !sympt.isEmpty())   symptStr = String.join(", ", sympt);



        holder.symptomsText.setText(symptStr);  // the symptoms


        if (item.triggers != null && !item.triggers.isEmpty()) {
            holder.triggersText.setText(item.triggers);   //the triggers
        } else {
            holder.triggersText.setText("None");
        }
    }

    @Override
    public int getItemCount() { //so I can run a for loop on the outside with no problems
        return lst.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder { //a single row of my list
        TextView dateText, authorText, symptomsText, triggersText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.textDate);
            authorText = itemView.findViewById(R.id.textAuthor);
            symptomsText = itemView.findViewById(R.id.textSymptoms);
            triggersText = itemView.findViewById(R.id.textTriggers);
        }
    }
}
