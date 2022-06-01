package com.example.mailapp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.apache.commons.mail.util.MimeMessageParser;

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ConnectionHandler {
    private static ConnectionHandler connectionHandler;

    static String host = UserData.getInstance().host;
    static String user = UserData.getInstance().user;
    static String password = UserData.getInstance().password;
    static Integer port = UserData.getInstance().port;

    public static void deleteMessage(int index, String folderName){
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

                Folder emailFolder = store.getFolder(folderName);
                emailFolder.open(Folder.READ_WRITE);
                Message[] messages = emailFolder.getMessages();
                messages[index].setFlag(Flags.Flag.DELETED, true);
                emailFolder.close(true);
                store.close();
            } catch (MessagingException e){
                e.printStackTrace();
            }
            handler.post(() -> {
            });
        });
    }

    public static ArrayList<Email> refreshEmailData(String folderName){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        ArrayList<Email> emails = new ArrayList<>();

        executor.execute(() -> {
            try {
                Properties properties = new Properties();
                properties.put("mail.imap.host", host);
                properties.put("mail.imap.port", port);
                properties.put("mail.imap.ssl.enable", "true");
                Session emailSession = Session.getInstance(properties);

                Store store = emailSession.getStore("imaps");
                store.connect(host, port, user, password);

                Folder emailFolder = store.getFolder(folderName);
                emailFolder.open(Folder.READ_ONLY);

                Message[] messages = emailFolder.getMessages();

                for (int j = 0; j < messages.length; j++) {
                    Message message = messages[j];

                    String sender = String.valueOf((message.getFrom() == null ? null : ((InternetAddress) message.getFrom()[0]).getAddress()));
                    String subject = message.getSubject();
                    String content = new MimeMessageParser((MimeMessage) message).parse().getPlainContent();
                    String time = String.valueOf(message.getReceivedDate());

                    Email mEmail = new Email(sender, subject, content, time);
                    emails.add(mEmail);
                }
                emailFolder.close(false);
                store.close();
            } catch (MessagingException e){
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            handler.post(() -> {
            });
        });
        return emails;
    }
}
