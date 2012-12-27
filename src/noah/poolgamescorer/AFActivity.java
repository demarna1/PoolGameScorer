package noah.poolgamescorer;

import java.text.DecimalFormat;
import java.util.*;
import android.support.v4.app.DialogFragment;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

public class AFActivity extends Activity {
	
	private final int TABLEROWID2 = 100;
	private final int TEXTVIEWID2 = 200;
	private final int EDITTEXTID2 = 300;
	private final int TABLEROWID3 = 400;
	private final int TEXTVIEWID3 = 500;
	private final int EDITTEXTID3 = 600;
	private final int TEXTVIEWID5 = 700;
	private final int IMAGEVIEWID = 800;
	private Map<String, Integer> playerTotals;
	private int numPlayers;
	private int round;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		playerTotals = new HashMap<String, Integer>();
		numPlayers = 0;
        round = 0;
        PrepareThirdScreen();
	}
	
	private void PrepareFirstScreen() {
    	round = 0;
    	playerTotals = new HashMap<String, Integer>();
    	setContentView(R.layout.af_choice);	
    	
    	Button button3 = (Button)findViewById(R.id.button3);
        button3.setOnClickListener(listener3);

        Button button4 = (Button)findViewById(R.id.button4);
        button4.setOnClickListener(listener4);
        
        Button button5 = (Button)findViewById(R.id.button5);
        button5.setOnClickListener(listener5);
        
        Button button6 = (Button)findViewById(R.id.button6);
        button6.setOnClickListener(listener6);
        
        Button button7 = (Button)findViewById(R.id.button7);
        button7.setOnClickListener(listener7);
        
        Button button8 = (Button)findViewById(R.id.button8);
        button8.setOnClickListener(listener8);
    }
    
    private void PrepareSecondScreen() {
    	setContentView(R.layout.af_form);
    	
    	Button backButton = (Button)findViewById(R.id.backButton);
        backButton.setOnClickListener(backListener);
        
        Button startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(startListener);
        
    	TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout1);
    	
    	for (int i = 1; i <= numPlayers; i++) {
    		//Create new row
    		TableRow tr = new TableRow(this);
    		tr.setId(TABLEROWID2+i);
    		tr.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		
    		//Add TextView to row
    		TextView tv = new TextView(this);
    		tv.setId(TEXTVIEWID2+i);
    		tv.setText("Player " + i);
    		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		tv.setPadding(5, 0, 0, 0);
    		tv.setTextSize(18);
    		tr.addView(tv);
    		
    		//Add EditText to row
    		EditText et = new EditText(this);
    		et.setId(EDITTEXTID2+i);
    		et.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		et.setSingleLine(true);
    		//set margins?
    		tr.addView(et);
    		
    		//Add row to table
    		tableLayout.addView(tr, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	} 
    }
    
    private void PrepareThirdScreen() {
    	//Create player list
    	int i;
		for (i = 1; i <= numPlayers; i++) {
			EditText et = (EditText)findViewById(EDITTEXTID2+i);
			if (et.getText().length() == 0) {
				playerTotals.put("Player " + i, 0);
			}
			else {
				playerTotals.put(et.getText().toString(), 0);
			}
		}

    	setContentView(R.layout.af_scores);

		TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout1);
		
		i = 1;
    	for (String name : playerTotals.keySet()) {
    		//Create new row
    		TableRow tr = new TableRow(this);
    		tr.setId(TABLEROWID3+i);
    		tr.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		
    		//Add position image to row
    		ImageView iv = new ImageView(this);
    		iv.setId(IMAGEVIEWID+1);
    		iv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		iv.setPadding(0, 5, 0, 5);
    		switch(i) {
    			case 1: iv.setImageResource(R.drawable.one); break;
    			case 2: iv.setImageResource(R.drawable.two); break;
    			case 3: iv.setImageResource(R.drawable.three); break;
    			case 4: iv.setImageResource(R.drawable.four); break;
    			case 5: iv.setImageResource(R.drawable.five); break;
    			case 6: case 7: case 8: default: 
    					iv.setImageResource(R.drawable.cue); break;
    		}
    		tr.addView(iv);
    		
    		//Add TextView "Name" to row
    		TextView tv1 = new TextView(this);
    		tv1.setId(TEXTVIEWID3+i);
			tv1.setText(name);
    		tv1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		tv1.setTextSize(22);
    		tv1.setPadding(0,5,0,5);
    		tr.addView(tv1);
		
			//Add EditText "New" to row
    		EditText et = new EditText(this);
    		et.setId(EDITTEXTID3+i);
    		et.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		et.setInputType(2); 	//"number" is 2
    		et.setImeOptions(6); 	//"done" is 6
    		tr.addView(et);
    		
    		//Add TextView "AF" to row
    		TextView tv3 = new TextView(this);
    		tv3.setId(TEXTVIEWID5+i);
			tv3.setText("-");
    		tv3.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		tv3.setTextSize(22);
    		tv3.setPadding(10,5,5,5);
    		tr.addView(tv3);
    		
    		//Add row to table
    		tableLayout.addView(tr, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		i++;
    	}

    	Button newButton = (Button)findViewById(R.id.newButton);
    	newButton.setOnClickListener(newListener);
    	
    	Button addButton = (Button)findViewById(R.id.addButton);
    	addButton.setOnClickListener(addListener);
    }
    
    private OnClickListener listener3 = new OnClickListener() {
    	public void onClick(View v) {
    		numPlayers = 3;
    		PrepareSecondScreen();
    	}
    };
    
    private OnClickListener listener4 = new OnClickListener() {
    	public void onClick(View v) {
    		numPlayers = 4;
    		PrepareSecondScreen();
    	}
    };
    
    private OnClickListener listener5 = new OnClickListener() {
    	public void onClick(View v) {
    		numPlayers = 5;
    		PrepareSecondScreen();
    	}
    };
    
    private OnClickListener listener6 = new OnClickListener() {
    	public void onClick(View v) {
    		numPlayers = 6;
    		PrepareSecondScreen();
    	}
    };
    
    private OnClickListener listener7 = new OnClickListener() {
    	public void onClick(View v) {
    		numPlayers = 7;
    		PrepareSecondScreen();
    	}
    };
    
    private OnClickListener listener8 = new OnClickListener() {
    	public void onClick(View v) {
    		numPlayers = 8;
    		PrepareSecondScreen();
    	}
    };
    
    private OnClickListener backListener = new OnClickListener() {
    	public void onClick(View v) {
    		PrepareFirstScreen();
    	}
    };
    
    private OnClickListener startListener = new OnClickListener() {
    	public void onClick(View v) {
    		PrepareThirdScreen();
    	}
    };
    
    private OnClickListener newListener = new OnClickListener() {
    	public void onClick(View v) {
    		NewAFDialogFragment afDialog = new NewAFDialogFragment();
    		afDialog.show(getFragmentManager(), "af");
    		PrepareFirstScreen();
    	}
    };
    
    private OnClickListener addListener = new OnClickListener() {
    	public void onClick(View v) {
    		//Variables
    		EditText et;
    		TextView tv;
    		
    		//Check number of players
    		if (numPlayers == 0) {
    			return;
    		}
    		
    		//Check to see that all editTexts are filled
    		for (int i = 1; i <= numPlayers; i++) {
    			et = (EditText)findViewById(EDITTEXTID3+i);
    			if (et.getText().length() == 0) {
    				Toast.makeText(getApplicationContext(), "Please fill in a position for every player.", Toast.LENGTH_SHORT).show();
    				return;
    			}
    		}
    		
    		//New round
    		round++;
    		((TextView)findViewById(R.id.textView1)).setText("After " + round);
    		
    		//Update the AF
    		for (int i = 1; i <= numPlayers; i++) {
    			//Find views
    			et = (EditText)findViewById(EDITTEXTID3+i);
    			tv = (TextView)findViewById(TEXTVIEWID5+i);
    			
    			//Get integer from editText
    			String scoreX = et.getText().toString();
    			et.setText(""); //Reset editText
    			int score = Integer.parseInt(scoreX);
    			
    			//Get current AF
    			double currentAF = tv.getText().toString() != "-" ? Double.parseDouble(tv.getText().toString()) : 0;
    			
    			//Calculate new AF
    			double total = score + currentAF * (round - 1);
    	        DecimalFormat df = new DecimalFormat("#.##");
    	        String newAF = df.format((total/round));
    			tv.setText(newAF);
    		}
    		
    		//Get all rows and reset
    		TableLayout tl = (TableLayout)findViewById(R.id.tableLayout1);
    		List<TableRow> trList = new ArrayList<TableRow>();
    		TableRow headerRow = (TableRow)tl.getChildAt(0);
    		for (int i = 1; i <= numPlayers; i++) {
    			trList.add((TableRow)tl.getChildAt(i));
    		}
    		tl.removeAllViews();
    		
    		//Add back all rows in order of AF
    		tl.addView(headerRow);
    		for (int i = 1; i <= numPlayers; i++) {
    			//Variables
    			double min = Double.MAX_VALUE;
    			int lowestAFIndex = Integer.MAX_VALUE;
    			
    			//Set the lowestAFIndex
    			for (int j = 0; j < trList.size(); j++) {
    				tv = (TextView)trList.get(j).getChildAt(3);
    				double currentAF = Double.parseDouble(tv.getText().toString());
    				if (currentAF < min) {
    					min = currentAF;
    					lowestAFIndex = j;
    				}
    			}
    			
    			//Add lowest row to table with respective position image; then remove from list
    			TableRow tr = trList.get(lowestAFIndex);
    			ImageView iv = (ImageView)tr.getChildAt(0);
    			switch (i) {
	    			case 1: iv.setImageResource(R.drawable.one); break;
	    			case 2: iv.setImageResource(R.drawable.two); break;
	    			case 3: iv.setImageResource(R.drawable.three); break;
	    			case 4: iv.setImageResource(R.drawable.four); break;
	    			case 5: iv.setImageResource(R.drawable.five); break;
	    			case 6: case 7: case 8: default: 
	    					iv.setImageResource(R.drawable.cue); break;
    			}
    			tl.addView(tr);
    			trList.remove(lowestAFIndex);
    		}
    	}
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity, menu);
		return true;
	}

}
