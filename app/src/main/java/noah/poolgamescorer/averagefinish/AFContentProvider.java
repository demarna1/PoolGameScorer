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

import java.sql.SQLException;

public class AFContentProvider extends ContentProvider {

    // Common database constants
    public static final int DB_VERSION = 1;
    public static final String AUTHORITY = "noah.poolgamescorer.averagefinish";

    // AFGame table constants
    public static final String AF_GAME_TABLE = "afGame";
    public static final String AF_GAME_ID = "_id";
    public static final String AF_GAME_ROUND = "round";
    public static final String AF_GAME_SEND_TEXTS = "sendTexts";

    // AFPlayer table constants
    public static final String AF_PLAYER_TABLE = "afPlayer";
    public static final String AF_PLAYER_ID = "_id";
    public static final String AF_PLAYER_NAME = "name";
    public static final String AF_PLAYER_NUMBER = "number";
    public static final String AF_PLAYER_TOTAL = "total";
    public static final String AF_PLAYER_LAST = "last";
    public static final String AF_PLAYER_BALLS = "balls";
    public static final String AF_PLAYER_GAME_ID = "gameId";

    // AFGame URI constants
    public static final int AF_GAMES = 1;
    public static final int AF_GAME = 2;
    public static final Uri AF_GAME_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + AF_GAME_TABLE);
    public static final String AF_GAME_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.af_game";
    public static final String AF_GAME_DIR_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.af_game";

    // AFPlayer URI constants
    public static final int AF_PLAYERS = 3;
    public static final int AF_PLAYER = 4;
    public static final Uri AF_PLAYER_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + AF_PLAYER_TABLE);
    public static final String AF_PLAYER_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.af_player";
    public static final String AF_PLAYER_DIR_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.af_player";

    // URI Matcher
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        URI_MATCHER.addURI(AUTHORITY, AF_GAME_TABLE, AF_GAMES);
        URI_MATCHER.addURI(AUTHORITY, AF_GAME_TABLE + "/#", AF_GAME);
        URI_MATCHER.addURI(AUTHORITY, AF_PLAYER_TABLE, AF_PLAYERS);
        URI_MATCHER.addURI(AUTHORITY, AF_PLAYER_TABLE + "/#", AF_PLAYER);
    }

    // The database
    private SQLiteDatabase database;

    private static class OpenHelper extends SQLiteOpenHelper {
        public OpenHelper(Context context) {
            super(context, "AF_GAME", null, DB_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.beginTransaction();
                String sql = String.format("create table %s (%s integer primary key autoincrement, " +
                                "%s integer, %s integer)", AF_GAME_TABLE, AF_GAME_ID, AF_GAME_ROUND,
                                AF_GAME_SEND_TEXTS);
                db.execSQL(sql);
                sql = String.format("create table %s (%s integer primary key autoincrement, " +
                                "%s text, %s text, %s integer, %s integer, %s text, %s integer)",
                                AF_PLAYER_TABLE, AF_PLAYER_ID, AF_PLAYER_NAME, AF_PLAYER_NUMBER,
                                AF_PLAYER_TOTAL, AF_PLAYER_LAST, AF_PLAYER_BALLS, AF_PLAYER_GAME_ID);
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
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case AF_GAMES:
                return AF_GAME_DIR_TYPE;
            case AF_GAME:
                return AF_GAME_ITEM_TYPE;
            case AF_PLAYERS:
                return AF_PLAYER_DIR_TYPE;
            case AF_PLAYER:
                return AF_PLAYER_ITEM_TYPE;
            default:
                throw new RuntimeException("Failed to get URI type: unknown uri");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor c;
        String id;
        switch (URI_MATCHER.match(uri)) {
            case AF_GAMES:
                c = database.query(AF_GAME_TABLE, projection, selection,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            case AF_GAME:
                id = uri.getLastPathSegment();
                c = database.query(AF_GAME_TABLE, projection, AF_GAME_ID+"=?",
                        new String[] {id}, null, null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            case AF_PLAYERS:
                c = database.query(AF_PLAYER_TABLE, projection, selection,
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            case AF_PLAYER:
                id = uri.getLastPathSegment();
                c = database.query(AF_PLAYER_TABLE, projection, AF_PLAYER_ID+"=?",
                        new String[] {id}, null, null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            default:
                throw new RuntimeException("Failed to query the DB: unknown uri");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id;
        Uri insertedUri = null;
        switch (URI_MATCHER.match(uri)) {
            case AF_GAMES:
            case AF_GAME:
                id = database.insert(AF_GAME_TABLE, null, values);
                insertedUri = Uri.withAppendedPath(AF_GAME_CONTENT_URI, id+"");
                getContext().getContentResolver().notifyChange(uri, null);
                return insertedUri;
            case AF_PLAYERS:
            case AF_PLAYER:
                id = database.insert(AF_PLAYER_TABLE, null, values);
                insertedUri = Uri.withAppendedPath(AF_PLAYER_CONTENT_URI, id+"");
                getContext().getContentResolver().notifyChange(uri, null);
                return insertedUri;
            default:
                throw new RuntimeException("Failed to insert row(s) in DB: unknown uri");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numDeleted;
        switch (URI_MATCHER.match(uri)) {
            case AF_GAMES:
            case AF_GAME:
                numDeleted = database.delete(AF_GAME_TABLE, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return numDeleted;
            case AF_PLAYERS:
            case AF_PLAYER:
                numDeleted = database.delete(AF_PLAYER_TABLE, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return numDeleted;
            default:
                throw new RuntimeException("Failed to delete row(s) in DB: unknown uri");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int numUpdated;
        switch (URI_MATCHER.match(uri)) {
            case AF_GAMES:
            case AF_GAME:
                numUpdated = database.update(AF_GAME_TABLE, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return numUpdated;
            case AF_PLAYERS:
            case AF_PLAYER:
                numUpdated = database.update(AF_PLAYER_TABLE, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return numUpdated;
            default:
                throw new RuntimeException("Failed to update row(s) in DB: unknown uri");
        }
    }
}
