package com.example.qrhunter;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ScanTest {
    /**
     * Test class for RankActivity. All the UI tests are written here. Robotium test framework is
     used
     */
    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * test search by code's scan
     */
    @Test
    public void testSearchCode() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("Search By code");
        solo.assertCurrentActivity("Wrong Activity", ScanActivity.class);
    }

    /**
     * test play's scan
     */
    @Test
    public void testPlay(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("Play");
        solo.assertCurrentActivity("Wrong Activity", ScanActivity.class);
    }


    /**
     * test sign in 's scan
     */

    @Test
    public void testSignin(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("Sign Out");
        solo.clickOnText("Confirm");
        solo.assertCurrentActivity("Wrong Activity",SigninActivity.class);
        solo.clickOnText("Sign in by QR code");
        solo.assertCurrentActivity("Wrong Activity", ScanActivity.class);
    }









    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }

}
