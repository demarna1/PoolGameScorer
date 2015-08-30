package noah.poolgamescorer.main;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import noah.averagefinish.R;
import noah.poolgamescorer.averagefinish.AFGameActivity;
import noah.poolgamescorer.bowlliards.BWActivity;
import noah.poolgamescorer.endlesspool.EPActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_main);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#50000000")));
        setContentView(R.layout.activity_main);
    }

    public void startEndlessPool(View view) {
        Intent intent = new Intent(MainActivity.this, EPActivity.class);
        startActivity(intent);
    }

    public void startAverageFinish(View view) {
        Intent intent = new Intent(MainActivity.this, AFGameActivity.class);
        startActivity(intent);
    }

    public void startBowlliards(View view) {
        Intent intent = new Intent(MainActivity.this, BWActivity.class);
        startActivity(intent);
    }
}
