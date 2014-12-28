package noah.poolgamescorer.averagefinish;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class AFDatabaseHelper {

    public static AFGame pullGameFromDatabase(Context context, long id) {
        Uri uri = Uri.withAppendedPath(AFContentProvider.CONTENT_URI, id+"");
        String[] projection = {
                AFContentProvider.ID,
                AFContentProvider.ROUND,
                AFContentProvider.SEND_TEXTS,
            };
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, AFContentProvider.ID + "=?",
                    new String[] {""+id}, null);
            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }
            int numPlayers = cursor.getInt(1);
            boolean sendTexts = cursor.getInt(2) != 0;
            AFGame afGame = new AFGame(numPlayers, sendTexts);
            afGame.setId(cursor.getLong(0));
            return afGame;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void pushContactToDatabase(Context context, AFGame afGame) {
        Uri uri = Uri.withAppendedPath(AFContentProvider.CONTENT_URI, ""+afGame.getId());
        ContentValues values = new ContentValues();
        values.put(AFContentProvider.ROUND, afGame.getRound());
        values.put(AFContentProvider.SEND_TEXTS, afGame.getSendTexts());
        if (afGame.getId() < 0) {
            Uri insertedUri = context.getContentResolver().insert(uri, values);
            String idString = insertedUri.getLastPathSegment();
            long id = Long.parseLong(idString);
            afGame.setId(id);
        } else {
            context.getContentResolver().update(uri, values, AFContentProvider.ID+"=?",
                    new String[] {""+afGame.getId()});
        }
    }
}
