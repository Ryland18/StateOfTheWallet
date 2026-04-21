package com.example.stateofthewallet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConfettiView extends View {
    private List<Particle> particles = new ArrayList<>();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Random random = new Random();
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isAnimating = false;
    
    private static final int[] COLORS = {
        0xFF4CAF50, // Green
        0xFFCF6679, // Red
        0xFFFFD700, // Gold
        0xFF2196F3, // Blue
        0xFFFF9800  // Orange
    };
    
    private static final int NUM_PARTICLES = 60;

    public ConfettiView(Context context) {
        super(context);
    }

    public ConfettiView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void startConfetti() {
        setVisibility(VISIBLE);
        particles.clear();
        isAnimating = true;
        
        // Create particles across the screen width
        int screenWidth = getWidth();
        if (screenWidth <= 0) {
            screenWidth = getResources().getDisplayMetrics().widthPixels;
        }
        
        for (int i = 0; i < NUM_PARTICLES; i++) {
            particles.add(new Particle(
                random.nextInt(screenWidth),
                0,
                COLORS[random.nextInt(COLORS.length)],
                3 + random.nextInt(4) // Size 3-7
            ));
        }
        
        performAnimation();
    }

    private void performAnimation() {
        if (!isAnimating || particles.isEmpty()) {
            isAnimating = false;
            return;
        }
        
        // Update all particles
        int screenHeight = getHeight();
        if (screenHeight <= 0) {
            screenHeight = getResources().getDisplayMetrics().heightPixels;
        }
        
        List<Particle> toRemove = new ArrayList<>();
        for (Particle p : particles) {
            p.update();
            if (p.y > screenHeight) {
                toRemove.add(p);
            }
        }
        particles.removeAll(toRemove);
        
        invalidate();
        
        // Continue animation if particles remain
        if (!particles.isEmpty()) {
            handler.postDelayed(this::performAnimation, 16); // ~60fps
        } else {
            isAnimating = false;
            setVisibility(GONE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        for (Particle p : particles) {
            paint.setColor(p.color);
            canvas.drawCircle(p.x, p.y, p.size, paint);
        }
    }

    private static class Particle {
        float x, y;
        float vx, vy;
        int color;
        float size;

        Particle(int startX, int startY, int color, float size) {
            this.x = startX;
            this.y = startY;
            this.color = color;
            this.size = size;
            
            // Random horizontal velocity
            this.vx = (float) (Math.random() - 0.5) * 3;
            // Falling velocity
            this.vy = 3 + (float) Math.random() * 2;
        }

        void update() {
            x += vx;
            y += vy;
            vy += 0.1f; // Gravity
            vx *= 0.99f; // Air resistance
        }
    }
}

