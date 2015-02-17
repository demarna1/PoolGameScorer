package noah.poolgamescorer.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import noah.averagefinish.R;
import noah.poolgamescorer.averagefinish.AFGameActivity;
import noah.poolgamescorer.bowlliards.BWActivity;
import noah.poolgamescorer.endlesspool.EPActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
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
