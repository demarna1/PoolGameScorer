package noah.poolgamescorer.averagefinish;

import java.text.DecimalFormat;

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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import noah.poolgamescorer.main.NewGameDialog;
import noah.averagefinish.R;
import noah.poolgamescorer.main.Utils;

public class AFGameActivity extends Activity {

    public static final int GAME_START = 1;

    private final int TABLEROWID3 = 400;
    private final int TEXTVIEWID3 = 500;
    private final int EDITTEXTID3 = 600;
    private final int TEXTVIEWID5 = 700;
    private final int IMAGEVIEWID = 800;

    private AFGame afGame;
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

        // TODO: Empty player appears after adding a round, leaving, and returning
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        long gameId = preferences.getLong("activeGameId", -1);
        if (gameId < 0) {
            afGame = new AFGame(0, false);
        } else {
            afGame = AFDatabaseHelper.pullGameFromDatabase(this, gameId);
        }

        // Add in scoring rows
        AddScoringRows();
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

            // Check to see that all editTexts are filled
            for (int i = 1; i <= afGame.getPlayerCount(); i++) {
                if (((EditText) findViewById(EDITTEXTID3 + i)).getText()
                        .length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please fill in a position for every player.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            StartNextRound();
        }
    };

    private void StartNextRound() {
        // Next round
        afGame.newRound();
        ((TextView)findViewById(R.id.textView1)).setText(
                "After " + afGame.getRound());

        // Update the totals
        for (int i = 1; i <= afGame.getPlayerCount(); i++) {
            EditText et = (EditText) findViewById(EDITTEXTID3 + i);
            int score = Integer.parseInt(et.getText().toString());
            AFPlayer player = afGame.getPlayer(i-1);
            player.addToTotal(score);
            et.setText("");
        }

        // Save game to database
        AFDatabaseHelper.pushGameToDatabase(this, afGame);

        // Sort list and update standings
        afGame.sortPlayerListByTotal();
        int j = 1;
        for (AFPlayer player : afGame.getPlayerList()) {
            TextView tvName = (TextView) findViewById(TEXTVIEWID3 + j);
            tvName.setText(player.getName());
            TextView tvAF = (TextView) findViewById(TEXTVIEWID5 + j);
            double af = player.getAF(afGame.getRound());
            tvAF.setText(new DecimalFormat("#.##").format(af));
            j++;
        }

        // Hide the keyboard
        Utils.HideSoftKeyboard(this);
        if (afGame.getSendTexts()) {
            Utils.SendTextMessages(afGame);
        }
    }

    private void AddScoringRows() {
        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout1);
        int i = 0;
        for (AFPlayer player : afGame.getPlayerList()) {
            // Create new row
            TableRow tr = new TableRow(this);
            tr.setId(TABLEROWID3 + i + 1);
            tr.setLayoutParams(new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

            // Add position image to row
            ImageView iv = new ImageView(this);
            iv.setId(IMAGEVIEWID + i + 1);
            iv.setLayoutParams(new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            iv.setPadding(0, 5, 0, 5);
            iv.setImageResource(Utils.POOLBALLIMAGES[i]);
            tr.addView(iv);

            // Add TextView "Name" to row
            TextView tv1 = new TextView(this);
            tv1.setId(TEXTVIEWID3 + i + 1);
            tv1.setText(player.getName());
            tv1.setLayoutParams(new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            tv1.setTextSize(22);
            tv1.setPadding(0, 5, 0, 5);
            tr.addView(tv1);

            // Add EditText "New" to row
            EditText et = new EditText(this);
            et.setId(EDITTEXTID3 + i + 1);
            et.setLayoutParams(new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            et.setInputType(2); // "number" is 2
            et.setImeOptions(5); // "next" is 5
            tr.addView(et);

            // Add TextView "AF" to row
            TextView tv3 = new TextView(this);
            tv3.setId(TEXTVIEWID5 + i + 1);
            tv3.setText("-");
            tv3.setLayoutParams(new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            tv3.setTextSize(22);
            tv3.setPadding(10, 5, 5, 5);
            tr.addView(tv3);

            // Add row to table
            tableLayout.addView(tr, new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            i++;
        }
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
            afGame = AFDatabaseHelper.pullGameFromDatabase(this, gameId);
            // TODO: Remove any existing rows; preferably convert to ListView
            AddScoringRows();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    private void ScalePoolTableImage() {
        ImageView poolTableImage = (ImageView)findViewById(R.id.poolTableImage);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.pooltable);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int desiredWidth = size.x;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleFactor = (float)desiredWidth/(float)width;
        int desiredHeight = (int)(height * scaleFactor);
        bitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight,
                true);
        poolTableImage.setImageBitmap(bitmap);
    }
}
