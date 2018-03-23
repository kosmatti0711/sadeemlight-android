package com.sadeemlight.util;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

/**
 * Created by Rajesh Dabhi on 6/9/2016.
 */
public class Circle_textview {

    public static ShapeDrawable drawCircle(Context context, int width, int height, int color) {

        //////Drawing oval & Circle programmatically /////////////

        ShapeDrawable oval = new ShapeDrawable(new OvalShape());
        oval.setIntrinsicHeight(height);
        oval.setIntrinsicWidth(width);
        oval.getPaint().setColor(color);
        return oval;

    }
}
