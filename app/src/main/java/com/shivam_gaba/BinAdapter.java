package com.shivam_gaba;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BinAdapter extends RecyclerView.Adapter<BinAdapter.ViewHolder> {


    Context context;
    ArrayList<marker> binList;

    public BinAdapter(ArrayList<marker> binList, Context context) {
        this.binList = binList;
        this.context=context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAddress, tvBinLevel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBinLevel = itemView.findViewById(R.id.tvBinLevel);
            tvAddress = itemView.findViewById(R.id.tvAddress);
        }
    }


    @NonNull
    @Override
    public BinAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bins_list_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BinAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(binList.get(position));

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(binList.get(position).getLat(),binList.get(position).getLng(), 1);
        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        String add;
        try {
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);
            addresses.clear();
            holder.tvAddress.setTextColor(Color.BLACK);

        } catch (Exception e) {
            add = "Address not provided";
            holder.tvAddress.setTextColor(Color.RED);
        }
        holder.tvAddress.setText(add);
        holder.tvBinLevel.setText("Garbage level : "+binList.get(position).getPercent()+"%");
    }

    @Override
    public int getItemCount() {
        return binList.size();
    }
}