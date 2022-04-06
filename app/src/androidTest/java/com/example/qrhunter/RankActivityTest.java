package com.example.qrhunter;

import static org.junit.Assert.assertTrue;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class RankActivityTest {

    /**
     * Test class for RankActivity. All the UI tests are written here. Robotium test framework is
     used
     */
    private Solo solo;
    @Rule
    public ActivityTestRule<RankActivity> rule =
            new ActivityTestRule<>(RankActivity.class, true, true);

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
     * Check whether activity correctly switched
     *
     */

    @Test
    public void checkAmount() {
        solo.assertCurrentActivity("Wrong Activity", RankActivity.class);
        solo.clickOnButton("Rank by Amount of Codes"); //Click ADD CITY Button
        solo.assertCurrentActivity("Wrong Activity", RankAmount.class);
        assertTrue(solo.searchText("1234"));
        solo.clickOnButton("Back");
        solo.assertCurrentActivity("Wrong Activity", RankActivity.class);
    }

    /**
     * test sum rank
     */

    @Test
    public void checkSum() {
        solo.clickOnButton("Rank by Sum Scores"); //Click ADD CITY Button
        solo.assertCurrentActivity("Wrong Activity", RankSum.class);
        assertTrue(solo.searchText("1234"));
        solo.clickOnButton("Back");
        solo.assertCurrentActivity("Wrong Activity", RankActivity.class);
    }

    /**
     * test highest
     */

    @Test
    public void checkHighest() {
        solo.clickOnButton("Rank by Highest Score"); //Click ADD CITY Button
        assertTrue(solo.searchText("1234"));
        solo.assertCurrentActivity("Wrong Activity", RankHighest.class);
        solo.clickOnButton("Back");
        solo.assertCurrentActivity("Wrong Activity", RankActivity.class);

    }

    /**
     * test unique
     */

    @Test
    public void checkUnique() {
        solo.clickOnButton("Rank by Unique code"); //Click ADD CITY Button
        solo.assertCurrentActivity("Wrong Activity", RankUnique.class);
        assertTrue(solo.searchText("1234"));
        solo.clickOnButton("Back");
        solo.assertCurrentActivity("Wrong Activity", RankActivity.class);

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
