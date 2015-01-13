package noah.poolgamescorer.averagefinish;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import noah.poolgamescorer.main.Contact;
import noah.poolgamescorer.main.ContactList;
import noah.averagefinish.R;
import noah.poolgamescorer.main.Utils;

public class AFPlayersActivity extends Activity {

    private final int TABLEROWID2 = 100;
    private final int TEXTVIEWID2 = 200;
    private final int EDITTEXTID2 = 300;
    private final int IMAGEVIEWID = 800;

    private ContactList contactList;
    private int numPlayers;
    private boolean sendTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afplayers);
        numPlayers = getIntent().getIntExtra("numPlayers", 3);
        sendTexts = getIntent().getBooleanExtra("sendTexts", false);
        // TODO: run in background on app start
        contactList = new ContactList(this);

        // TODO: change this to ListView to avoid table mess
        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout1);
        for (int i = 1; i <= numPlayers; i++) {
            // Create new row
            TableRow tr = new TableRow(this);
            tr.setId(TABLEROWID2 + i);
            tr.setLayoutParams(new TableRow.LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

            // Add TextView to row
            TextView tv = new TextView(this);
            tv.setId(TEXTVIEWID2 + i);
            tv.setText("Player " + i);
            tv.setLayoutParams(new TableRow.LayoutParams(
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
            TableRow.LayoutParams lp = new TableRow.LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
            iv.setLayoutParams(lp);
            tr.addView(iv);

            // Add EditText to row
            AutoCompleteTextView et = new AutoCompleteTextView(this);
            et.setId(EDITTEXTID2 + i);
            lp = new TableRow.LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 5, 15, 5);
            et.setLayoutParams(lp);
            et.setSingleLine(true);
            et.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            et.addTextChangedListener(watcher);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, contactList.getNames());
            et.setAdapter(adapter);
            et.setBackgroundColor(0x66000000);
            tr.addView(et);

            // Add row to table
            tableLayout.addView(tr, new TableRow.LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_afplayers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_start) {
            List<Contact> playerContacts = new ArrayList<>();
            if (PopulatePlayerContacts(playerContacts)) {
                AFGame afGame = CreateNewGame(playerContacts);
                SaveGameAndFinish(afGame);
            } else {
                Toast.makeText(this, "Valid contact required for each player.",
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean PopulatePlayerContacts(List<Contact> playerContacts) {
        for (int i = 0; i < numPlayers; i++) {
            EditText et = (EditText)findViewById(EDITTEXTID2 + i + 1);
            String text = et.getText().toString();
            if (sendTexts) {
                Contact contact = contactList.getContact(text);
                if (contact == null) {
                    return false;
                }
                playerContacts.add(contact);
            } else {
                // TODO: refactor this mess (change Contact to no-arg constructor)
                if (text.length() == 0) {
                    playerContacts.add(new Contact("Player " + (i+1), ""));
                }
                else {
                    playerContacts.add(new Contact(text, ""));
                }
            }
        }
        return true;
    }

    private AFGame CreateNewGame(List<Contact> playerContacts) {
        AFGame afGame = new AFGame();
        List<AFPlayer> playerList = new ArrayList<>();
        for (Contact contact : playerContacts) {
            AFPlayer player = new AFPlayer();
            player.setName(contact.getName());
            player.setNumber(contact.getNumber());
            playerList.add(player);
        }
        afGame.setSendTexts(sendTexts);
        afGame.setPlayerList(playerList);
        if (sendTexts) {
            Utils.SendTextMessages(afGame);
        }
        return afGame;
    }

    private void SaveGameAndFinish(AFGame afGame) {
        AFDatabaseHelper.pushGameToDatabase(this, afGame);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("activeGameId", afGame.getId());
        editor.apply();
        Intent intent = new Intent();
        intent.putExtra("gameId", afGame.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO: Change Xs to check marks when contact is confirmed
        }

        @Override
        public void afterTextChanged(Editable s) { }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) { }
    };
}
