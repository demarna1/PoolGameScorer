package noah.poolgamescorer.averagefinish;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class AFContentProvider extends ContentProvider {

    public static final int DB_VERSION = 1;

    // AFGame table constants
    public static final String AF_GAME_TABLE = "afGame";
    public static final String ID = "_id";
    public static final String ROUND = "round";
    public static final String SEND_TEXTS = "sendTexts";

    // Player table constants
    public static final String PLAYER_TABLE = "afPlayer";
    public static final String PLAYER_ID = "_id";
    public static final String PLAYER_NAME = "name";
    public static final String PLAYER_NUMBER = "number";
    public static final String PLAYER_TOTAL = "total";
    public static final String PLAYER_LAST = "last";
    public static final String PLAYER_BALLS = "balls";
    public static final String PLAYER_GAME_ID = "gameId";

    // Database URI constants
    public static final int AF_GAMES = 1;
    public static final int AF_GAME = 2;
    public static final String AUTHORITY = "noah.poolgamescorer.averagefinish";
    public static final String BASE_PATH = AF_GAME_TABLE;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.af_game";
    public static final String CONTENT_DIR_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.af_game";
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH, AF_GAMES);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/#", AF_GAME);
    }

    private SQLiteDatabase database;

    private static class OpenHelper extends SQLiteOpenHelper {
        public OpenHelper(Context context) {
            super(context, "AF_GAME", null, DB_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            // Create the database
            try {
                db.beginTransaction();
                String sql = String.format("create table %s (%s integer primary key autoincrement, " +
                                "%s integer, %s integer)", AF_GAME_TABLE, ID, ROUND, SEND_TEXTS);
                db.execSQL(sql);
                sql = String.format("create table %s (%s integer primary key autoincrement, " +
                                "%s text, %s text, %s integer, %s integer, %s text, %s integer",
                                PLAYER_TABLE, PLAYER_ID, PLAYER_NAME, PLAYER_NUMBER, PLAYER_TOTAL,
                                PLAYER_LAST, PLAYER_BALLS, PLAYER_GAME_ID);
                db.execSQL(sql);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // No current plans to upgrade the database between versions.
        }
    }

    @Override
    public boolean onCreate() {
        database = new OpenHelper(getContext()).getWritableDatabase();
        return true; // Database successfully opened
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor c;
        switch (URI_MATCHER.match(uri)) {
            case AF_GAMES:
                c = database.query(AF_GAME_TABLE, projection, selection,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            case AF_GAME:
                String id = uri.getLastPathSegment();
                c = database.query(AF_GAME_TABLE, projection, ID+"=?",
                        new String[] {id}, null, null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            default:
                return null;
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case AF_GAMES:
                return CONTENT_DIR_TYPE;
            case AF_GAME:
                return CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(AF_GAME_TABLE, null, values);
        Uri insertedUri = Uri.withAppendedPath(CONTENT_URI, id+"");
        getContext().getContentResolver().notifyChange(uri, null);
        return insertedUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numDeleted = database.delete(AF_GAME_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return numDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int numUpdated = database.update(AF_GAME_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return numUpdated;
    }
}
