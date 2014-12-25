package noah.averagefinish;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class AFActivity extends Activity {

    private final int TABLEROWID2 = 100;
    private final int TEXTVIEWID2 = 200;
    private final int EDITTEXTID2 = 300;
    private final int TABLEROWID3 = 400;
    private final int TEXTVIEWID3 = 500;
    private final int EDITTEXTID3 = 600;
    private final int TEXTVIEWID5 = 700;
    private final int IMAGEVIEWID = 800;

    private AFModel model;
    private NewGameDialog newGameDialog;
    private ContactList contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new AFModel(0, false);
        newGameDialog = new NewGameDialog();
        contactList = new ContactList(this);
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

    private void PrepareSecondScreen() {
        setContentView(R.layout.af_form);
        Button startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(startListener);
        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout1);

        for (int i = 1; i <= model.getPlayerCount(); i++) {
            // Create new row
            TableRow tr = new TableRow(this);
            tr.setId(TABLEROWID2 + i);
            tr.setLayoutParams(new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

            // Add TextView to row
            TextView tv = new TextView(this);
            tv.setId(TEXTVIEWID2 + i);
            tv.setText("Player " + i);
            tv.setLayoutParams(new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setPadding(5, 0, 0, 0);
            tv.setTextSize(18);
            tr.addView(tv);

            // Add ImageView to row
            ImageView iv = new ImageView(this);
            iv.setId(IMAGEVIEWID + i + 100);
            iv.setImageResource(R.drawable.red_x);
            iv.setPadding(0, 0, 5, 0);
            LayoutParams lp = new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
            iv.setLayoutParams(lp);
            tr.addView(iv);

            // Add EditText to row
            AutoCompleteTextView et = new AutoCompleteTextView(this);
            et.setId(EDITTEXTID2 + i);
            lp = new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 5, 15, 5);
            et.setLayoutParams(lp);
            et.setSingleLine(true);
            et.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            et.addTextChangedListener(watcher);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,
                    contactList.getNames());
            et.setAdapter(adapter);
            et.setBackgroundColor(0x66000000);
            tr.addView(et);

            // Add row to table
            tableLayout.addView(tr, new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private OnClickListener startListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // Create player list
            Contact[] playerContacts = new Contact[model.getPlayerCount()];
            //String[] playerNames = new String[model.getPlayerCount()];
            boolean contactsValid = true;
            for (int i = 0; i < model.getPlayerCount(); i++) {
                EditText et = (EditText)findViewById(EDITTEXTID2 + i + 1);
                String text = et.getText().toString();
                if (model.getSendTexts()) {
                    playerContacts[i] = contactList.getContact(text);
                    if (playerContacts[i] == null) {
                        contactsValid = false;
                    }
                }
                else {
                    if (text.length() == 0) {
                        playerContacts[i] = new Contact("Player " + (i+1), "");
                    }
                    else {
                        playerContacts[i] = new Contact(text, "");
                    }
                }
            }
            if (contactsValid) {
                model.setNamesAndNumbers(playerContacts);
                PrepareFirstScreen();
                if (model.getSendTexts()) {
                    SendTextMessages();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),
                        "Valid contact required for each player.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

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
            if (model.getPlayerCount() == 0)
                return;

            // Check to see that all editTexts are filled
            for (int i = 1; i <= model.getPlayerCount(); i++) {
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

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {

        }

        @Override
        public void afterTextChanged(Editable s) { }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) { }
    };

    private void StartNextRound() {
        // Next round
        model.newRound();
        ((TextView)findViewById(R.id.textView1)).setText(
                "After " + model.getRound());

        // Update the totals
        for (int i = 1; i <= model.getPlayerCount(); i++) {
            EditText et = (EditText) findViewById(EDITTEXTID3 + i);
            int score = Integer.parseInt(et.getText().toString());
            Player player = model.getPlayer(i-1);
            player.addToTotal(score);
            et.setText("");
        }

        // Sort list and update standings
        model.sortPlayerListByTotal();
        int j = 1;
        for (Player player : model.getPlayerList()) {
            TextView tvName = (TextView) findViewById(TEXTVIEWID3 + j);
            tvName.setText(player.getName());
            TextView tvAF = (TextView) findViewById(TEXTVIEWID5 + j);
            double af = player.getAF(model.getRound());
            tvAF.setText(new DecimalFormat("#.##").format(af));
            j++;
        }

        // Hide the keyboard
        Utils.HideSoftKeyboard(this);
        if (model.getSendTexts()) {
            SendTextMessages();
        }
    }

    private void SendTextMessages() {
        // Create random sequence of balls
        List<Ball> ballList = new ArrayList<Ball>();
        for (int i = 1; i <= 15; i++) {
            ballList.add(new Ball(i));
        }
        Collections.shuffle(ballList);

        // Keep assigning balls until we run out (unless this is for round 0)
        while (model.getRound() >= 1 ||
                ballList.size() >= model.getPlayerCount()) {
            Player[] playerList = model.getPlayerList();
            for (int i = model.getPlayerCount() - 1; i >= 0; i--) {
                if (ballList.size() > 0) {
                    playerList[i].addBall(ballList.remove(0));
                }
            }
            if (ballList.size() <= 0) {
                break;
            }
        }

        // Send the texts
        for (Player player : model.getPlayerList()) {
            StringBuilder s = new StringBuilder();
            s.append("Round ").append(model.getRound() + 1);
            s.append(": ").append(player.ballListToString());
            Utils.SendSMS(s.toString(), player.getNumber());
        }
    }

    private void AddScoringRows() {
        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout1);
        int i = 0;
        for (Player player : model.getPlayerList()) {
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
        model = new AFModel(num, sendTexts);
        PrepareSecondScreen();
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
