package com.udacity.gradle.builditbigger.testing;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.runner.AndroidJUnitRunner;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.sabkayar.praveen.showjokeandroidlib.JokeShowingActivity;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AsyncAndroidTest {

    /**
     * This demonstrates how to test AsyncTasks in android JUnit. Below I used
     * an in line implementation of a asyncTask, but in real life you would want
     * to replace that with some task in your application.
     * @throws Throwable
     */
    @Test
    public void testSomeAsynTask () throws Throwable {
        // create  a signal to let us know when our task is done.
        final CountDownLatch signal = new CountDownLatch(1);


        final MyApi[] myApiService = {null};

        /* Just create an in line implementation of an asynctask. Note this
         * would normally not be done, and is just here for completeness.
         * You would just use the task you want to unit test in your project.
         */
        final AsyncTask<Void, Void, String> myTask = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                if (myApiService[0] == null) {  // Only do this once
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

                    myApiService[0] = builder.build();
                }


                try {
                    return myApiService[0].tellJoke().execute().getData();
                } catch (IOException e) {
                    return e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                /* This is the key, normally you would use some type of listener
                 * to notify your activity that the async call was finished.
                 *
                 * In your test method you would subscribe to that and signal
                 * from there instead.
                 */
                // The task is done, and now you can assert some things!
                assertNotNull("Passed Test", result);

                signal.countDown();
            }
        };

        myTask.execute();

        /* The testing thread will wait here until the UI thread releases it
         * above with the countDown() or 30 seconds passes and it times out.
         */
        signal.await(30, TimeUnit.SECONDS);

        // The task is done, and now you can assert some things!
        assertTrue("Task Completed", true);
    }

   /* static class EndpointsAsyncTask extends AsyncTask<Context, Void, String> {
        private static MyApi myApiService = null;
        private Context context;

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
            context = params[0];

            try {
                return myApiService.tellJoke().execute().getData();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            context.startActivity(JokeShowingActivity.newIntent(context, result));
        }
    }*/


}
