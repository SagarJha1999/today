package org.underdev.today.provider.pojo;

import android.content.ContentValues;

import org.underdev.today.provider.TodayDatabase;

import lombok.Data;

/**
 * Created by defer on 23/03/14.
 */
@Data(staticConstructor = "of")
public class Day {
    private final String id;
    private final long start;

    public static Day fromContentValues(ContentValues values) {
        String id = values.getAsString(TodayDatabase.Days.DAY_ID);
        long start  = values.getAsLong(TodayDatabase.Days.DAY_START);

        return Day.of(id, start);
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(TodayDatabase.Days.DAY_ID, id);
        values.put(TodayDatabase.Days.DAY_START, start);

        return values;
    }
}
