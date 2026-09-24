package com.example.printxpress;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // Log debug add panniyaachu
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.printxpress.adapter.CommentAdapter;
import com.example.printxpress.model.Comment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView rvComments;
    private EditText etComment;
    private MaterialButton btnSubmit;
    private TextView tvSubtitle;
    
    private CommentAdapter adapter;
    private List<Comment> commentList;
    
    private DatabaseReference dbRef;
    private String userId;
    private String category;
    private String userName;

    private static final String TAG = "CommentActivity"; // Logging purpose

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        rvComments = findViewById(R.id.rvComments);
        etComment = findViewById(R.id.etComment);
        btnSubmit = findViewById(R.id.btnSubmitComment);
        tvSubtitle = findViewById(R.id.tvCommentSubtitle);


        category = getIntent().getStringExtra("CATEGORY");
        if (category == null) {
            category = "General Inquiry";
        }
        
        tvSubtitle.setText("Chat about your " + category + " requirements");


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        
        if (userId == null) {
            userId = "Guest_" + System.currentTimeMillis(); // Random ID for guests
            userName = "Guest User";
            Log.d(TAG, "User not logged in, using Guest ID");
        } else {
            String email = mAuth.getCurrentUser().getEmail();
            if (email != null && email.contains("@")) {
                userName = email.split("@")[0];
            } else {
                userName = "User";
            }
        }


        dbRef = FirebaseDatabase.getInstance().getReference("comments").child(userId).child(category);

        commentList = new ArrayList<>();
        adapter = new CommentAdapter(commentList);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(adapter);


        loadCommentsFromFirebase();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = etComment.getText().toString().trim();
                if (!TextUtils.isEmpty(messageText)) {
                    sendNewMessage(messageText);
                } else {
                    Toast.makeText(CommentActivity.this, "Please enter some text", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadCommentsFromFirebase() {
        Log.d(TAG, "Starting to load comments...");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Comment comment = ds.getValue(Comment.class);
                    if (comment != null) {
                        commentList.add(comment);
                    }
                }
                adapter.notifyDataSetChanged();
                // Scroll to bottom
                if (!commentList.isEmpty()) {
                    rvComments.scrollToPosition(commentList.size() - 1);
                }
                Log.d(TAG, "Comments loaded: " + commentList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database Error: " + error.getMessage());
                Toast.makeText(CommentActivity.this, "Network error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNewMessage(String message) {

        String commentId = dbRef.push().getKey();
        

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        
        Comment commentObj = new Comment(commentId, currentDate, message, "", userName);
        
        if (commentId != null) {
            dbRef.child(commentId).setValue(commentObj).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    etComment.setText("");
                    Log.d(TAG, "Message sent successfully");
                } else {
                    Toast.makeText(CommentActivity.this, "Failed to send!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
