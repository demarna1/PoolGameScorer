package noah.poolgamescorer.averagefinish;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class NewGameDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        // Create view to add to the AlertDialog
        final LinearLayout verticalLayout = new LinearLayout(getActivity());
        verticalLayout.setOrientation(LinearLayout.HORIZONTAL);
        final EditText et = new EditText(getActivity());
        et.setInputType(2);
        final CheckBox cb = new CheckBox(getActivity());
        cb.setText("Send Texts");
        verticalLayout.addView(et);
        verticalLayout.addView(cb);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New AF Game");
        builder.setMessage("Enter Number of Players:");
        builder.setView(verticalLayout);
        builder.setPositiveButton("Start",
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                AFGameActivity context = (AFGameActivity)getActivity();
                if (et.getText().length() == 0) {
                    dialog.dismiss();
                }
                int num = Integer.parseInt(et.getText().toString());
                if (num > 15) {
                    Toast.makeText(context, "Too many players.",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                context.onNewGameStart(num, cb.isChecked());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", null);

        // Create the AlertDialog object and return it
        final AlertDialog dialog = builder.create();
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager
                            .LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        return dialog;
    }
}
