package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


import com.example.chatapp.Chat.ChatObject;
import com.example.chatapp.Chat.MediaAdapter;
import com.example.chatapp.Chat.MessageAdapter;
import com.example.chatapp.Chat.MessageObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mChat,mMedia;
    private RecyclerView.Adapter mChatAdapter,mMediaAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager,mMediaLayoutManager;

    ArrayList<MessageObject> messageList;
    String chatID;
    DatabaseReference mChatDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatID = getIntent().getExtras().getString("chatID");
        mChatDb = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);
        Button mSend = findViewById(R.id.send);
        Button mAddMedia = findViewById(R.id.addMedia);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }

        });
        mAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        initializeMessage();
        initializeMedia();
        getChatMessages();
    }


    private void getChatMessages() {
        mChatDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    String text = "", creatorID = "";

                    if(dataSnapshot.child("text").getValue() != null){
                        text = dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("creator").getValue() != null){
                        creatorID = dataSnapshot.child("creator").getValue().toString();
                    }
                    MessageObject mMessage = new MessageObject(dataSnapshot.getKey(), creatorID, text);
                    messageList.add(mMessage);
                    mChatLayoutManager.scrollToPosition(messageList.size()-1);
                    mChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void sendMessage() {
        EditText mMessage = findViewById(R.id.message1);
        if (!mMessage.getText().toString().isEmpty()) {
            DatabaseReference newMessageDb = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).push();

            Map newMessageMap = new HashMap<>();
            newMessageMap.put("text", mMessage.getText().toString());
            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());
            newMessageDb.updateChildren(newMessageMap);
        }
        mMessage.setText(null);
    }
    private void initializeMessage() {
        messageList = new ArrayList<>();
        mChat = findViewById(R.id.messagelist);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }





    int PICK_IMAGE_INTENT=1;
    ArrayList<String> mediaUriList=new ArrayList<>();
    private void initializeMedia() {
        messageList = new ArrayList<>();
        mMedia = findViewById(R.id.messagelist);
        mMedia.setNestedScrollingEnabled(false);
        mMedia.setHasFixedSize(false);
        mMediaLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mMedia.setLayoutManager(mMediaLayoutManager);
        mMediaAdapter = new MediaAdapter(getApplicationContext(),mediaUriList);
        mMedia.setAdapter(mMediaAdapter);
    }

    private void openGallery() {
        Intent intent=new Intent();
        //what type of file we want user to enter
        intent.setType("image/*");
        //allow user to pick multiple images at the same time
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        //allows the user to get the content
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture(s)"),PICK_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if(requestCode==PICK_IMAGE_INTENT){
                //used to make sure its one image or multiple images
                if(data.getClipData()==null) {
                    mediaUriList.add(data.getData().toString());
                    //used to get data and convert it to string to add to the media uri string arraylist
                }
                else{
                    for(int i=0;i<data.getClipData().getItemCount();i++){
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }

                mMediaAdapter.notifyDataSetChanged();
            }
        }
    }
}