package com.udacity.gradle.builditbigger.testing;

import com.udacity.gradle.builditbigger.EndpointsAsyncTask;

import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertNotNull;

@RunWith(JUnit4ClassRunner.class)
public class AsyncAndroidTest {
    @Test
    public void testEndpointsAsyncTask() {
        new EndpointsAsyncTask(new EndpointsAsyncTask.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                assertNotNull("Passed Test", output);
            }
        }).execute();





    }
}
