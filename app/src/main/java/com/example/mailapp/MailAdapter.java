package com.example.mailapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

public class MailAdapter extends RecyclerView.Adapter<MailViewHolder> {
    private final List<Email> mEmailData;
    private Context mContext;

    public MailAdapter(Context mContext, List mEmailData) {
        this.mEmailData = mEmailData;
        this.mContext = mContext;
    }

    @Override
    public MailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_mail_item,
                parent, false);
        return new MailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MailViewHolder holder, int position) {
        holder.mSender.setText(mEmailData.get(position).getEmail());
        holder.mEmailTitle.setText(mEmailData.get(position).getSubject());
        holder.mEmailDetails.setText(mEmailData.get(position).getMessage());
        holder.mEmailTime.setText(mEmailData.get(position).getTime());
        if (!mEmailData.get(position).getEmail().equals("")) {
            holder.mIcon.setText(mEmailData.get(position).getEmail().substring(0, 1).toUpperCase());
        }
        Random mRandom = new Random();
        int color = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
        ((GradientDrawable) holder.mIcon.getBackground()).setColor(color);
    }

    @Override
    public int getItemCount() {
        return mEmailData.size();
    }
}

class MailViewHolder extends RecyclerView.ViewHolder {
    TextView mIcon;
    TextView mSender;
    TextView mEmailTitle;
    TextView mEmailDetails;
    TextView mEmailTime;
    ImageView mFavorite;

    MailViewHolder(View itemView) {
        super(itemView);

        mIcon = itemView.findViewById(R.id.tvIcon);
        mSender = itemView.findViewById(R.id.tvEmailSender);
        mEmailTitle = itemView.findViewById(R.id.tvEmailTitle);
        mEmailDetails = itemView.findViewById(R.id.tvEmailDetails);
        mEmailTime = itemView.findViewById(R.id.tvEmailTime);
        mFavorite = itemView.findViewById(R.id.ivFavorite);
    }
}
