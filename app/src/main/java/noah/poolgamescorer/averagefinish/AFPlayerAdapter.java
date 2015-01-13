package noah.poolgamescorer.averagefinish;

import android.content.Context;
import android.database.DataSetObserver;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import noah.averagefinish.R;
import noah.poolgamescorer.main.Utils;

public class AFPlayerAdapter extends BaseAdapter {

    private AFGame afGame;
    private LayoutInflater layoutInflater;

    public AFPlayerAdapter(AFGame afGame, LayoutInflater layoutInflater) {
        this.afGame = afGame;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        return afGame.getPlayerCount();
    }

    @Override
    public Object getItem(int position) {
        return afGame.getPlayerList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            int rowId = position % 2 == 0 ? R.layout.afplayer_even : R.layout.afplayer_odd;
            convertView = layoutInflater.inflate(rowId, null);
        }

        // Get views
        ImageView ballImage = (ImageView) convertView.findViewById(R.id.ballImage);
        TextView nameText = (TextView) convertView.findViewById(R.id.nameText);
        TextView afText = (TextView) convertView.findViewById(R.id.afText);

        // Manipulate views
        AFPlayer player = (AFPlayer) getItem(position);
        ballImage.setImageResource(Utils.POOL_BALL_IMAGES[position]);
        nameText.setText(player.getName());
        if (afGame.getRound() == 0) {
            afText.setText("-");
        } else {
            double af = ((double) player.getTotal()) / afGame.getRound();
            afText.setText(new DecimalFormat("#.##").format(af));
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
