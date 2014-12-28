package noah.poolgamescorer.averagefinish;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import noah.averagefinish.Ball;
import noah.averagefinish.Contact;
import noah.averagefinish.ContactList;
import noah.averagefinish.Player;
import noah.averagefinish.R;
import noah.averagefinish.Utils;

public class AFGameActivity extends Activity {

    private final int TABLEROWID3 = 400;
    private final int TEXTVIEWID3 = 500;
    private final int EDITTEXTID3 = 600;
    private final int TEXTVIEWID5 = 700;
    private final int IMAGEVIEWID = 800;

    private static final int GAME_LOADER = 1;
    private SimpleCursorAdapter gameCursorAdapter;

    private String contactListProjection[] = {
            AFContentProvider.ID,
            AFContentProvider.ROUND,
            AFContentProvider.SEND_TEXTS
    };

    private AFGame game;
    private NewGameDialog newGameDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game = new AFGame(0, false);
        newGameDialog = new NewGameDialog();
        PrepareFirstScreen();
    }

    private void PrepareFirstScreen() {
        // Change layout and add in scoring rows
        setContentView(R.layout.af_scores);
        Utils.HideSoftKeyboard(this);
        ScalePoolTableImage();
        AddScoringRows();

        // Add New Round and Add Round buttons
        Button newButton = (Button) findViewById(R.id.newButton);
        newButton.setOnClickListener(newListener);
        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(addListener);
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
            if (game.getPlayerCount() == 0)
                return;

            // Check to see that all editTexts are filled
            for (int i = 1; i <= game.getPlayerCount(); i++) {
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
        game.newRound();
        ((TextView)findViewById(R.id.textView1)).setText(
                "After " + game.getRound());

        // Update the totals
        for (int i = 1; i <= game.getPlayerCount(); i++) {
            EditText et = (EditText) findViewById(EDITTEXTID3 + i);
            int score = Integer.parseInt(et.getText().toString());
            Player player = game.getPlayer(i-1);
            player.addToTotal(score);
            et.setText("");
        }

        // Sort list and update standings
        game.sortPlayerListByTotal();
        int j = 1;
        for (Player player : game.getPlayerList()) {
            TextView tvName = (TextView) findViewById(TEXTVIEWID3 + j);
            tvName.setText(player.getName());
            TextView tvAF = (TextView) findViewById(TEXTVIEWID5 + j);
            double af = player.getAF(game.getRound());
            tvAF.setText(new DecimalFormat("#.##").format(af));
            j++;
        }

        // Hide the keyboard
        Utils.HideSoftKeyboard(this);
        if (game.getSendTexts()) {
            Utils.SendTextMessages(game);
        }
    }

    private void AddScoringRows() {
        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout1);
        int i = 0;
        for (Player player : game.getPlayerList()) {
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
        game = new AFGame(num, sendTexts);
        Intent intent = new Intent(AFGameActivity.this, AFPlayersActivity.class);
        intent.putExtra("numPlayers", num);
        intent.putExtra("sendTexts", sendTexts);
        startActivity(intent);
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
