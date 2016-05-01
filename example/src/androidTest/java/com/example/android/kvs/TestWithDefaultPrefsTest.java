package com.example.android.kvs;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.kvs.prefs.schemas.TestWithDefaultPrefs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class TestWithDefaultPrefsTest {

    private Context context;
    private TestWithDefaultPrefs prefs;

    @Before
    public void setup() {
        this.context = InstrumentationRegistry.getTargetContext();
        this.prefs = TestWithDefaultPrefs.get(context);
        prefs.clear();
    }

    @Test
    public void getSingletonInstance() {
        {
            TestWithDefaultPrefs prefs1 = new TestWithDefaultPrefs(context);
            TestWithDefaultPrefs prefs2 = new TestWithDefaultPrefs(context);
            assertThat(prefs1).isNotEqualTo(prefs2);
        }
        {
            TestWithDefaultPrefs prefs1 = TestWithDefaultPrefs.get(context);
            TestWithDefaultPrefs prefs2 = TestWithDefaultPrefs.get(context);
            assertThat(prefs1).isEqualTo(prefs2);
        }
    }

    @Test
    public void longWorks() {
        assertThat(prefs.hasLongValue()).isFalse();
        assertThat(prefs.getLongValue()).isEqualTo(99999999999L);
    }

    @Test
    public void stringWorks() {
        assertThat(prefs.hasStringValue()).isFalse();
        assertThat(prefs.getStringValue()).isEqualTo("abc");
    }

    @Test
    public void intWorks() {
        assertThat(prefs.hasIntValue()).isFalse();
        assertThat(prefs.getIntValue()).isEqualTo(-1);
    }

    @Test
    public void floatWorks(){
        assertThat(prefs.hasFloatValue()).isFalse();
        assertThat(prefs.getFloatValue()).isEqualTo(-1.0F);
    }

    @Test
    public void booleanWorks() {
        assertThat(prefs.hasBooleanValue()).isFalse();
        assertThat(prefs.getBooleanValue()).isTrue();
    }

    @Test
    public void stringSetWorks() {
        assertThat(prefs.hasStringSetValue()).isFalse();
    }
}

