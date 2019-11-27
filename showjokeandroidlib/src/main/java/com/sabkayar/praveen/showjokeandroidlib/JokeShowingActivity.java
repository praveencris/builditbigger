package com.sabkayar.praveen.showjokeandroidlib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class JokeShowingActivity extends AppCompatActivity {
    private static final String EXTRA_JOKE = "extra_joke" + JokeShowingActivity.class.getSimpleName();

    public static Intent newIntent(Context context, String joke) {
        Intent intent = new Intent(context, JokeShowingActivity.class);
        intent.putExtra(EXTRA_JOKE, joke);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke_showing);

        if (getIntent().getStringExtra(EXTRA_JOKE) != null)
            ((TextView) findViewById(R.id.tv_joke))
                    .setText(getIntent().getStringExtra(EXTRA_JOKE));
        else
            Toast.makeText(this, "No Joke Received", Toast.LENGTH_SHORT).show();
    }
}
