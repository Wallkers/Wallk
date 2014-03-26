package com.imac.wallk;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.util.List;

/**
 * Draws up to four other drawables.
 */
public class MultiDrawable extends Drawable {

    private final List<Bitmap> mDrawables;

    public MultiDrawable(List<Bitmap> drawables) {
        mDrawables = drawables;
    }

    @Override
    public void draw(Canvas canvas) {
    	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    	if (mDrawables.size() == 1) {
        	canvas.drawBitmap(mDrawables.get(0), 0,0, paint); 
            return;
        }
        int width = getBounds().width();
        int height = getBounds().height();

        canvas.save();
        canvas.clipRect(0, 0, width, height);

        if (mDrawables.size() == 2 || mDrawables.size() == 3) {
            // Paint left half
            canvas.save();
            canvas.clipRect(0, 0, width / 2, height);
            canvas.translate(-width / 4, 0);
            canvas.drawBitmap(mDrawables.get(0), 0,0, paint); 
            canvas.restore();
        }
        if (mDrawables.size() == 2) {
            // Paint right half
            canvas.save();
            canvas.clipRect(width / 2, 0, width, height);
            canvas.translate(width / 4, 0);
            canvas.drawBitmap(mDrawables.get(1), 0,0, paint); 
            canvas.restore();
        } else if(mDrawables.size() == 3){
            // Paint top right
            canvas.save();
            canvas.scale(.5f, .5f);
            canvas.translate(width, 0);
            canvas.drawBitmap(mDrawables.get(1), 0,0, paint); 

            // Paint bottom right
            canvas.translate(0, height);
            canvas.drawBitmap(mDrawables.get(2), 0,0, paint); 
            canvas.restore();
        }

        if (mDrawables.size() >= 4) {
            // Paint top left
            canvas.save();
            canvas.scale(.5f, .5f);
            canvas.drawBitmap(mDrawables.get(0), 0,0, paint); 

            // Paint bottom left
            canvas.translate(0, height);
            canvas.drawBitmap(mDrawables.get(3), 0,0, paint); 
            canvas.restore();
        }

        canvas.restore();
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
