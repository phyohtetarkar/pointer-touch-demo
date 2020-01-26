package com.phyohtet.pointertouchdemo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DrawingView drawingView = new DrawingView(this);
        setContentView(drawingView);

        drawingView.setOnFinalActionListener(direction -> {
            switch (direction) {
                case TOP:
                    Toast.makeText(this, "Top Action", Toast.LENGTH_SHORT).show();
                    break;
                case RIGHT:
                    Toast.makeText(this, "Right Action", Toast.LENGTH_SHORT).show();
                    break;
                case BOTTOM:
                    Toast.makeText(this, "Bottom Action", Toast.LENGTH_SHORT).show();
                    break;
                case LEFT:
                    Toast.makeText(this, "Left Action", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

    }


}
