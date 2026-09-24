package com.example.printxpress.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printxpress.R;
import com.example.printxpress.model.Comment;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        holder.tvUsername.setText(comment.getUsername());
        holder.tvUserMsg.setText(comment.getMessage());
        holder.tvDate.setText(comment.getDate());

        if (comment.getReply() != null && !TextUtils.isEmpty(comment.getReply())) {
            holder.layoutAdmin.setVisibility(View.VISIBLE);
            holder.tvAdminMsg.setText(comment.getReply());

        } else {
            holder.layoutAdmin.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        View layoutUser, layoutAdmin;
        TextView tvUserMsg, tvAdminMsg, tvUsername, tvDate;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutUser = itemView.findViewById(R.id.layoutUser);
            layoutAdmin = itemView.findViewById(R.id.layoutAdmin);
            tvUserMsg = itemView.findViewById(R.id.tvUserMsg);
            tvAdminMsg = itemView.findViewById(R.id.tvAdminMsg);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
