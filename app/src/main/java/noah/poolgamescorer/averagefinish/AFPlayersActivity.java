package noah.poolgamescorer.averagefinish;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import noah.averagefinish.Contact;
import noah.averagefinish.ContactList;
import noah.averagefinish.R;
import noah.averagefinish.Utils;

public class AFPlayersActivity extends Activity {

    private final int TABLEROWID2 = 100;
    private final int TEXTVIEWID2 = 200;
    private final int EDITTEXTID2 = 300;
    private final int IMAGEVIEWID = 800;

    private AFGame afGame;
    private ContactList contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afplayers);
        int numPlayers = getIntent().getIntExtra("numPlayers", 3);
        boolean sendTexts = getIntent().getBooleanExtra("sendTexts", false);
        afGame = new AFGame(numPlayers, sendTexts);
        contactList = new ContactList(this);

        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout1);
        for (int i = 1; i <= afGame.getPlayerCount(); i++) {
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
        int id = item.getItemId();
        if (id == R.id.action_start) {

            // 1. Create player list from edit texts
            // 2. Determine if contacts are valid
            // 3. Save game to database
            // 4. Put the new game id in the intent
            // 5. Call intent for AFGameActivity

            // Create player list
            Contact[] playerContacts = new Contact[afGame.getPlayerCount()];
            boolean contactsValid = true;
            for (int i = 0; i < afGame.getPlayerCount(); i++) {
                EditText et = (EditText)findViewById(EDITTEXTID2 + i + 1);
                String text = et.getText().toString();
                if (afGame.getSendTexts()) {
                    playerContacts[i] = contactList.getContact(text);
                    if (playerContacts[i] == null) {
                        contactsValid = false;
                    }
                } else {
                    if (text.length() == 0) {
                        playerContacts[i] = new Contact("Player " + (i+1), "");
                    }
                    else {
                        playerContacts[i] = new Contact(text, "");
                    }
                }
            }
            if (contactsValid) {
                afGame.setNamesAndNumbers(playerContacts);
                if (afGame.getSendTexts()) {
                    Utils.SendTextMessages(afGame);
                }
                // CALL INTENT HERE!!!
                Toast.makeText(this, "NOT READY", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Valid contact required for each player.",
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) { }

        @Override
        public void afterTextChanged(Editable s) { }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) { }
    };
}
