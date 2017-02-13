package com.example.omar.todolisttask;

import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by omar on 2/12/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UiTests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);



    @Test
    public void addItemTest(){
        String test="Test Item 1";
        onView(withId(R.id.add_todo_text)).perform(typeText(test));
        onView(withId(R.id.add_todo_button)).perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onData(hasToString(startsWith(test))).inAdapterView(withId(R.id.todos_list)).check(matches(isDisplayed()));
        onView(withId(R.id.finished_todos_list))
                .check(matches(not(withAdaptedData(Matchers.<Object>is(test)))));
    }




    @Test
    public void checkIteminDoneTest(){
        String test="Test Item 1";
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(allOf(hasSibling(withText(test)),withId(R.id.todo_checkbox))
        ).perform(click());
        onData(hasToString(startsWith(test))).inAdapterView(withId(R.id.finished_todos_list)).check(matches(isDisplayed()));
        onView(withId(R.id.todos_list))
                .check(matches(not(withAdaptedData(Matchers.<Object>is(test)))));

    }

    @Test
    public void checkIteminUnDoneTest(){
        String test="Test Item 1";
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(allOf(hasSibling(withText(test)),withId(R.id.todo_checkbox))
        ).perform(click());
        onData(hasToString(startsWith(test))).inAdapterView(withId(R.id.todos_list)).check(matches(isDisplayed()));
        onView(withId(R.id.finished_todos_list))
                .check(matches(not(withAdaptedData(Matchers.<Object>is(test)))));

    }

    @Test
    public void searchTest(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String test1="abc";
        onView(withId(R.id.add_todo_text)).perform(typeText(test1));
        onView(withId(R.id.add_todo_button)).perform(click());

        String test2="def";
        onView(withId(R.id.add_todo_text)).perform(typeText(test2));
        onView(withId(R.id.add_todo_button)).perform(click());

        String test3="ghi";
        onView(withId(R.id.add_todo_text)).perform(typeText(test1));
        onView(withId(R.id.add_todo_button)).perform(click());

        String test4="beh";
        onView(withId(R.id.add_todo_text)).perform(typeText(test4));
        onView(withId(R.id.add_todo_button)).perform(click());


        onView(withId(R.id.search_view)).perform(click());
        onView(allOf(isAssignableFrom(AppCompatImageView.class),withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),withContentDescription("Search"))).perform(click());


        onView(isAssignableFrom(AutoCompleteTextView.class)).perform(typeText("e"));



        onData(hasToString(startsWith(test2))).inAdapterView(withId(R.id.todos_list)).check(matches(isDisplayed()));
        onView(withId(R.id.finished_todos_list))
                .check(matches(not(withAdaptedData(Matchers.<Object>is(test2)))));







    }







    private static Matcher<View> withAdaptedData(final Matcher<Object> dataMatcher) {
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("with class name: ");
                dataMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof AdapterView)) {
                    return false;
                }
                @SuppressWarnings("rawtypes")
                Adapter adapter = ((AdapterView) view).getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (dataMatcher.matches(adapter.getItem(i))) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

}
