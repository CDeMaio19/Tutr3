package com.example.tutr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class DrawActivity extends AppCompatActivity {

    private CanvasView canvas;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        canvas = findViewById(R.id.canvas);
        ImageButton resetButton = findViewById(R.id.reset_button);
        ImageButton abandonButton = findViewById(R.id.abandon_button);
        ImageButton acceptAndSend = findViewById(R.id.accept_and_send);
        resetButton.setOnClickListener(resetClickListener);
        abandonButton.setOnClickListener(abandonClickListener);
        acceptAndSend.setOnClickListener(acceptAndSendClickListener);

    }
    private View.OnClickListener resetClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            canvas.ClearCanvas();
        }
    };
    private View.OnClickListener abandonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };
    private View.OnClickListener acceptAndSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bitmap bitmap = canvas.getBitmapFromView(canvas);
            Bitmap emptyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            //checks if the current bitmap is empty if its not then send image
            if(!bitmap.sameAs(emptyBitmap))
            {
                //converts the current contents of the canvas into a byte array to be sent back to chat fragment
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                byte [] imageBytes = byteArrayOutputStream.toByteArray();
                Intent intent = new Intent();
                intent.putExtra("data",imageBytes);
                setResult(RESULT_OK,intent);
                finish();
            }


        }
    };
}
