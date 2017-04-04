package com.example.chen.ex6;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Chen on 2016/11/8.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button play, stop, quit;
    private ImageView imageView;
    private TextView current, total, state;
    private SeekBar seekBar;
    private  boolean isStop;

    private MusicService musicService;

    private int rotateAngle;
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        isStop = true;
        rotateAngle = 0;
        FindViews();
        BindLisetners();
        ConnectService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MyBinder)service).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };

    private void FindViews() {
        play = (Button) findViewById(R.id.play);
        stop = (Button) findViewById(R.id.stop);
        quit = (Button) findViewById(R.id.quit);
        imageView = (ImageView) findViewById(R.id.cover);
        current = (TextView) findViewById(R.id.current);
        total = (TextView) findViewById(R.id.total);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        state = (TextView) findViewById(R.id.state);
    }

    private void BindLisetners() {
        play.setOnClickListener(this);
        stop.setOnClickListener(this);
        quit.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                return;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                return;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (musicService == null) return;
                int targetPosition = seekBar.getProgress();
                musicService.seekTo(targetPosition);
            }
        });
    }

    private void ConnectService() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void UpdateUI() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                rotateAngle = (musicService.getCurrentPosition()/100)%360;
                imageView.setRotation(rotateAngle);
                current.setText(time.format(musicService.getCurrentPosition()));
                seekBar.setProgress(musicService.getCurrentPosition());
                handler.postDelayed(this, 50);
            }
        };
        handler.post(runnable);
    }
    @Override
    public void onClick(View v) {
        if (musicService == null) return;
        String currentState = state.getText().toString();
        switch (v.getId()) {
            case R.id.play:
                musicService.play();
               if (currentState.equals("IDLE")) {
                   imageView.setRotation(0);
                   seekBar.setMax(musicService.getDuration());
                   total.setText(time.format(musicService.getDuration()));
                   UpdateUI();
               }
                if (currentState.equals("IDLE") || currentState.equals("Pause") ||
                        currentState.toString().equals("Stop"))  {
                    state.setText("Playing");
                } else {
                    state.setText("Pause");
                }
                break;
            case R.id.stop:
                musicService.stop();
                state.setText("Stop");
                break;
            case R.id.quit:
                musicService.release();
                this.finish();
                break;
        }
    }
}
