package org.underdev.today.tests;

import android.content.ContentValues;
import android.net.Uri;

import org.underdev.today.provider.TodayContract;
import org.underdev.today.provider.TodayDatabase;
import org.underdev.today.provider.TodayProvider;

import java.util.UUID;

/**
 * Tests the content Uri described in {@link org.underdev.today.provider.TodayContract.Goals}.
 */
public class TodayCrudProviderTestCase extends CrudProviderTestCase<TodayProvider> {

    public TodayCrudProviderTestCase() {
        super(TodayProvider.class, TodayContract.CONTENT_AUTHORITY);
    }

    @Override
    protected Uri getContentUri() {
        return TodayContract.Goals.CONTENT_URI;
    }

    @Override
    protected Uri getItemUri(String id) {
        return TodayContract.Goals.buildGoalUri(id);
    }

    @Override
    protected ContentValues getRandomContent() {
        ContentValues values = new ContentValues();
        values.put(TodayDatabase.Goals.GOAL_ID, UUID.randomUUID().toString());
        values.put(TodayDatabase.Goals.GOAL_NAME, UUID.randomUUID().toString());

        return values;
    }

    @Override
    protected ContentValues getRandomUpdateContent() {
        ContentValues values = getRandomContent();
        values.remove(TodayDatabase.Goals.GOAL_ID);

        return values;
    }

    @Override
    protected Uri itemUriForValues(ContentValues values) {
        return TodayContract.Goals.buildGoalUri(values.getAsString(TodayDatabase.Goals.GOAL_ID));
    }
}
