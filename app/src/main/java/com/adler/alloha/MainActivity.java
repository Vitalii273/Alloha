package com.adler.alloha;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class MainActivity extends AppCompatActivity {
    private static int SIGN_IN_CODE = 1;
    private RelativeLayout activity_main;
    private FirebaseListAdapter<Message> adapter;
    private EmojiconEditText emojiconEditText;
    private ImageView emojiBtn, submitBtn;
    private EmojIconActions emojIconActions;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data); //cheking for Auth native in Android Studio
        if (requestCode == SIGN_IN_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(activity_main, "Register is done", Snackbar.LENGTH_LONG).show();
                displayAllMessages();
            } else {
                Snackbar.make(activity_main, "You not authorised", Snackbar.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity_main = findViewById(R.id.activity_main);

        submitBtn = findViewById(R.id.submit_btn);
        emojiBtn = findViewById(R.id.emoji_btn);
        emojiconEditText = findViewById(R.id.text_field);
        emojIconActions = new EmojIconActions(getApplicationContext(), activity_main, emojiconEditText, emojiBtn);
        emojIconActions.ShowEmojIcon();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().push().setValue(new Message(
                        FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                        emojiconEditText.getText().toString()
                ));
                emojiconEditText.setText("");
            }
        });


        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);// user is not authorized
        else
            Snackbar.make(activity_main, "Register is done", Snackbar.LENGTH_LONG).show();

        displayAllMessages();
    }

    private void displayAllMessages() {
        ListView listOfMessages = findViewById(R.id.listOfMessages);


        Query query = FirebaseDatabase.getInstance().getReference()
                .orderByKey();

        FirebaseListOptions<Message> options = new FirebaseListOptions.Builder<Message>()
                .setLayout(R.layout.list_item)
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();


        adapter = new FirebaseListAdapter<Message>(options) {

            @Override
            protected void populateView(@NonNull View v, @NonNull Message model, int position) {
                TextView mess_user, mess_time;
                BubbleTextView mess_text;
                mess_user = v.findViewById(R.id.message_user);
                mess_time = v.findViewById(R.id.message_time);
                mess_text = v.findViewById(R.id.message_text);

                mess_user.setText(model.getUserName());
                mess_text.setText(model.getTextMessage());
                mess_time.setText(DateFormat.format("dd-MM-yyyy HH:mm:ss", model.getMessageTime()));
            }
        };
        listOfMessages.setAdapter(adapter);
    }
}

