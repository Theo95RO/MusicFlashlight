package com.gmail.btheo95.musicflashlight;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    public boolean isRunning = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                int initialColor;
                int finalColor;
                if (isRunning) {
                    isRunning = false;
                    initialColor = ContextCompat.getColor(MainActivity.this, R.color.primary);
                    finalColor = ContextCompat.getColor(MainActivity.this, R.color.accent);
                }
                else {
                    isRunning = true;
                    initialColor = ContextCompat.getColor(MainActivity.this, R.color.accent);
                    finalColor = ContextCompat.getColor(MainActivity.this, R.color.primary);
                }

                final ObjectAnimator animator = ObjectAnimator.ofInt(fab, "backgroundTint", initialColor, finalColor);
//                animator.setDuration(2000L);
                animator.setEvaluator(new ArgbEvaluator());
                animator.setInterpolator(new DecelerateInterpolator(1));
                animator.addUpdateListener(new ObjectAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int animatedValue = (int) animation.getAnimatedValue();
                        fab.setBackgroundTintList(ColorStateList.valueOf(animatedValue));
                    }
                });
                animator.start();
            }
        });



    }
}
