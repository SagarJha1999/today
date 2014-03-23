package org.underdev.today.provider.pojo;

import android.content.ContentValues;

import org.underdev.today.provider.TodayDatabase;

import lombok.Data;

/**
 * Created by defer on 23/03/14.
 */
@Data(staticConstructor = "of")
public class Goal {
    private final String id;
    private final String name;

    public static Goal fromContentValues(ContentValues values) {
        String id   = values.getAsString(TodayDatabase.Goals.GOAL_ID);
        String name = values.getAsString(TodayDatabase.Goals.GOAL_NAME);

        return Goal.of(id, name);
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(TodayDatabase.Goals.GOAL_ID, id);
        values.put(TodayDatabase.Goals.GOAL_NAME, name);

        return values;
    }
}
