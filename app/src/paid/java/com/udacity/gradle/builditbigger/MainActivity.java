package com.udacity.gradle.builditbigger;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.sabkayar.praveen.joketellinglib.Joke;
import com.sabkayar.praveen.showjokeandroidlib.JokeShowingActivity;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import java.io.IOException;
import java.lang.ref.WeakReference;


public class MainActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        //1. Create InterstitialAd Object
        mInterstitialAd=new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        //2. Request an Ad
        mInterstitialAd.loadAd(adRequest);

        //3. Wait until the right movement
        //Here my case when button is clicked


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view) {
        Toast.makeText(this, new Joke().getJoke(), Toast.LENGTH_SHORT).show();
    }

    public void passJoke(View view) {
        startActivity(JokeShowingActivity.newIntent(this, new Joke().getJoke()));
    }

    public void passJokeFromGCE(View view) {
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
        new EndpointsAsyncTask(this).execute(this);
    }

    static class EndpointsAsyncTask extends AsyncTask<Context, Void, String> {
        WeakReference<Context> mContextWeakReference;
        WeakReference<ProgressBar> mProgressBarWeakReference;
        EndpointsAsyncTask(Context context){
            mContextWeakReference=new WeakReference<>(context);
            mProgressBarWeakReference=new WeakReference<>((ProgressBar) ((AppCompatActivity)mContextWeakReference.get()).findViewById(R.id.progressBar));
        }

        private static MyApi myApiService = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBarWeakReference.get().setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Context... params) {
            if (myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver

                myApiService = builder.build();
            }

            try {
                return myApiService.tellJoke().execute().getData();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressBarWeakReference.get().setVisibility(View.GONE);
            Toast.makeText(mContextWeakReference.get(), result, Toast.LENGTH_LONG).show();
            mContextWeakReference.get().startActivity(JokeShowingActivity.newIntent(mContextWeakReference.get(), result));
        }
    }
}
