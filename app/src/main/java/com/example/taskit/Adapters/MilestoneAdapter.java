package com.example.taskit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskit.Interfaces.RecyclerViewClickInterface;
import com.example.taskit.R;

import java.util.ArrayList;

public class MilestoneAdapter extends RecyclerView.Adapter<MilestoneAdapter.MilestonesViewHolder> {
    private ArrayList<String> nameList;
    private Context context;

    private RecyclerViewClickInterface recyclerViewClickInterface;


    public MilestoneAdapter(ArrayList<String> nameList, Context context, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.nameList = nameList;
        this.context = context;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public MilestoneAdapter.MilestonesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.list_item, parent, false);


        return new MilestonesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MilestonesViewHolder holder, int position) {
          holder.milestone.setText(nameList.get(position));
    }


    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public class MilestonesViewHolder extends RecyclerView.ViewHolder{
        TextView milestone;
        public MilestonesViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewClickInterface.onItemClick(getAdapterPosition());
                }
            });


            milestone = (TextView) itemView.findViewById(R.id.subTaskListItem);

        }
    }
}
