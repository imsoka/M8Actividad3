package com.example.m8actividad3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    static final int INCREASE_SECONDS = 10;
    static final int DECREASE_SECONDS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = new MediaPlayer();
        setContentView(R.layout.activity_main);
        String[] modes = {
                "Musica Raw Directo",
                "Musica URI Raw",
                "Video"
        };

        ArrayAdapter<String> modesAdapter = new ArrayAdapter<String>(
                getApplicationContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                modes
        );
        Spinner modesSpinner = findViewById(R.id.spModes);
        Button butPlay = findViewById(R.id.butPlay);
        Button butPause = findViewById(R.id.butPause);
        Button butStop = findViewById(R.id.butStop);
        Button butDecrease = findViewById(R.id.butDecrease);
        Button butIncrease = findViewById(R.id.butIncrease);
        VideoView videoView = findViewById(R.id.videoView);

        modesSpinner.setAdapter(modesAdapter);

        modesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = modesSpinner.getSelectedItem().toString();
                if(mediaPlayer.isPlaying()) mediaPlayer.stop();
                if(videoView.isPlaying()) mediaPlayer.stop();

                if(selectedItem.equals(modes[0])) {
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.pokemon);
                    mediaPlayer.start();
                    setPlayingStatus();

                    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pokemon);
                    extractMetada(uri);

                }

                if(selectedItem.equals(modes[1])) {
                    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pokemon);
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        mediaPlayer.setDataSource(getApplicationContext(), uri);
                        mediaPlayer.prepare();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                    mediaPlayer.start();
                    setPlayingStatus();
                    extractMetada(uri);

                    TextView tvMetadata = findViewById(R.id.tvDuration);
                    MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                    metadataRetriever.setDataSource(getApplicationContext(), uri);
                    String duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    tvMetadata.setText("Duracion: " + duration + " Milisegundos");
                }

                if(selectedItem.equals(modes[2])) {
                    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
                    videoView.setVideoURI(uri);
                    videoView.start();
                    setPlayingStatus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        butPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                if(!videoView.isPlaying()) {
                    videoView.start();
                }
                if(mediaPlayer.isPlaying() || videoView.isPlaying()) {
                    setPlayingStatus();
                }
            }
        });

        butPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                if(videoView.isPlaying()) {
                    videoView.pause();
                }
                if(!mediaPlayer.isPlaying() || !videoView.isPlaying()) {
                    setPauseStatus();
                }
            }
        });

        butStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                videoView.stopPlayback();
                setStopStatus();
            }
        });

        butIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(modesSpinner.getSelectedItemPosition() < 2) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int newPosition = currentPosition + INCREASE_SECONDS * 1000;

                    if(newPosition <= mediaPlayer.getDuration()) {
                        mediaPlayer.seekTo(newPosition);
                    }
                }

                if(modesSpinner.getSelectedItemPosition() == 2 && videoView.canSeekForward()){
                    int currentPosition = videoView.getCurrentPosition();
                    int newPosition = currentPosition + INCREASE_SECONDS * 1000;
                    if(newPosition <= videoView.getDuration()) {
                        videoView.seekTo(newPosition);
                    }
                }
            }
        });

        butDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(modesSpinner.getSelectedItemPosition() < 2) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int newPosition = currentPosition - DECREASE_SECONDS * 1000;

                    if(newPosition >= 0) {
                        mediaPlayer.seekTo(newPosition);
                        return;
                    }

                    mediaPlayer.seekTo(0);
                }

                if(modesSpinner.getSelectedItemPosition() == 2 && videoView.canSeekForward()){
                    int currentPosition = videoView.getCurrentPosition();
                    int newPosition = currentPosition - DECREASE_SECONDS * 1000;
                    if(newPosition >= 0) {
                        videoView.seekTo(newPosition);
                        return;
                    }

                    videoView.seekTo(0);
                }
            }
        });
    }

    private void setPlayingStatus() {
        TextView tvStatus = findViewById(R.id.tvStatus);
        tvStatus.setBackgroundColor(Color.GREEN);
        tvStatus.setText("Playing");
    }

    private void setPauseStatus() {
        TextView tvStatus = findViewById(R.id.tvStatus);
        tvStatus.setBackgroundColor(Color.YELLOW);
        tvStatus.setText("Paused");
    }

    private void setStopStatus() {
        TextView tvStatus = findViewById(R.id.tvStatus);
        tvStatus.setBackgroundColor(Color.RED);
        tvStatus.setText("Stopped");
    }

    private void extractMetada(Uri uri) {
        TextView tvDuration = findViewById(R.id.tvDuration);
        TextView tvBitrate = findViewById(R.id.tvBitrate);
        TextView tvMimetype = findViewById(R.id.tvMimetype);

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(getApplicationContext(), uri);
        String duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String bitrate = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
        String fileType = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);

        tvDuration.setText("Duration: " + duration + " Miliseconds");
        tvBitrate.setText("Bitrate: " + bitrate);
        tvMimetype.setText("Mimetype: " + fileType);
    }

}