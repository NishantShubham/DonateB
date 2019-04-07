package com.tphkiit.thisis.donateb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ThankYou extends AppCompatActivity {
    TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        Toast.makeText(this, "Data Saved Successfully!", Toast.LENGTH_LONG).show();
        mTextView = (TextView) findViewById(R.id.back);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThankYou.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
