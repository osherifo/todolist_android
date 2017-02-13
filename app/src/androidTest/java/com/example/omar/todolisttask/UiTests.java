package com.example.omar.todolisttask;

import android.support.test.espresso.ViewAssertion;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
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
    public void initialTest() {
        onView(withId(R.id.add_todo_text))
                .check(matches(withHint("Add a todo")));
    }
    @Test
    public void listTest(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onData(hasToString(startsWith("ui tests"))).inAdapterView(withId(R.id.finished_todos_list)).check(matches(isDisplayed()));

    }

    @Test
    public void addItemTest(){
        String test="Test Item 4";
        onView(withId(R.id.add_todo_text)).perform(typeText(test));
        onView(withId(R.id.add_todo_button)).perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onData(hasToString(startsWith(test))).inAdapterView(withId(R.id.todos_list)).check(matches(isDisplayed()));
//        onData(hasToString(startsWith(test))).inAdapterView(withId(R.id.finished_todos_list)).check(matches(not(isDisplayed())));
    }

    @Test
    public void checkItemTest(){
        String test="Test Item 2";
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onData(hasToString(startsWith(test))).inAdapterView(withId(R.id.todos_list)).perform(click());
        onData(hasToString(startsWith(test))).inAdapterView(withId(R.id.finished_todos_list)).check(matches(isDisplayed()));
      }

    @Test
    public void checkItemTestTEST(){
        String test="Test Item 4";
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(allOf(hasSibling(withText(test)),withId(R.id.todo_checkbox))
        ).perform(click());
    }

    @Test
    public void ischeckItemTest(){
        String test="Test Item 4";
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(allOf(hasSibling(withText(test)),withId(R.id.todo_checkbox))
        ).check(matches(not(isChecked())));
    }


}
