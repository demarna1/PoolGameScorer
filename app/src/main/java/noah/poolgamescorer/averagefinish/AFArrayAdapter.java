package noah.poolgamescorer.averagefinish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import noah.averagefinish.R;
import noah.poolgamescorer.main.Utils;

public class AFArrayAdapter extends ArrayAdapter<AFPlayer> {

    public AFArrayAdapter(Context context, List<AFPlayer> playerList) {
        super(context, 0, playerList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.afplayer_odd,
                    parent, false);
        }

        // Get views
        ImageView ballImage = (ImageView) convertView.findViewById(R.id.ballImage);
        TextView nameText = (TextView) convertView.findViewById(R.id.nameText);
        TextView afText = (TextView) convertView.findViewById(R.id.afText);

        // Manipulate views
        AFPlayer player = getItem(position);
        ballImage.setImageResource(Utils.POOL_BALL_IMAGES[position]);
        nameText.setText(player.getName());
        // TODO: get correct AF using round
        //if (round == 0) {
        //    afText.setText("-");
        //} else {
        double af = player.getAF(1);
        afText.setText(new DecimalFormat("#.##").format(af));
        //}

        return convertView;
    }
}
