package com.gex.micmic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {


    private AudioCapture audioCapture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create a new AudioCapture instance.
        Intent mis = new Intent(this, Svc.class);
        this.startService(mis);
        finish();
        //audioCapture = new AudioCapture();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /* Start capturing audio data and sending it over the WebSocket connection.
        try {
            audioCapture.start();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Stop capturing audio data and close the WebSocket connection.
        /*try {
            audioCapture.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}