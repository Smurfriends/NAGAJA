package com.gachon.nagaja;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class CanvasView extends View {

    Paint paint = new Paint();

    public CanvasView(Context context) {
        super(context);

    }

    @Override
    protected void onDraw(Canvas canvas) {
    }

}
