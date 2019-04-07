package com.tphkiit.thisis.donateb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindDonor extends AppCompatActivity {
    String namee,c1,c2,groupp,result;
    String pin,group;
    FirebaseDatabase database;
    ListView listView;
    ArrayAdapter adapter;
    ArrayList<String> listItems = new ArrayList<>();
    TextView mTextView;
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_donor);

        mTextView = (TextView) findViewById(R.id.back);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindDonor.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        Intent intent=getIntent();
        pin=intent.getStringExtra("pin");
        group=intent.getStringExtra("group");
        mProgressDialog = new ProgressDialog(FindDonor.this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();
        listView = (ListView) findViewById(R.id.donorList);
        adapter = new ArrayAdapter(FindDonor.this, android.R.layout.simple_list_item_1,listItems);
        listView.setAdapter(adapter);
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(pin+"/"+group);


        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressDialog.dismiss();
                if (dataSnapshot.exists()){
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        String rawData = snapshot.getValue().toString();
                        Pattern r = Pattern.compile("name=(.*?)end");
                        Matcher m = r.matcher(rawData);


                        while(m.find()){
                            namee =  m.group(1);
                        }

                        r = Pattern.compile("contact1=(.*?)end");
                        m = r.matcher(rawData);

                        while(m.find()){
                            c1 =  m.group(1);
                        }

                        r = Pattern.compile("contact2=(.*?)end");
                        m = r.matcher(rawData);

                        while(m.find()){
                            c2 =  m.group(1);
                        }

                        r = Pattern.compile(", group=(.*?)end");
                        m = r.matcher(rawData);

                        while(m.find()){
                            groupp =  m.group(1);
                        }

                        result="Name:"+namee+"\n"+"Blood Group: "+groupp+"\n"+"Contacts: "+c1+","+c2;

                        listItems.add(result);
                        adapter.notifyDataSetChanged();
                    }
                }
                else{
                    Toast.makeText(FindDonor.this,"Sorry No Donors Available", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FindDonor.this,"Sorry No Donors Available", Toast.LENGTH_LONG).show();
                Log.d("checking","klo--"+databaseError.getCode());
            }
        });
    }
}
