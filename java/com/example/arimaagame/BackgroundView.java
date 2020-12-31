package com.example.arimaagame;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;

public class BackgroundView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String PREF_BACKSELECTION = "backset_selection";
    static final String TAG = "BackgroundView";
    private Bitmap back_map;
    protected int backset = 1;
    int width, height;
    public BackgroundView(Context context) {
        super(context);
        initialize(context);
    }
    public BackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public BackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context){

        if(null == context) context = this.getContext();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        try {
            backset = Integer.parseInt(pref.getString(PREF_BACKSELECTION, "1"));
        } catch (NumberFormatException N) {
            Log.v(TAG, "Tried to backset parse " + pref.getString(PREF_BACKSELECTION, "1"));
            backset = 1;
        }
    }

    public void setWindowWidth(int windowWidth){
        width = windowWidth;
        height = windowWidth;
        setDrawable();
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        if(widthMeasureSpec < heightMeasureSpec)
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        else
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(back_map != null) canvas.drawBitmap(back_map, 0, 0, null);

    }

    public void updateGraphicsSelection(SharedPreferences pref){
        backset = Integer.parseInt(pref.getString(PREF_BACKSELECTION, "1"));
        setDrawable();
    }

    private void setDrawable(){
        setImageDrawable(getResources().getDrawable(R.drawable.stone));
    }

}

