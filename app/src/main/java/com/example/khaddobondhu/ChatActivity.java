package com.example.khaddobondhu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {
    TextView chatHeader;
    EditText editTextMessage;
    Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatHeader = findViewById(R.id.textViewChatHeader);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        String userName = getIntent().getStringExtra("userName");
        chatHeader.setText("Chatting with " + userName);

        buttonSend.setOnClickListener(v -> {
            String msg = editTextMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                Toast.makeText(this, "Sending: " + msg, Toast.LENGTH_SHORT).show();
                editTextMessage.setText("");
            }
        });
    }
}

