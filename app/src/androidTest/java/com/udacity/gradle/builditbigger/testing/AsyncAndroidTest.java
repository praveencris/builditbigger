package com.udacity.gradle.builditbigger.testing;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.test.internal.runner.junit4.AndroidJUnit4Builder;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.runner.AndroidJUnitRunner;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.sabkayar.praveen.showjokeandroidlib.JokeShowingActivity;
import com.udacity.gradle.builditbigger.EndpointsAsyncTask;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4ClassRunner.class)
public class AsyncAndroidTest {
    @Test
    public void testEndpointsAsyncTask() {
        new EndpointsAsyncTask(InstrumentationRegistry.getInstrumentation().getContext(), new EndpointsAsyncTask.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                assertNotNull("Passed Test", output);
            }
        }).execute();
    }
}
