package com.yh04.joyfulmindapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yh04.joyfulmindapp.R;
import com.yh04.joyfulmindapp.model.Diary;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {

    private List<Diary> diaryList;

    public DiaryAdapter(List<Diary> diaryList) {
        this.diaryList = diaryList;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_row, parent, false);
        return new DiaryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        Diary diary = diaryList.get(position);
        holder.txtTitle.setText(diary.getTitle());
        holder.txtDate.setText(formatDate(diary.getDate()));
        holder.txtDescription.setText(diary.getContent());
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    public void setDiaryList(List<Diary> diaryList) {
        this.diaryList = diaryList;
        notifyDataSetChanged();
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }

    static class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        TextView txtDate;
        TextView txtDescription;

        DiaryViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtDescription = itemView.findViewById(R.id.txtDescription);
        }
    }
}
