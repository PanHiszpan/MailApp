package com.example.mailapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSend extends AsyncTask<Void,Void,Void> {

    //Declaring Variables
    private Context context;
    private Session session;

    //Information to send email
    private String user;
    private String password;
    private String email;
    private String subject;
    private String message;

    //ProgressDialog to show while sending email
    private ProgressDialog progressDialog;

    //Class Constructor
    public MailSend(Context context, String user, String password, String email, String subject, String message){
        //Initializing variables
        this.context = context;
        this.user = user;
        this.password = password;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        progressDialog = ProgressDialog.show(context,"Sending message","Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
        progressDialog.dismiss();
        //Showing a success message
        Toast.makeText(context,"Message Sent",Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");



        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);   //  ---!TODO!--- pobierz z propertioes mainActivity
                    }
                });

        try {
            System.out.println("Before creating MimeMessage object [1]");
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            System.out.println("After creating MimeMessage object [2]");

            //Setting sender address
            mm.setFrom(new InternetAddress(user));    //  ---!TODO!--- jw ^
            System.out.println("set from mail [3]");
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            System.out.println("set to mail [4]");
            //Adding subject
            mm.setSubject(subject);
            System.out.println("set subject [5]");
            //Adding message
            mm.setText(message);

            System.out.println("set message [6]");

            //Sending email
            Transport.send(mm);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

}

