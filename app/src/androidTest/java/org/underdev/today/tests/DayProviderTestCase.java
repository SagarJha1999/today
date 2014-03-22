package org.underdev.today.tests;

import android.content.ContentValues;
import android.net.Uri;

import org.underdev.today.provider.TodayContract;
import org.underdev.today.provider.TodayDatabase;
import org.underdev.today.provider.TodayProvider;

import java.util.Random;
import java.util.UUID;

/**
 * Tests the content Uri described in {@link org.underdev.today.provider.TodayContract.Goals}.
 */
public class DayProviderTestCase extends CrudProviderTestCase<TodayProvider> {

    public DayProviderTestCase() {
        super(TodayProvider.class, TodayContract.CONTENT_AUTHORITY);
    }

    @Override
    protected Uri getContentUri() {
        return TodayContract.Days.CONTENT_URI;
    }

    @Override
    protected Uri getItemUri(String id) {
        return TodayContract.Goals.buildGoalUri(id);
    }

    @Override
    protected ContentValues getRandomContent() {
        ContentValues values = new ContentValues();
        values.put(TodayDatabase.Days.DAY_ID, UUID.randomUUID().toString());
        values.put(TodayDatabase.Days.DAY_START, new Random().nextLong());

        return values;
    }

    @Override
    protected ContentValues getRandomUpdateContent() {
        ContentValues values = getRandomContent();
        values.remove(TodayDatabase.Days.DAY_ID);

        return values;
    }

    @Override
    protected Uri itemUriForValues(ContentValues values) {
        return TodayContract.Days.buildDaysUri(values.getAsString(TodayDatabase.Days.DAY_ID));
    }
}
