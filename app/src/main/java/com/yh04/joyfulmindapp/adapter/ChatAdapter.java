package com.yh04.joyfulmindapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yh04.joyfulmindapp.R;
import com.yh04.joyfulmindapp.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> chatMessages;
    private String currentUser;

    public ChatAdapter(List<ChatMessage> chatMessages, String currentUser) {
        this.chatMessages = chatMessages;
        this.currentUser = currentUser;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        if (message.getNickname() != null && message.getNickname().equals(currentUser)) {
            return 0; // 사용자의 메시지
        } else if (message.getNickname() != null && message.getNickname().equals("조이")) {
            return 1; // 조이의 메시지
        } else {
            return -1; // 기본 값 또는 오류 처리
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mychat_row, parent, false);
            return new MyChatViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.joychat_row, parent, false);
            return new JoyChatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (holder instanceof MyChatViewHolder) {
            ((MyChatViewHolder) holder).bind(chatMessage);
        } else {
            ((JoyChatViewHolder) holder).bind(chatMessage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class MyChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtMyMessage, txtMyTime, txtMyName;
        ImageView imgMessage;
        CircleImageView profileImage;

        public MyChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMyMessage = itemView.findViewById(R.id.txtMyMessage);
            txtMyTime = itemView.findViewById(R.id.txtMyTime);
            txtMyName = itemView.findViewById(R.id.txtMyName);
            imgMessage = itemView.findViewById(R.id.imgMessage);
            profileImage = itemView.findViewById(R.id.profileImage);
        }

        public void bind(ChatMessage chatMessage) {
            txtMyName.setText(chatMessage.getNickname());
            txtMyTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(chatMessage.getTimestamp().toDate()));

            if (chatMessage.getMessage() != null) {
                txtMyMessage.setVisibility(View.VISIBLE);
                txtMyMessage.setText(chatMessage.getMessage());
                imgMessage.setVisibility(View.GONE);
            } else {
                txtMyMessage.setVisibility(View.GONE);
                imgMessage.setVisibility(View.GONE);
            }

            // 프로필 이미지 로드 (필요 시 주석 해제)
//            String profileImageUrl = chatMessage.getProfileImageUrl();
//            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
//                Glide.with(profileImage.getContext())
//                        .load(profileImageUrl)
//                        .placeholder(R.drawable.default_profile_image)
//                        .error(R.drawable.default_profile_image)
//                        .into(profileImage);
//            } else {
//                profileImage.setImageResource(R.drawable.default_profile_image);
//            }
        }
    }

    static class JoyChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtJoyMessage, txtJoyTime, txtJoyName;
        ImageView imgMessageJoy;
        CircleImageView profileImage;

        public JoyChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtJoyMessage = itemView.findViewById(R.id.txtJoyMessage);
            txtJoyTime = itemView.findViewById(R.id.txtJoyTime);
            txtJoyName = itemView.findViewById(R.id.txtJoyName);
            imgMessageJoy = itemView.findViewById(R.id.imgMessageJoy);
            profileImage = itemView.findViewById(R.id.profileImage);
        }

        public void bind(ChatMessage chatMessage) {
            txtJoyName.setText(chatMessage.getNickname());
            txtJoyTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(chatMessage.getTimestamp().toDate()));

            if (chatMessage.getMessage() != null) {
                txtJoyMessage.setVisibility(View.VISIBLE);
                txtJoyMessage.setText(chatMessage.getMessage());
                imgMessageJoy.setVisibility(View.GONE);
            } else {
                txtJoyMessage.setVisibility(View.GONE);
                imgMessageJoy.setVisibility(View.GONE);
            }

            // 프로필 이미지 로드 (필요 시 주석 해제)
//            String profileImageUrl = chatMessage.getProfileImageUrl();
//            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
//                Glide.with(profileImage.getContext())
//                        .load(profileImageUrl)
//                        .placeholder(R.drawable.default_profile_image)
//                        .error(R.drawable.default_profile_image)
//                        .into(profileImage);
//            } else {
//                profileImage.setImageResource(R.drawable.default_profile_image);
//            }
        }
    }
}