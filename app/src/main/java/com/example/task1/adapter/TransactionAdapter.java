package com.example.task1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task1.R;
import com.example.task1.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;
    private List<Transaction> filteredList;

    public TransactionAdapter(List<Transaction> list) {
        this.transactionList = list;
        this.filteredList = new ArrayList<>(list);
    }

    public void filter(String text) {
        filteredList.clear();

        if (text == null || text.trim().isEmpty()) {
            filteredList.addAll(transactionList);
        } else {
            String query = text.toLowerCase().trim();

            for (Transaction t : transactionList) {
                // Match category and description (case-insensitive)
                boolean matchesCategory = t.getCategory().toLowerCase().contains(query);
                boolean matchesDescription = t.getDescription().toLowerCase().contains(query);

                // Match amount if input is a number
                boolean matchesAmount = false;
                try {
                    double searchAmount = Double.parseDouble(query);
                    matchesAmount = t.getAmount() == searchAmount;
                } catch (NumberFormatException e) {
                    // not a number, ignore amount matching
                }

                if (matchesCategory || matchesDescription || matchesAmount) {
                    filteredList.add(t);
                }
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = filteredList.get(position);
        holder.title.setText(transaction.getCategory());
        holder.amount.setText(String.valueOf(transaction.getAmount()));
        holder.date.setText(transaction.getDate());
        holder.tvDesc.setText(transaction.getDescription());
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount, date, tvDesc;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            amount = itemView.findViewById(R.id.tvAmount);
            date = itemView.findViewById(R.id.tvDate);
            tvDesc = itemView.findViewById(R.id.tvDesc);
        }
    }
}
