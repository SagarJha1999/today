package org.underdev.today.provider;

import android.net.Uri;

/**
 * Created by defer on 17/03/14.
 */
public class TodayContract {
    public static final String CONTENT_AUTHORITY = "org.underdev.today.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_GOAL = "goals";

    public static class Goals {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GOAL).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.today.block";

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.today.block";

        public static Uri buildGoalUri(String id) {
            return CONTENT_URI.buildUpon().appendEncodedPath(id).build();
        }

        public static String getGoalId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
