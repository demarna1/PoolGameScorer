package noah.poolgamescorer.main;

import android.app.Activity;
import android.telephony.SmsManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import noah.averagefinish.R;
import noah.poolgamescorer.averagefinish.AFGame;
import noah.poolgamescorer.averagefinish.AFPlayer;

public class Utils {

    /** The list of pool ball image paths. */
    public static final int[] POOLBALLIMAGES = {
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

    public static void SendTextMessages(AFGame afGame) {
        // Create random sequence of balls
        List<Integer> ballList = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            ballList.add(i);
        }
        Collections.shuffle(ballList);

        // Keep assigning balls until we run out (unless this is for round 0)
        while (afGame.getRound() >= 1 || ballList.size() >= afGame.getPlayerCount()) {
            List<AFPlayer> playerList = afGame.getPlayerList();
            for (int i = playerList.size() - 1; i >= 0; i--) {
                if (ballList.size() > 0) {
                    playerList.get(i).addBall(ballList.remove(0));
                }
            }
            if (ballList.size() <= 0) {
                break;
            }
        }

        // Send the texts
        for (AFPlayer player : afGame.getPlayerList()) {
            StringBuilder s = new StringBuilder();
            s.append("Round ").append(afGame.getRound() + 1);
            s.append(": ").append(player.getNiceStringFromBallList());
            SendSMS(s.toString(), player.getNumber());
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
    private static void SendSMS(String message, String number) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(number, null, message, null, null);
    }
}
