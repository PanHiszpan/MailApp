package com.example.mailapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class MailboxActivity extends AppCompatActivity {
    Spinner spinner;
    Spinner spinnerSort;
    RecyclerView mRecyclerView;

    String host;
    String password;
    String user;
    Integer port;

    Button bt_delete;
    Button bt_decline;
    Button bt_logout;
    ImageButton bt_refresh;
    ImageButton bt_newMsg;
    TextView tv_delete;
    ArrayList<Email> mEmailData;

    int lastIndex = -1;

    @RequiresApi(api = Build.VERSION_CODES.N)  //nie wiem po co to ale nie wywala blendow xd
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mailbox);

        mRecyclerView = findViewById(R.id.recyclerView);
        spinner = findViewById(R.id.spinner);
        spinnerSort = findViewById(R.id.spinnerSort);
        bt_delete = findViewById(R.id.buttonDelete);
        bt_decline = findViewById(R.id.buttonDecline2);
        bt_logout = findViewById(R.id.buttonLogout);
        bt_refresh = findViewById(R.id.buttonRefresh);
        bt_newMsg = findViewById(R.id.buttonNewMessage);
        tv_delete = findViewById(R.id.textViewDelete);

        host = UserData.getInstance().host;
        user = UserData.getInstance().user;
        password = UserData.getInstance().password;
        port = UserData.getInstance().port;

        bt_delete.setVisibility(View.INVISIBLE);
        bt_decline.setVisibility(View.INVISIBLE);
        tv_delete.setVisibility(View.INVISIBLE);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(MailboxActivity.this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(MailboxActivity.this,
                DividerItemDecoration.VERTICAL));

        mEmailData = EmailData.getInstance().inbox_messages;
        MailAdapter mMailAdapter = new MailAdapter(MailboxActivity.this, mEmailData);
        mRecyclerView.setAdapter(mMailAdapter);

        bt_logout.setOnClickListener(view -> {
            Intent i = new Intent(MailboxActivity.this, MainActivity.class);
            startActivity(i);
        });
        bt_delete.setOnClickListener(view -> {
            bt_delete.setVisibility(View.INVISIBLE);
            bt_decline.setVisibility(View.INVISIBLE);
            tv_delete.setVisibility(View.INVISIBLE);
            setActive();
            switch(spinner.getSelectedItemPosition()){
                case 0:
                    ConnectionHandler.deleteMessage(lastIndex, "Inbox");
                    EmailData.getInstance().inbox_messages.remove(lastIndex);
                    break;
                case 1:
                    ConnectionHandler.deleteMessage(lastIndex, "[Gmail]/Wysłane");
                    EmailData.getInstance().sent_messages.remove(lastIndex);
                    break;
                case 2:
                    ConnectionHandler.deleteMessage(lastIndex, "[Gmail]/Wersje robocze");
                    EmailData.getInstance().drafts.remove(lastIndex);
                    break;
            }
            mMailAdapter.notifyItemRemoved(lastIndex);
        });
        bt_decline.setOnClickListener(view -> {
            bt_delete.setVisibility(View.INVISIBLE);
            bt_decline.setVisibility(View.INVISIBLE);
            tv_delete.setVisibility(View.INVISIBLE);
            setActive();
        });
        bt_refresh.setOnClickListener(view -> {
            switch(spinner.getSelectedItemPosition()){
                case 0:
                    EmailData.getInstance().inbox_messages = ConnectionHandler.refreshEmailData("Inbox");
                    mRecyclerView.setAdapter(new MailAdapter(MailboxActivity.this, EmailData.getInstance().inbox_messages));
                    break;
                case 1:
                    EmailData.getInstance().sent_messages = ConnectionHandler.refreshEmailData("[Gmail]/Wysłane");
                    mRecyclerView.setAdapter(new MailAdapter(MailboxActivity.this, EmailData.getInstance().sent_messages));
                    break;
                case 2:
                    EmailData.getInstance().drafts = ConnectionHandler.refreshEmailData("[Gmail]/Wersje robocze");
                    mRecyclerView.setAdapter(new MailAdapter(MailboxActivity.this, EmailData.getInstance().drafts));
                    break;
            }
        });
        bt_newMsg.setOnClickListener(view -> {
            Intent i = new Intent(MailboxActivity.this, MailSendActivity.class);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ArrayList<Email> mEmailData = new ArrayList<>();
                ((TextView) parentView.getChildAt(0)).setTextColor(Color.WHITE);
                spinner.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                switch(position){
                    case 0:
                        mEmailData = EmailData.getInstance().inbox_messages;
                        break;
                    case 1:
                        mEmailData = EmailData.getInstance().sent_messages;
                        break;
                    case 2:
                        mEmailData = EmailData.getInstance().drafts;
                        break;
                }
                switch(spinnerSort.getSelectedItemPosition()){
                    case 0:
                        mEmailData.sort(Email.sortByTimeAsc);
                        break;
                    case 1:
                        mEmailData.sort(Email.sortByTimeDsc);
                        break;
                    case 2:
                        mEmailData.sort(Email.sortByMailAsc);
                        break;
                    case 3:
                        mEmailData.sort(Email.sortByMailDsc);
                        break;
                    case 4:
                        mEmailData.sort(Email.sortBySubjectAsc);
                        break;
                    case 5:
                        mEmailData.sort(Email.sortBySubjectDsc);
                        break;

                }
                mRecyclerView.setOnClickListener(null);
                mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent i = new Intent(MailboxActivity.this, MailShowActivity.class);

                        switch(position){
                            case 0:
                                i.putExtra("inbox_index", position);
                                break;
                            case 1:
                                i.putExtra("sent_index", position);
                                break;
                            case 2:
                                i.putExtra("draft_index", position);
                                break;
                        }

                        startActivity(i);
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                        bt_delete.setVisibility(View.VISIBLE);
                        bt_decline.setVisibility(View.VISIBLE);
                        tv_delete.setVisibility(View.VISIBLE);
                        lastIndex = position;
                        setInactive();
                    }
                }));

                MailAdapter mMailAdapter = new MailAdapter(MailboxActivity.this, mEmailData);
                mRecyclerView.setAdapter(mMailAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ArrayList<Email> mEmailData = new ArrayList<>();
                ((TextView) parentView.getChildAt(0)).setTextColor(Color.WHITE);
                spinnerSort.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

                switch (spinner.getSelectedItemPosition()){
                    case 0:
                        mEmailData = EmailData.getInstance().inbox_messages;
                        break;
                    case 1:
                        mEmailData = EmailData.getInstance().sent_messages;
                        break;
                    case 2:
                        mEmailData = EmailData.getInstance().drafts;
                        break;
                }
                switch(position){
                    case 0:
                        mEmailData.sort(Email.sortByTimeAsc);
                        break;
                    case 1:
                        mEmailData.sort(Email.sortByTimeDsc);
                        break;
                    case 2:
                        mEmailData.sort(Email.sortByMailAsc);
                        break;
                    case 3:
                        mEmailData.sort(Email.sortByMailDsc);
                        break;
                    case 4:
                        mEmailData.sort(Email.sortBySubjectAsc);
                        break;
                    case 5:
                        mEmailData.sort(Email.sortBySubjectDsc);
                        break;

                }


                mRecyclerView.setOnClickListener(null);
                mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent i = new Intent(MailboxActivity.this, MailShowActivity.class);
                        switch(spinner.getSelectedItemPosition()){
                            case 0:
                                i.putExtra("inbox_index", position);
                                break;
                            case 1:
                                i.putExtra("sent_index", position);
                                break;
                            case 2:
                                i.putExtra("draft_index", position);
                                break;
                        }
                        startActivity(i);
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                        bt_delete.setVisibility(View.VISIBLE);
                        bt_decline.setVisibility(View.VISIBLE);
                        tv_delete.setVisibility(View.VISIBLE);
                        lastIndex = position;
                        setInactive();
                    }
                }));

                MailAdapter mMailAdapter = new MailAdapter(MailboxActivity.this, mEmailData);
                mRecyclerView.setAdapter(mMailAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    void setInactive(){
        spinner.setEnabled(false);
        spinnerSort.setEnabled(false);
        bt_logout.setEnabled(false);
        bt_refresh.setEnabled(false);
        bt_newMsg.setEnabled(false);
        mRecyclerView.setAdapter(null);
    }

    void setActive(){
        spinner.setEnabled(true);
        spinnerSort.setEnabled(true);
        bt_logout.setEnabled(true);
        bt_refresh.setEnabled(true);
        bt_newMsg.setEnabled(true);
        MailAdapter mMailAdapter = new MailAdapter(MailboxActivity.this, mEmailData);
        mRecyclerView.setAdapter(mMailAdapter);
    }
}