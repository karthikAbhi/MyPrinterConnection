package com.nash.myprinterconnection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    Button mUSBButton, mBTButton, mWIFIButton;
    MyPrinter mPrinter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUSBButton = findViewById(R.id.USBButton);
        mBTButton = findViewById(R.id.BTButton);
        mWIFIButton = findViewById(R.id.WIFIButton);

        mUSBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), USBActivity.class);
                intent.putExtra("MyPrinterInstance", mPrinter);
                startActivity(intent);
            }
        });

        mBTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrinter = new MyPrinter(getApplicationContext(), 2);
            }
        });

        mWIFIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrinter = new MyPrinter(getApplicationContext(), 3);
            }
        });
    }
}
