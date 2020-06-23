package ru.csu.ttpapp.mvp;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.csu.ttpapp.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void showTasks() {
        onView(withId(R.id.listView)).check(matches(isDisplayed()));
    }

    @Test
    public void onDialogPositiveClick() {
        onView(withId(R.id.floatingActionButton)).perform(click());
        onView(withId(R.id.clSetting)).check(matches(isDisplayed()));
        onView(withId(R.id.textInputLayoutSetLink)) .check(matches(isDisplayed()));
        onView(withId(R.id.textInputLayoutSetName)).check(matches(isDisplayed()));
        onView(withId(R.id.setLink)).perform(typeText("https://127.0.0.1/"));
        onView(withText(R.string.done)).perform(click());

        onView(withId(R.id.clSetting)).check(doesNotExist());

    }

    @Test
    public void onDialogNegativeClick() {
        onView(withId(R.id.floatingActionButton)).perform(click());
        onView(withId(R.id.clSetting)).check(matches(isDisplayed()));
        onView(withText(R.string.cancel)).perform(click());

        onView(withId(R.id.clSetting)).check(doesNotExist());
    }
}