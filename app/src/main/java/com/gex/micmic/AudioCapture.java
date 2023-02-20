package com.gex.micmic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class AudioCapture {
    private Context context;
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
    private AudioRecord audioRecord;
    private WebSocketClient webSocketClient;
    private boolean running;

    public String socket = "ws://192.168.1.19:8762";
    public AudioCapture(Context context) {
        this.context = context;
    }

    @SuppressLint("MissingPermission")
    public void start() throws Exception {

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);
        audioRecord.startRecording();




        webSocketClient = new WebSocketClient(new URI(socket)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("WebSocket opened: " + handshakedata.getHttpStatusMessage());


            }

            @Override
            public void onMessage(String message) {
                Log.d("Websocket", "onMessage: "+message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("WebSocket closed: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };

        webSocketClient.connect();
        running = true;
        // constantly check if it is connected and reconnect to client
        Thread better_fking_work = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!webSocketClient.isOpen()) {
                    try {
                        webSocketClient.reconnect();
                    } catch (Exception e) {
                        System.out.println(e);
                        try {
                            TimeUnit.SECONDS.sleep(3);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        System.out.println("attempted sleep");
                    }
                }
            }
        });
        better_fking_work.start();

        Thread anotherone_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[BUFFER_SIZE];
                while (running) {
                    try {
                        int read = audioRecord.read(buffer, 0, buffer.length);
                        if (read > 0) {
                            try{
                                Log.d("AudioData", Arrays.toString(buffer));
                                webSocketClient.send(ByteBuffer.wrap(buffer, 0, read));
                            }catch (Exception e){
                                System.out.println(e);
                            }
                        }else {
                            System.out.println("i AM ZERO");
                        }
                    }catch (Exception e){
                        System.out.println(e);
                    }
                }

            }

        });

        anotherone_thread.start();




    }

    public void stop() throws Exception {
        running = false;
        audioRecord.stop();
        audioRecord.release();
        webSocketClient.close();
    }
}
