package noah.averagefinish;

import android.app.Activity;
import android.telephony.SmsManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import noah.averagefinish.R;

public class Utils {

	/** The list of pool ball image paths. */
	public static final int[] POOLBALLIMAGES =
		{
			R.drawable.one,
			R.drawable.two,
			R.drawable.three,
			R.drawable.four,
			R.drawable.five,
			R.drawable.six,
			R.drawable.seven,
			R.drawable.eight,
			R.drawable.nine,
			R.drawable.ten,
			R.drawable.eleven,
			R.drawable.twelve,
			R.drawable.thirteen,
			R.drawable.fourteen,
			R.drawable.fifteen
		};

	/**
	 * Closes the soft keyboard.
	 *
	 * @param activity
	 * 			 the current activity
	 */
	public static void HideSoftKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(
				Activity.INPUT_METHOD_SERVICE);
		View view = activity.getCurrentFocus();
		if (view != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/**
	 * Sends the given text message to the given phone number.
	 *
	 * @param message
	 * 			 the text message to send
	 * @param number
	 * 			 the phone number to message
	 */
	public static void SendSMS(String message, String number) {
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(number, null, message, null, null);
	}
}
