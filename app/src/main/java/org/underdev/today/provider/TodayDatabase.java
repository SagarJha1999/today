package org.underdev.today.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by defer on 17/03/14.
 */
public class TodayDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "schedule.db";

    private static final int DATABASE_VERSION_INITIAL = 1;

    private static final int DATABASE_VERSION = DATABASE_VERSION_INITIAL;

    interface Tables {
        String GOALS = "goals";
        String TODAY = "today";
        String TODAY_GOALS = "today_goals";
    }

    public interface Goals {
        String GOAL_ID   = "goal_id";
        String GOAL_NAME = "goal_name";
    }

    public TodayDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format(
                "CREATE TABLE %s (" +
                "%s TEXT PRIMARY KEY," +
                "%s TEXT NOT NULL," +
                "UNIQUE (%s) ON CONFLICT REPLACE)",
                Tables.GOALS,
                Goals.GOAL_ID,
                Goals.GOAL_NAME,
                Goals.GOAL_ID
        ));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int currentVersion = oldVersion;

        // This intentionally cascades to provide multi-version updates
        switch (currentVersion) {
            case DATABASE_VERSION_INITIAL:
                // no migration needed
        }

        if (currentVersion != newVersion) {
            db.execSQL(String.format("DROP TABLE IF EXISTS %s", Tables.GOALS));
        }

        onCreate(db);
    }
}
