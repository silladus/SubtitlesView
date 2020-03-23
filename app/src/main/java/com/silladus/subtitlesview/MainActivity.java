package com.silladus.subtitlesview;

import android.os.Bundle;
import android.util.Log;

import com.silladus.subtitles.SubtitlesEditViewContainer;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    SubtitlesEditViewContainer subtitlesEditViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subtitlesEditViewContainer = findViewById(R.id.subtitlesEditViewContainer);

        subtitlesEditViewContainer
                .addItemView("测试文本", 0xFFFFFFFF, "FFFFFF",
                        (container, textView) -> {
                            textView.setText("测试文本");
                            Log.e("onCreate: ", "---------------------");
                        }
                );
    }
}
