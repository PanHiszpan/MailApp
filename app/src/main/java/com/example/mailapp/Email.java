package com.example.mailapp;

import java.util.Comparator;

public class Email {
    private final String email;
    private final String subject;
    private final String message;
    private final String time;

    public Email(String email, String subject, String message, String time) {
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }


    public static Comparator<Email> sortByTimeAsc = new Comparator<Email>() {
        @Override
        public int compare(Email obj1, Email obj2) {
            return obj1.time.compareTo(obj2.time);
        }
    };
    public static Comparator<Email> sortByTimeDsc = new Comparator<Email>() {
        @Override
        public int compare(Email obj1, Email obj2) {
            return obj2.time.compareTo(obj1.time);
        }
    };
    public static Comparator<Email> sortByMailAsc = new Comparator<Email>() {
        @Override
        public int compare(Email obj1, Email obj2) {
            return obj1.email.compareTo(obj2.email);
        }
    };
    public static Comparator<Email> sortByMailDsc = new Comparator<Email>() {
        @Override
        public int compare(Email obj1, Email obj2) {
            return obj2.email.compareTo(obj1.email);
        }
    };
    public static Comparator<Email> sortBySubjectAsc = new Comparator<Email>() {
        @Override
        public int compare(Email obj1, Email obj2) {
            return obj1.subject.compareTo(obj2.subject);
        }
    };
    public static Comparator<Email> sortBySubjectDsc = new Comparator<Email>() {
        @Override
        public int compare(Email obj1, Email obj2) {
            return obj2.subject.compareTo(obj1.subject);
        }
    };

}
