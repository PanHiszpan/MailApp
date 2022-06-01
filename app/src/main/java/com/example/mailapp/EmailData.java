package com.example.mailapp;

import java.util.ArrayList;
import java.util.Collections;

public class EmailData {
    private static EmailData emailData;
    public ArrayList<Email> inbox_messages = new ArrayList<>();
    public ArrayList<Email> sent_messages = new ArrayList<>();
    public ArrayList<Email> drafts = new ArrayList<>();

    private EmailData() {}

    public static EmailData getInstance() {
        if (emailData == null) {
            emailData = new EmailData();
        }
        return emailData;
    }
}
