package noah.averagefinish;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactList {

	/** Holds the list of contacts. */
	private List<Contact> contactList;

	/**
	 * Builds a list of names and number from the user's contact list.
	 *
	 * @param context
	 * 			 the activity context
	 */
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
				contactList.add(new Contact(contactName, contactNumber));
			}
			phones.close();
		}
		people.close();
	}

	/**
	 * Returns the list of names in the contact list.
	 *
	 * @return the list of names in the contact list
	 */
	public String[] getNames() {
		String[] names = new String[contactList.size()];
		for (int i = 0; i < contactList.size(); i++) {
			names[i] = contactList.get(i).getName();
		}
		return names;
	}

	/**
	 * Gets the contact with the corresponding name. Returns null if not found.
	 *
	 * @param name
	 * 			 the name of the contact
	 * @return the contact with the corresponding name
	 */
	public Contact getContact(String name) {
		for (Contact c : contactList) {
			if (c.getName().equalsIgnoreCase(name)) {
				return c;
			}
		}
		return null;
	}
}
