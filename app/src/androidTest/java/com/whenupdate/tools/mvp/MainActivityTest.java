package com.whenupdate.tools.mvp;

import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.whenupdate.tools.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    private Activity mMainActivity;
    private RecyclerView mRecyclerView;
    private int id_list_view = R.id.listView;
    private int itemCount = 0;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        this.mMainActivity = this.mActivityRule.getActivity();
        this.mRecyclerView = this.mMainActivity.findViewById(this.id_list_view);
        this.itemCount = this.mRecyclerView.getAdapter().getItemCount();
    }

    @Test
    public void showTasks() {
        onView(withId(R.id.listView)).check(matches(isDisplayed()));

        if (this.itemCount > 0) {
            for (int i = 0; i < this.itemCount; i++) {
                /* проверяем, что ViewHolder-ы видны */
                onView(new RecyclerViewMatcher(this.id_list_view)
                        .atPositionOnView(i, R.id.cardView))
                        .check(matches(isDisplayed()));
            }
        }
    }

    @Test
    public void onDialogPositiveClick() {
        onView(withId(R.id.floatingActionButton)).perform(click());
        onView(withId(R.id.clSetting)).check(matches(isDisplayed()));
        onView(withId(R.id.textInputLayoutSetLink)).check(matches(isDisplayed()));
        onView(withId(R.id.textInputLayoutSetName)).check(matches(isDisplayed()));
        onView(withId(R.id.setLink)).perform(typeText("https://127.0.0.1/"));
        onView(withText(R.string.done)).perform(click());

        onView(withId(R.id.clSetting)).check(doesNotExist());
        int newCount = this.mRecyclerView.getAdapter().getItemCount();
        assertThat(newCount, is(itemCount + 1));
    }

    @Test
    public void onDialogNegativeClick() {
        onView(withId(R.id.floatingActionButton)).perform(click());
        onView(withId(R.id.clSetting)).check(matches(isDisplayed()));
        onView(withText(R.string.cancel)).perform(click());

        onView(withId(R.id.clSetting)).check(doesNotExist());
    }

    @Test
    public void remoteTask() {
        if (this.itemCount > 0) {
            onView(new RecyclerViewMatcher(this.id_list_view)
                    .atPositionOnView(this.itemCount - 1, R.id.nameLink))
                    .check(matches(withText("https://127.0.0.1/")));
//            onView(new RecyclerViewMatcher(this.id_list_view)
//                    .atPositionOnView(this.itemCount - 1, R.id.delBtn)).perform(click());
            onView(withText(R.string.text_conf_delete))
                    .check(matches(isDisplayed()));
            onView(withText(R.string.done)).perform(click());

            int newCount = this.mRecyclerView.getAdapter().getItemCount();
            assertThat(newCount, is(itemCount - 1));
        } else onView(withId(R.id.emptyId))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.also_empty)));
    }
}