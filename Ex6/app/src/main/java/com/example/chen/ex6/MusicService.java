package com.example.chen.ex6;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private boolean isPrepare;

    public final IBinder binder = new MyBinder();
    public class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    public boolean isPlaying() {
        if (mediaPlayer == null) return false;
        return mediaPlayer.isPlaying();
    }
    public void play(){
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.music);
            isPrepare = true;
        }
        if (!isPrepare) {
            try {
                mediaPlayer.prepare();
                isPrepare = true;
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        } else {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPrepare = false;
        }
    }

    public void release() {
        if (mediaPlayer == null) return;
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public int getDuration() {
        if (mediaPlayer == null)
            return 0;
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        if (mediaPlayer == null)
            return 0;
        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int position) {
        if (mediaPlayer == null) return;
        mediaPlayer.seekTo(position);
    }
}
