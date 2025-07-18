package com.example.khaddobondhu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.khaddobondhu.model.Message;
import com.example.khaddobondhu.model.User;
import com.example.khaddobondhu.service.FirebaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private TextView chatHeader;
    private EditText editTextMessage;
    private Button buttonSend;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messages;
    
    private FirebaseService firebaseService;
    private String receiverId, receiverName, chatId;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Firebase service
        firebaseService = new FirebaseService();
        currentUser = firebaseService.getCurrentUser();
        
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to send messages", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get intent data
        receiverId = getIntent().getStringExtra("userId");
        receiverName = getIntent().getStringExtra("userName");
        
        if (receiverId == null || receiverName == null) {
            Toast.makeText(this, "Invalid user data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Generate chat ID
        chatId = currentUser.getUid().compareTo(receiverId) < 0 ? 
            currentUser.getUid() + "_" + receiverId : receiverId + "_" + currentUser.getUid();

        // Initialize views
        chatHeader = findViewById(R.id.textViewChatHeader);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        // Setup UI
        chatHeader.setText("Chatting with " + receiverName);
        
        // Setup RecyclerView
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages, currentUser.getUid());
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        // Load messages
        loadMessages();

        // Setup send button
        buttonSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        firebaseService.getMessages(chatId, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    messages.clear();
                    
                    for (var document : task.getResult()) {
                        Message message = document.toObject(Message.class);
                        if (message != null) {
                            message.setId(document.getId());
                            messages.add(message);
                        }
                    }
                    
                    messageAdapter.notifyDataSetChanged();
                    scrollToBottom();
                } else {
                    Toast.makeText(ChatActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        
        if (messageText.isEmpty()) {
            return;
        }

        // Create message object
        Message message = new Message(currentUser.getUid(), receiverId, messageText);
        message.setChatId(chatId);

        // Disable send button temporarily
        buttonSend.setEnabled(false);

        // Send message
        firebaseService.sendMessage(message, new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                buttonSend.setEnabled(true);
                
                if (task.isSuccessful()) {
                    editTextMessage.setText("");
                    // Message will be loaded in real-time via listener
                } else {
                    Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void scrollToBottom() {
        if (messages.size() > 0) {
            recyclerViewMessages.smoothScrollToPosition(messages.size() - 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update user's last active time
        firebaseService.updateUserLastActive();
    }
}

