package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class EndpointsAsyncTask extends AsyncTask<Context, Void, String> {
    private WeakReference<Context> mContextWeakReference;
    private WeakReference<ProgressBar> mProgressBarWeakReference;
    private AsyncResponse delegate;
    private static MyApi myApiService = null;


    public EndpointsAsyncTask(Context context, AsyncResponse asyncResponse) {
        delegate = asyncResponse;
        mContextWeakReference = new WeakReference<>(context);
        mProgressBarWeakReference = new WeakReference<>((ProgressBar) ((AppCompatActivity) mContextWeakReference.get()).findViewById(R.id.progressBar));
    }


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
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
        mProgressBarWeakReference.get().setVisibility(View.GONE);
    }


    public interface AsyncResponse {
        void processFinish(String output);
    }

}
