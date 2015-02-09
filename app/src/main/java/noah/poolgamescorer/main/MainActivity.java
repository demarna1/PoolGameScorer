package noah.poolgamescorer.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import noah.averagefinish.R;
import noah.poolgamescorer.averagefinish.AFGameActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    public void startEndlessPool(View view) {
        //Intent intent = new Intent(MainActivity.this, EPGameActivity.class);
        //startActivity(intent);
    }

    public void startAverageFinish(View view) {
        Intent intent = new Intent(MainActivity.this, AFGameActivity.class);
        startActivity(intent);
    }

    public void startBowlliards(View view) {
        //Intent intent = new Intent(MainActivity.this, BGameActivity.class);
        //startActivity(intent);
    }
}
