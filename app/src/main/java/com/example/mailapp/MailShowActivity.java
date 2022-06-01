package com.example.mailapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class MailShowActivity extends AppCompatActivity {
    TextView tv_email;
    TextView tv_subject;
    TextView tv_message;

    ArrayList<Email> emails;

    int index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_show);

        tv_email = findViewById(R.id.textViewEmail);
        tv_subject = findViewById(R.id.textViewSubject);
        tv_message = findViewById(R.id.textViewMessage);
        ImageButton bt_return = findViewById(R.id.buttonReturn);

        Intent intent = getIntent();
        if (intent.hasExtra("inbox_index")){
            index = intent.getIntExtra("inbox_index", -1);
            emails = EmailData.getInstance().inbox_messages;
        }
        else if (intent.hasExtra("sent_index")){
            index = intent.getIntExtra("sent_index", -1);
            emails = EmailData.getInstance().sent_messages;
        }
        tv_email.setText(emails.get(index).getEmail());
        tv_subject.setText(emails.get(index).getSubject());
        tv_message.setText(emails.get(index).getMessage());

        bt_return.setOnClickListener(view -> {
            Intent i = new Intent(MailShowActivity.this, MailboxActivity.class);
            startActivity(i);
        });
    }
}