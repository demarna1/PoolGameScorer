package noah.poolgamescorer.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import noah.averagefinish.R;

public class MainMenuCard extends CardView {

    public MainMenuCard(Context context) {
        super(context);
        inflate(context, R.layout.custom_card_main, this);
    }

    public MainMenuCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.custom_card_main, this);
        ApplyAttributes(context, attrs);
    }

    public MainMenuCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.custom_card_main, this);
        ApplyAttributes(context, attrs);
    }

    private void ApplyAttributes(Context context, AttributeSet attrs) {
        // Get attributes
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.mainMenuCard);
        int imageSrc = attributes.getResourceId(R.styleable.mainMenuCard_imageViewSrc,
                R.drawable.eight_ball);
        String name = attributes.getString(R.styleable.mainMenuCard_nameText);
        String optimal = attributes.getString(R.styleable.mainMenuCard_optimalText);
        attributes.recycle();

        // Customize card using user attributes
        ImageView image = (ImageView) findViewById(R.id.mainMenuCardImage);
        TextView nameText = (TextView) findViewById(R.id.mainMenuCardName);
        TextView optimalText = (TextView) findViewById(R.id.mainMenuCardOptimal);
        image.setImageResource(imageSrc);
        nameText.setText(name);
        optimalText.setText(optimal);
    }
}
