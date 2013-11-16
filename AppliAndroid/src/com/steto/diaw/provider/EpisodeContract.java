package com.steto.diaw.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Field and table name constants for
 * {@link com.steto.diaw.provider.EpisodeProvider}.
 */
public class EpisodeContract {
    private EpisodeContract() {
    }

    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = "com.steto.diaw";

    /**
     * Base URI. (content://com.steto.diaw)
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path component for "episode"-type resources..
     */
    private static final String PATH_EPISODES = "episodes";

    /**
     * Columns supported by "episodes" records.
     */
    public static class Episode implements BaseColumns {
        /**
         * MIME type for lists of entries.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.basicsyncadapter.episodes";
        /**
         * MIME type for individual entries.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.basicsyncadapter.episode";

        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EPISODES).build();

        /**
         * Table name where records are stored for "episode" resources.
         */
        public static final String TABLE_NAME = "episode";

        public static final String COLUMN_NAME_SHOW = "showName";
        public static final String COLUMN_NAME_SEASON = "seasonNumber";
        public static final String COLUMN_NAME_EPISODE = "episodeNumber";
        public static final String COLUMN_NAME_UPDATE = "updatedAt";
    }
}