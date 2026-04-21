package com.example.stateofthewallet;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class SoundManager {
    private static MediaPlayer mediaPlayer;
    
    public static void playSound(Context context, int rawResourceId) {
        try {
            // Stop and release any existing player
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            
            // Create new player and play
            mediaPlayer = MediaPlayer.create(context, rawResourceId);
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                    mediaPlayer = null;
                });
                mediaPlayer.start();
            }
        } catch (Exception e) {
            Log.e("SoundManager", "Error playing sound: " + e.getMessage());
        }
    }
    
    public static void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
