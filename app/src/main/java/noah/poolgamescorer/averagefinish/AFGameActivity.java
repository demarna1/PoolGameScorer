package noah.poolgamescorer.averagefinish;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import noah.averagefinish.BuildConfig;
import noah.poolgamescorer.main.NewGameDialog;
import noah.averagefinish.R;
import noah.poolgamescorer.main.Utils;

public class AFGameActivity extends Activity {

    public static final int GAME_START = 1;

    private AFGame afGame;
    private ListView afListView;
    private AFPlayerAdapter playerAdapter;
    private TextView roundTextView;
    private NewGameDialog newGameDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afgame);
        ScalePoolTableImage();
        newGameDialog = new NewGameDialog();
        Button newButton = (Button) findViewById(R.id.newButton);
        newButton.setOnClickListener(newListener);
        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(addListener);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        long gameId = preferences.getLong("activeGameId", -1);
        afGame = new AFGame();
        if (gameId >= 0) {
            AFDatabaseHelper.pullGameFromDatabase(this, gameId, afGame);
        }

        // Add players to list view
        afGame.sortPlayerListByTotal();
        afListView = (ListView) findViewById(R.id.afListView);
        playerAdapter = new AFPlayerAdapter(afGame, getLayoutInflater());
        afListView.setAdapter(playerAdapter);

        // Set round
        roundTextView = (TextView)findViewById(R.id.roundText);
        roundTextView.setText("Round " + afGame.getRound());
    }

    private OnClickListener newListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            newGameDialog.show(getFragmentManager(), "m");
        }
    };

    private OnClickListener addListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // Check number of players
            if (afGame.getPlayerCount() == 0)
                return;

            // TODO - Fix for when views are not visible (>9 players). Remove assertion when fixed.
            if (BuildConfig.DEBUG && (afListView.getLastVisiblePosition() -
                    afListView.getFirstVisiblePosition() != afGame.getPlayerCount() - 1)) {
                throw new AssertionError("afListView is acting strange");
            }

            // Check to see that all editTexts are filled
            List<EditText> editTexts = new ArrayList<>();
            for (int pos = afListView.getFirstVisiblePosition();
                    pos <= afListView.getLastVisiblePosition(); pos++) {
                View row = afListView.getChildAt(pos);
                EditText editText = (EditText) row.findViewById(R.id.newEdit);
                if (editText.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please fill in a position for every player.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                editTexts.add(editText);
            }
            StartNextRound(editTexts);
        }
    };

    private void StartNextRound(List<EditText> editTexts) {
        // Next round
        afGame.setRound(afGame.getRound() + 1);
        roundTextView.setText("Round " + afGame.getRound());

        // Update the players
        for (int i = 0; i < afGame.getPlayerCount(); i++) {
            int score = Integer.parseInt(editTexts.get(i).getText().toString());
            AFPlayer player = afGame.getPlayer(i);
            player.setTotal(player.getTotal() + score);
            player.setLast(score);
            player.clearBalls();
            // TODO: Should not be needed
            editTexts.get(i).setText("");
        }

        // Save game to database
        AFDatabaseHelper.pushGameToDatabase(this, afGame);

        // Sort list and update standings
        afGame.sortPlayerListByTotal();
        playerAdapter.notifyDataSetChanged();

        // Send the texts and hide the keyboard
        if (afGame.getSendTexts()) {
            Utils.SendTextMessages(afGame);
        }
        Utils.HideSoftKeyboard(this);
    }

    public void onNewGameStart(int num, boolean sendTexts) {
        // TODO: Offer "keep current game" option
        // TODO: Remove existing game from database if elected to do so
        Intent intent = new Intent(AFGameActivity.this, AFPlayersActivity.class);
        intent.putExtra("numPlayers", num);
        intent.putExtra("sendTexts", sendTexts);
        startActivityForResult(intent, GAME_START);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == GAME_START && resultCode == RESULT_OK) {
            long gameId = intent.getLongExtra("gameId", -1);
            AFDatabaseHelper.pullGameFromDatabase(this, gameId, afGame);
            playerAdapter.notifyDataSetChanged();
            roundTextView.setText("Round " + afGame.getRound());
        }
    }

    private void ScalePoolTableImage() {
        ImageView poolTableImage = (ImageView)findViewById(R.id.poolTableImage);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pooltable);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int desiredWidth = size.x;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleFactor = (float)desiredWidth/(float)width;
        int desiredHeight = (int)(height * scaleFactor);
        bitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, true);
        poolTableImage.setImageBitmap(bitmap);
    }
}
