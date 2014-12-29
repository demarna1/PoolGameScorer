package noah.poolgamescorer.averagefinish;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class AFDatabaseHelper {

    public static AFGame pullGameFromDatabase(Context context, long id) {
        // Get the game with the specified id
        AFGame afGame;
        Uri uri = Uri.withAppendedPath(AFContentProvider.AF_GAME_CONTENT_URI, id+"");
        String[] projection = new String[] {
                AFContentProvider.AF_GAME_ROUND,
                AFContentProvider.AF_GAME_SEND_TEXTS
            };
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    AFContentProvider.AF_GAME_ID + "=?", new String[] {""+id}, null);
            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }
            int numPlayers = cursor.getInt(0);
            boolean sendTexts = cursor.getInt(1) != 0;
            afGame = new AFGame(numPlayers, sendTexts);
            afGame.setId(id);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // Get all players for this game
        uri = AFContentProvider.AF_PLAYER_CONTENT_URI;
        projection = new String[] {
                AFContentProvider.AF_PLAYER_ID,
                AFContentProvider.AF_PLAYER_NAME,
                AFContentProvider.AF_PLAYER_NUMBER,
                AFContentProvider.AF_PLAYER_TOTAL,
                AFContentProvider.AF_PLAYER_LAST,
                AFContentProvider.AF_PLAYER_BALLS
            };
        cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    AFContentProvider.AF_PLAYER_GAME_ID + "=?", new String[] {""+id}, null);
            if (cursor == null || !cursor.moveToFirst()) {
                return afGame;
            }
            do {
                AFPlayer player = new AFPlayer();
                player.setId(cursor.getLong(0));
                player.setName(cursor.getString(1));
                player.setNumber(cursor.getString(2));
                player.setTotal(cursor.getInt(3));
                player.setLast(cursor.getInt(4));
                player.setBallListFromDBString(cursor.getString(5));
                afGame.getPlayerList().add(player);
            } while (cursor.moveToNext());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Log.d("AF", "LOADED: id = " + afGame.getId() +
            ", round = " + afGame.getRound() +
            ", sendTexts = " + afGame.getSendTexts());
        return afGame;
    }

    public static void pushGameToDatabase(Context context, AFGame afGame) {
        // Insert/update the game in the database
        Uri uri = AFContentProvider.AF_GAME_CONTENT_URI;
        ContentValues values = new ContentValues();
        values.put(AFContentProvider.AF_GAME_ROUND, afGame.getRound());
        values.put(AFContentProvider.AF_GAME_SEND_TEXTS, afGame.getSendTexts() ? 1 : 0);
        if (afGame.getId() < 0) {
            Uri insertedUri = context.getContentResolver().insert(uri, values);
            String idString = insertedUri.getLastPathSegment();
            long id = Long.parseLong(idString);
            afGame.setId(id);
        } else {
            context.getContentResolver().update(uri, values, AFContentProvider.AF_GAME_ID+"=?",
                    new String[] {""+afGame.getId()});
        }

        // Insert/update players in the database
        uri = AFContentProvider.AF_PLAYER_CONTENT_URI;
        for (AFPlayer player : afGame.getPlayerList()) {
            values = new ContentValues();
            values.put(AFContentProvider.AF_PLAYER_NAME, player.getName());
            values.put(AFContentProvider.AF_PLAYER_NUMBER, player.getNumber());
            values.put(AFContentProvider.AF_PLAYER_TOTAL, player.getTotal());
            values.put(AFContentProvider.AF_PLAYER_LAST, player.getLast());
            values.put(AFContentProvider.AF_PLAYER_BALLS, player.getDBStringFromBallList());
            values.put(AFContentProvider.AF_PLAYER_GAME_ID, afGame.getId());
            if (player.getId() < 0) {
                Uri insertedUri = context.getContentResolver().insert(uri, values);
                String idString = insertedUri.getLastPathSegment();
                long id = Long.parseLong(idString);
                player.setId(id);
            } else {
                context.getContentResolver().update(uri, values, AFContentProvider.AF_PLAYER_ID+"=?",
                        new String[] {""+player.getId()});
            }
        }

        Log.d("AF", "SAVED: id = " + afGame.getId() +
                ", round = " + afGame.getRound() +
                ", sendTexts = " + afGame.getSendTexts());
    }
}
