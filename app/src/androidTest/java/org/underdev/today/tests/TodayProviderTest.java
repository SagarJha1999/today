package org.underdev.today.tests;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

import org.underdev.today.provider.TodayContract;
import org.underdev.today.provider.TodayDatabase;
import org.underdev.today.provider.TodayProvider;

/**
 * Created by defer on 17/03/14.
 */
public class TodayProviderTest extends ProviderTestCase2<TodayProvider> {
    public static final String PROVIDER_AUTHORITY = "org.underdev.today.provider";

    public TodayProviderTest() {
        super(TodayProvider.class, PROVIDER_AUTHORITY);
    }

    public void testInsertion() {
        ContentValues values = new ContentValues();
        values.put(TodayDatabase.Goals.GOAL_ID, "uuid");
        values.put(TodayDatabase.Goals.GOAL_NAME, "Make a contribution every day.");

        Uri inserted = getMockContentResolver().insert(TodayContract.Goals.CONTENT_URI, values);

        Cursor result = getMockContentResolver().query(inserted, null, null, null, null);
        assertEquals("The number of records matches", 1, result.getCount());

        result.moveToNext();
        assertEquals("The record matches", "uuid", result.getString(result.getColumnIndex(TodayDatabase.Goals.GOAL_ID)));
        assertEquals("The record matches", "Make a contribution every day.", result.getString(result.getColumnIndex(TodayDatabase.Goals.GOAL_NAME)));
    }

    public void testInsertMany() {
        for (int i = 0; i < 20; i++) {
            ContentValues values = new ContentValues();
            values.put(TodayDatabase.Goals.GOAL_ID, "" + i);
            values.put(TodayDatabase.Goals.GOAL_NAME, "Goal " + i);

           getMockContentResolver().insert(TodayContract.Goals.CONTENT_URI, values);
        }

        Cursor result = getMockContentResolver().query(TodayContract.Goals.CONTENT_URI, null, null, null, null);
        assertEquals("The number of records matches", 20, result.getCount());

        for (int i = 0; i < 20; i++) {
            result.moveToNext();
            assertEquals("The record matches", "" + i, result.getString(result.getColumnIndex(TodayDatabase.Goals.GOAL_ID)));
            assertEquals("The record matches", "Goal " + i, result.getString(result.getColumnIndex(TodayDatabase.Goals.GOAL_NAME)));
        }
    }

    public void testUpdate() {
        for (int i = 0; i < 5; i++) {
            ContentValues values = new ContentValues();
            values.put(TodayDatabase.Goals.GOAL_ID, "" + i);
            values.put(TodayDatabase.Goals.GOAL_NAME, "Goal " + i);

            getMockContentResolver().insert(TodayContract.Goals.CONTENT_URI, values);
        }


        ContentValues values = new ContentValues();
        values.put(TodayDatabase.Goals.GOAL_NAME, "Special Goal");
        int updated = getMockContentResolver().update(TodayContract.Goals.buildGoalUri("3"), values, null, null);

        assertEquals(1, updated);

        Cursor result = getMockContentResolver().query(TodayContract.Goals.CONTENT_URI, null, null, null, null);
        for (int i = 0; i < 5; i++) {
            result.moveToNext();
            assertEquals("The record matches", "" + i, result.getString(result.getColumnIndex(TodayDatabase.Goals.GOAL_ID)));
            if (i == 3) {
                assertEquals("The record matches", "Special Goal", result.getString(result.getColumnIndex(TodayDatabase.Goals.GOAL_NAME)));
            } else {
                assertEquals("The record matches", "Goal " + i, result.getString(result.getColumnIndex(TodayDatabase.Goals.GOAL_NAME)));
            }

        }
    }


    public void testDelete() {
        for (int i = 0; i < 5; i++) {
            ContentValues values = new ContentValues();
            values.put(TodayDatabase.Goals.GOAL_ID, "" + i);
            values.put(TodayDatabase.Goals.GOAL_NAME, "Goal " + i);

            getMockContentResolver().insert(TodayContract.Goals.CONTENT_URI, values);
        }

        int deleted = getMockContentResolver().delete(TodayContract.Goals.buildGoalUri("3"), null, null);
        assertEquals("The element is deleted", 1, deleted);

        int deletedNonExistant = getMockContentResolver().delete(TodayContract.Goals.buildGoalUri("3"), null, null);
        assertEquals("A non-existing element is not deleted", 0, deletedNonExistant);

        int deletedAll = getMockContentResolver().delete(TodayContract.Goals.CONTENT_URI, null, null);
        assertEquals("All elements are deleted", 4, deletedAll);
    }
}
