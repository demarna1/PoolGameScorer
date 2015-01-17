package noah.poolgamescorer.averagefinish;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import noah.poolgamescorer.main.Contact;
import noah.poolgamescorer.main.ContactList;
import noah.averagefinish.R;
import noah.poolgamescorer.main.Utils;

public class AFPlayersActivity extends Activity {

    private boolean sendTexts;
    private ContactList phoneContactList;
    private List<Contact> afContactList;

    private class ContactTextWatcher implements TextWatcher {
        private ImageView validImage;
        private Contact contact;
        public ContactTextWatcher(ImageView validImage, Contact contact) {
            this.validImage = validImage;
            this.contact = contact;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s) {
            // Check to see if the text has been changed to a valid contact name
            contact.setName(s.toString());
            contact.setNumber(phoneContactList.getNumber(contact.getName()));
            contact.setValid(!contact.getNumber().equals(""));
            validImage.setImageResource(contact.getValid() ? R.drawable.green_check :
                    R.drawable.red_x);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afplayers);
        final int numPlayers = getIntent().getIntExtra("numPlayers", 3);
        sendTexts = getIntent().getBooleanExtra("sendTexts", false);

        // TODO: run in background on app start
        phoneContactList = new ContactList(this);

        afContactList = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            afContactList.add(new Contact());
        }

        ArrayAdapter<Contact> contactListAdapter =
                new ArrayAdapter<Contact>(this, 0, afContactList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    int viewId = R.layout.afplayer_new;
                    convertView = LayoutInflater.from(getContext()).inflate(viewId, null);
                }

                Contact player = getItem(position);
                TextView playerText = (TextView) convertView.findViewById(R.id.playerText);
                playerText.setText("Player " + (position + 1));

                ImageView validImage = (ImageView) convertView.findViewById(R.id.validImage);
                validImage.setVisibility(sendTexts ? View.VISIBLE : View.INVISIBLE);

                AutoCompleteTextView playerEdit =
                        (AutoCompleteTextView) convertView.findViewById(R.id.playerEdit);
                playerEdit.setBackgroundColor(0x66000000);
                playerEdit.setAdapter(new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_list_item_1, phoneContactList.getNames()));
                ContactTextWatcher textWatcher = new ContactTextWatcher(validImage, player);
                playerEdit.addTextChangedListener(textWatcher);
                return convertView;
            }
        };

        ListView newListView = (ListView) findViewById(R.id.newListView);
        newListView.setAdapter(contactListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_afplayers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_start) {
            if (ValidatePlayerList()) {
                AFGame afGame = CreateNewGame();
                SaveGameAndFinish(afGame);
            } else {
                Toast.makeText(this, "Valid contact required for each player.",
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean ValidatePlayerList() {
        int i = 1;
        for (Contact contact : afContactList) {
            if (sendTexts && !contact.getValid()) {
                return false;
            }
            if (!sendTexts && contact.getName().equals("")) {
                contact.setName("Player " + i);
                contact.setNumber("");
            }
            i++;
        }
        return true;
    }

    private AFGame CreateNewGame() {
        AFGame afGame = new AFGame();
        List<AFPlayer> playerList = new ArrayList<>();
        for (Contact contact : afContactList) {
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
}
