package com.example.mailapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

public class MailSendActivity extends AppCompatActivity {
    EditText et_email;
    EditText et_subject;
    EditText et_message;

    String user;
    String password;
    String host;
    Integer port;

    int index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_send);

        user = UserData.getInstance().user;
        password = UserData.getInstance().password;
        host = UserData.getInstance().host;
        port = UserData.getInstance().port;

        et_email = findViewById(R.id.editTextEmail);
        et_subject = findViewById(R.id.editTextSubject);
        et_message = findViewById(R.id.editTextMessage);
        TextView tv_save = findViewById(R.id.textViewSave);
        Button bt_Send = findViewById(R.id.buttonSend);
        Button bt_save = findViewById(R.id.buttonSave);
        Button bt_decline = findViewById(R.id.buttonDecline);
        ImageButton bt_return = findViewById(R.id.buttonReturn);

        Intent intent = getIntent();
        if (intent.hasExtra("draft_index")){
            index = intent.getIntExtra("draft_index", -1);
            Email draft = EmailData.getInstance().drafts.get(index);

            et_email.setText(draft.getEmail());
            et_subject.setText(draft.getSubject());
            et_message.setText(draft.getMessage());
        }

        bt_Send.setOnClickListener(view -> sendEmail());
        bt_return.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(et_email.getText()) || !TextUtils.isEmpty(et_subject.getText()) || !TextUtils.isEmpty(et_message.getText())) {
                bt_save.setVisibility(View.VISIBLE);
                bt_decline.setVisibility(View.VISIBLE);
                tv_save.setVisibility(View.VISIBLE);
                bt_Send.setEnabled(false);
                bt_return.setEnabled(false);
                et_email.setEnabled(false);
                et_subject.setEnabled(false);
                et_message.setEnabled(false);
            } else {
                Intent i = new Intent(MailSendActivity.this, MailboxActivity.class);
                startActivity(i);
            }
        });
        bt_save.setOnClickListener(view -> {
            Date currentTime = Calendar.getInstance().getTime();
            Email email = new Email(et_email.getText().toString(), et_subject.getText().toString(), et_message.getText().toString(), currentTime.toString());
            if (index != -1){
                ConnectionHandler.deleteMessage(index, "[Gmail]/Wersje robocze");
                EmailData.getInstance().drafts.remove(index);
            }
            EmailData.getInstance().drafts.add(email);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                try {
                    Properties properties = new Properties();
                    properties.put("mail.imap.host", host);
                    properties.put("mail.imap.port", port);
                    properties.put("mail.imap.ssl.enable", "true");
                    Session emailSession = Session.getInstance(properties);

                    Store store = emailSession.getStore("imaps");
                    store.connect(host, port, user, password);

                    Folder emailFolder = store.getFolder("[Gmail]/Wersje robocze");
                    emailFolder.open(Folder.READ_WRITE);
                    MimeMessage mm = new MimeMessage(emailSession);
                    mm.setFrom();
                    mm.setRecipients(Message.RecipientType.TO, et_email.getText().toString());
                    mm.setSubject(et_subject.getText().toString());
                    mm.setText(et_message.getText().toString());
                    mm.setFlag(Flags.Flag.DRAFT, true);
                    MimeMessage[] draftMessages = {mm};
                    emailFolder.appendMessages(draftMessages);
                    emailFolder.close(false);
                    store.close();
                } catch (MessagingException e){
                    e.printStackTrace();
                }
                handler.post(() -> {
                });
            });

            Intent i = new Intent(MailSendActivity.this, MailboxActivity.class);
            startActivity(i);
        });
        bt_decline.setOnClickListener(view -> {
            Intent i = new Intent(MailSendActivity.this, MailboxActivity.class);
            startActivity(i);
        });

        bt_save.setVisibility(View.INVISIBLE);
        bt_decline.setVisibility(View.INVISIBLE);
        tv_save.setVisibility(View.INVISIBLE);
    }

    private void sendEmail() {
        //Getting content for email
        String email = et_email.getText().toString().trim();
        String subject = et_subject.getText().toString().trim();
        String message = et_message.getText().toString().trim();

        //Creating SendMail object
        MailSend sm = new MailSend(this, user, password, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
    }
}