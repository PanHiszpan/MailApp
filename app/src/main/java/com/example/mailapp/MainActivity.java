package com.example.mailapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import org.apache.commons.mail.util.MimeMessageParser;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String TAG = "[Application]";
        Log.i(TAG, "Starting activity " + MainActivity.class.getName());

        TextView tv_emailAddress = (TextView) findViewById(R.id.emailAddress);
        TextView tv_password = (TextView) findViewById(R.id.password);
        TextView tv_imapServer = (TextView) findViewById(R.id.imapServer);
        TextView tv_port = (TextView) findViewById(R.id.port);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        MaterialButton bt_login = (MaterialButton) findViewById(R.id.loginBtn);


        //Testowo przypisalem gmaila
        tv_emailAddress.setText("testJavaMail32@gmail.com");
        tv_password.setText("KurwaJakiDzban34");
        tv_imapServer.setText("imap.gmail.com");
        tv_port.setText("993");


        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String host = tv_imapServer.getText().toString();
                String emailAddress = tv_emailAddress.getText().toString();
                String password = tv_password.getText().toString();
                Integer port = Integer.parseInt(tv_port.getText().toString());

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    check(host, emailAddress, password, port);
                    handler.post(() -> {
                    });
                });
            }

            @SuppressLint("SetTextI18n")
            public void check(String host, String user, String password, Integer port)
            {
                try {
                    Properties properties = new Properties();

                    properties.put("mail.imap.host", host);
                    properties.put("mail.imap.port", port);
                    properties.put("mail.imap.ssl.enable", "true");
                    Session emailSession = Session.getInstance(properties);

                    Store store = emailSession.getStore("imaps");
                    store.connect(host, port, user, password);

                    /*javax.mail.Folder[] folders = store.getDefaultFolder().list("*");
                    for (javax.mail.Folder folder : folders) {
                        if ((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
                            System.out.println(folder.getFullName() + ": " + folder.getMessageCount());
                        }
                    }*/

                    EmailData emailData = EmailData.getInstance();
                    UserData.getInstance().setUserData(user, password, host, port);

                    String[] foldersToGet = {"Inbox", "[Gmail]/Wysłane", "[Gmail]/Wersje robocze"};

                    for (String folder: foldersToGet
                    ) {
                        Folder emailFolder = store.getFolder(folder);
                        emailFolder.open(Folder.READ_ONLY);

                        Message[] messages = emailFolder.getMessages();
                        int messageCount = emailFolder.getMessageCount();
                        progressBar.setMax(messageCount);

                        Log.i(TAG, "Getting messages from folder " + folder);
                        for (int j = 0; j < messages.length; j++) {
                            Message message = messages[j];

                            String sender = String.valueOf((message.getFrom() == null ? null : ((InternetAddress) message.getFrom()[0]).getAddress()));
                            String subject = message.getSubject();
                            String content = new MimeMessageParser((MimeMessage) message).parse().getPlainContent();
                            String time = String.valueOf(message.getReceivedDate());

                            Email mEmail = new Email(sender, subject, content, time);
                            switch(folder){
                                case "Inbox":
                                    emailData.inbox_messages.add(mEmail);
                                    break;
                                case "[Gmail]/Wysłane":
                                    emailData.sent_messages.add(mEmail);
                                    break;
                                case "[Gmail]/Wersje robocze":
                                    emailData.drafts.add(mEmail);
                                    break;
                            }
                            progressBar.setProgress(j);
                        }
                        emailFolder.close(false);
                    }
                    store.close();

                    Intent i = new Intent(MainActivity.this, MailboxActivity.class);
                    startActivity(i);
                    Log.i(TAG, "Closing activity " + MainActivity.class.getName());
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

