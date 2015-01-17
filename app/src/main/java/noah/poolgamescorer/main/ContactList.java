package noah.poolgamescorer.main;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactList {

    private List<Contact> contactList;

    public ContactList(Context context) {
        contactList = new ArrayList<Contact>();
        String[] projection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        };
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "='1'";
        Cursor people = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, projection, selection,
                null, ContactsContract.Contacts.DISPLAY_NAME);

        while (people.moveToNext()) {
            String contactId = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts._ID));
            String contactName = people.getString(people.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));
            selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                    + contactId;
            Cursor phones = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    selection, null, null);
            String contactNumber = null;
            if (phones.moveToNext()) {
                contactNumber = phones.getString(phones.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            if (contactNumber != null) {
                Contact contact = new Contact();
                contact.setName(contactName);
                contact.setNumber(contactNumber);
                contactList.add(contact);
            }
            phones.close();
        }
        people.close();
    }

    public String[] getNames() {
        String[] names = new String[contactList.size()];
        for (int i = 0; i < contactList.size(); i++) {
            names[i] = contactList.get(i).getName();
        }
        return names;
    }

    public String getNumber(String name) {
        for (Contact c : contactList) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c.getNumber();
            }
        }
        return "";
    }
}
