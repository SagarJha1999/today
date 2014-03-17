package org.underdev.today.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by defer on 17/03/14.
 */
public class TodayProvider extends ContentProvider {
    private TodayDatabase openHelper;

    private static final UriMatcher matcher = makeMatcher();

    private static final int GOALS    = 100;
    private static final int GOALS_ID = 101;

    @Override
    public boolean onCreate() {
        this.openHelper = new TodayDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        int match = matcher.match(uri);
        switch (match) {
            default: {
                return buildSelection(uri, match)
                        .where(selection, selectionArgs)
                        .query(db, projection, sortOrder);
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = matcher.match(uri);
        SQLiteDatabase db = this.openHelper.getWritableDatabase();

        if (db == null) {
            throw new IllegalStateException("Unable to obtain a writable database");
        }

        switch (match) {
            case GOALS:
                db.insertOrThrow(TodayDatabase.Tables.GOALS, null, contentValues);
                notifyChange(uri);
                return TodayContract.Goals.buildGoalUri(contentValues.getAsString(TodayDatabase.Goals.GOAL_ID));
            default:
                throw new UnsupportedOperationException("Unknown insertion URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        SelectionBuilder builder = buildSelection(uri, matcher.match(uri));
        int retVal = builder.where(selection, selectionArgs).delete(db);
        notifyChange(uri);
        return retVal;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        SelectionBuilder builder = buildSelection(uri, matcher.match(uri));
        int retVal = builder.where(selection, selectionArgs).update(db, contentValues);
        notifyChange(uri);
        return retVal;
    }

    @Override
    public String getType(Uri uri) {
        int match = matcher.match(uri);
        switch (match) {
            case GOALS:
                return TodayContract.Goals.CONTENT_TYPE;
            case GOALS_ID:
                return TodayContract.Goals.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    /**
     * Builds a selection for querying a given URI.
     *
     * @param uri The URI.
     * @param match The URI match.
     * @return A {@link org.underdev.today.provider.SelectionBuilder} representing the Uri.
     * @throws java.lang.UnsupportedOperationException If no expanded selection is available for the URI.
     */
    private SelectionBuilder buildSelection(Uri uri, int match) {
        SelectionBuilder builder = new SelectionBuilder();

        switch (match) {
            case GOALS:
                return builder.table(TodayDatabase.Tables.GOALS);
            case GOALS_ID:
                String goalId = TodayContract.Goals.getGoalId(uri);
                return builder.table(TodayDatabase.Tables.GOALS)
                        .where(TodayDatabase.Goals.GOAL_ID + "=?", goalId);
            default:
                throw new UnsupportedOperationException("Unknown URI for selection builder: " + uri);
        }
    }

    /**
     * Notifies observers of changes to a given URI. This can be used to, for instance, transparently
     * update lists served by Cursor loaders.
     *
     * @param uri The URI that changed.
     */
    private void notifyChange(Uri uri) {
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
    }

    /**
     * Creates a matcher for all the URIs supported by the content provider.
     *
     * @return The UriMatcher.
     */
    private static UriMatcher makeMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = TodayContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TodayContract.PATH_GOAL, GOALS);
        matcher.addURI(authority, TodayContract.PATH_GOAL + "/*", GOALS_ID);

        return matcher;
    }
}
