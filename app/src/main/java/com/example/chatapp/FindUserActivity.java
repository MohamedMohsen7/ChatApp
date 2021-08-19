package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindUserActivity extends AppCompatActivity {

    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListManager;


    //contactList fetches the contacts
    //userList updates the recycler view with only the users that have the app.
    ArrayList<UserObject> userList, contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        contactList = new ArrayList<>();
        userList = new ArrayList<>();
        initializeRecyclerView();
        getContactList();
    }

    //Displays the contacts on the phone
    private void getContactList() {

        String ISOPrefix = getCountryISO();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");

            //Condition that adds the ISO of the country if the phone number has no ISO before it.
            if (!String.valueOf(phone.charAt(0)).equals("+")) {
                phone = ISOPrefix + phone;
            }

            UserObject mContact = new UserObject(name, phone);
            contactList.add(mContact);
            getUserDetails(mContact);
        }
    }

    //Fetches the data from the database to make sure it exists
    private void getUserDetails(UserObject mContact) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = mUserDB.orderByChild("phone").equalTo(mContact.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String phone = "", name = "";

                    //Query might return alot of users and this for loop only gets the users info
                    //Even if we have only 1 user, this for loop should be implemented because it points to a strange place in the DB
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.child("phone").getValue() != null) {
                            phone = childSnapshot.child("phone").getValue().toString();
                        }
                        if (childSnapshot.child("name").getValue() != null) {
                            name = childSnapshot.child("name").getValue().toString();
                        }


                        UserObject mUser = new UserObject(name, phone);
                        //Displays name of the contact as saved on the phone
                        if (name.equals(phone)) {
                            for (UserObject mContactIterator : contactList) {
                                if (mContactIterator.getPhone().equals(mUser.getPhone())) {
                                    mUser.setName(mContactIterator.getName());
                                }
                            }
                        }
                        userList.add(mUser);
                        mUserListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private String getCountryISO() {
        String iso = null;

        //Contains phone's ISO
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso().toString() != null) {
            if (!telephonyManager.getNetworkCountryIso().toString().equals("")) {
                iso = telephonyManager.getNetworkCountryIso().toString();
            }
        }
        return CountryToPhonePrefix.getPhone(iso);
    }

    private void initializeRecyclerView() {
        mUserList = findViewById(R.id.userList);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        mUserListManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mUserList.setLayoutManager(mUserListManager);
        mUserListAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserListAdapter);
    }
}