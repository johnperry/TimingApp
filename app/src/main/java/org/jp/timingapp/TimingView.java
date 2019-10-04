/**
 * Created by John on 3/30/2016.
 */

package org.jp.timingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class TimingView extends View {
    Context context;
    private TextPaint anglePaint;
    private TextPaint numberPaint;
    private Paint ringPaint;
    private Paint circlePaint;
    private Paint linePaint;
    private Paint pointerPaint;
    private Paint diamondPaint;
    private Paint arcPaint;
    private Paint extPaint;
    private double angle = 0;
    private double offsetAngle = 0;
    private double atdcAngle = 0;
    private double btdcAngle = 0;
    private boolean atdcSet = false;
    private boolean btdcSet = false;

    static char degrees = (char)0x00B0;

    public TimingView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
        anglePaint = new TextPaint();
        anglePaint.setColor(Color.BLACK);
        anglePaint.setTextSize(144);
        anglePaint.setTypeface(Typeface.MONOSPACE);
        anglePaint.setFakeBoldText(true);
        circlePaint = new Paint();
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Paint.Style.FILL);
        ringPaint = new Paint();
        ringPaint.setColor(Color.BLACK);
        ringPaint.setStrokeWidth(8.0f);
        ringPaint.setStyle(Paint.Style.STROKE);
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(2.0f);
        numberPaint = new TextPaint();
        numberPaint.setColor(Color.BLACK);
        numberPaint.setTextSize(96);
        pointerPaint = new Paint();
        pointerPaint.setColor(Color.RED);
        pointerPaint.setStyle(Paint.Style.FILL);
        pointerPaint.setAlpha(175);
        diamondPaint = new Paint();
        diamondPaint.setColor(Color.BLACK);
        diamondPaint.setStyle(Paint.Style.FILL);
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        arcPaint.setColor(Color.GRAY);
        arcPaint.setTextSize(64);
        extPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        extPaint.setStyle(Paint.Style.STROKE);
        extPaint.setColor(Color.BLACK);
        extPaint.setTextSize(64);
        extPaint.setStrokeWidth(2);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float h = getMeasuredHeight();
        float w = getMeasuredWidth();
        float cx = w / 2;
        float cy = h / 2;

        //draw the outer circle with angle tick marks
        drawFace(canvas); //**********

        canvas.save();
        canvas.rotate((float)angle, cx, cy);

        //draw the pointer
        Path path = new Path();
        float hOffset = - anglePaint.ascent() / 2;
        float dh = 0.05f * cx;
        path.moveTo(cx, cy - hOffset);
        path.lineTo(cx - 0.05f * cx, cy - hOffset - dh);
        path.lineTo(cx , cy - cx);
        path.lineTo(cx + 0.05f * cx, cy - hOffset - dh);
        path.lineTo(cx, cy - hOffset);
        canvas.drawPath(path, pointerPaint);

        //draw the angle
        double x = angle - offsetAngle;
        if (x > 180.0) x -= 360.0;
        else if (x <= -180.0) x += 360.0;
        String text = String.format("%.1f", x);
        if (text.equals("-0.0")) text = "0.0";
        float width = anglePaint.measureText(text);
        float height = - anglePaint.ascent();
        canvas.drawText(text, cx - width/2, cy + height/2, anglePaint);
        canvas.restore();
    }

    public void setAngle(double rad) {
        angle = Math.toDegrees(rad);
        invalidate();
    }

    public void setATDC() {
        atdcAngle = angle;
        atdcSet = true;
        if (btdcSet) offsetAngle = (atdcAngle + btdcAngle) / 2;
    }

    public void setCenter() {
        offsetAngle = angle;
        atdcSet = false;
        btdcSet = false;
    }

    public void setBTDC() {
        btdcAngle = angle;
        btdcSet = true;
        if (atdcSet) offsetAngle = (atdcAngle + btdcAngle) / 2;
    }

    private void drawFace(Canvas canvas) {
        float h = getMeasuredHeight();
        float w = getMeasuredWidth();
        float cx = w / 2;
        float cy = h / 2;
        float outerR = cx;

        //draw the angle tick marks
        canvas.drawCircle(cx, cy, outerR, circlePaint);
        float tick90 = 0.2f * outerR;
        float tick10 = 0.15f * outerR;
        float tick5 = 0.1f * outerR;
        float tick1 = 0.05f * outerR;
        for (int i=0; i<360; i++) {
            float a = (float) ((i + offsetAngle) * Math.PI / 180.);
            float tick = tick1;
            if (i % 45 == 0) {
                tick = ((i % 90 == 0) ? tick90 : tick5);
                int n = (i <= 180) ? i : 360 - i;
                String text = Integer.toString(n);
                float width = numberPaint.measureText(text);
                float height = -numberPaint.ascent();
                canvas.save();
                canvas.rotate((float) (i + offsetAngle), cx, cy);
                canvas.drawText(text + degrees, cx - width / 2, cy - cx + tick90 + height, numberPaint);

                //draw a diamond at the 45s
                Path path = new Path();
                float dh = 0.02f * cx;
                float top = cy - cx + tick5;
                path.moveTo(cx, top);
                path.lineTo(cx - dh, top + dh);
                path.lineTo(cx, top + 2 * dh);
                path.lineTo(cx + dh, top + dh);
                path.lineTo(cx, top);
                canvas.drawPath(path, diamondPaint);

                canvas.restore();
                linePaint.setStrokeWidth( ((i%90 == 0) ? 4.0f : 2.0f) );
            }
            else if (i % 10 == 0) {
                tick = tick10;
                linePaint.setStrokeWidth(3.0f);
            }
            else if (i % 5 == 0) {
                tick = tick5;
                linePaint.setStrokeWidth(2.0f);
            }
            else linePaint.setStrokeWidth(2.0f);

            float innerR = (cx - tick);
            float startX = cx + innerR * (float)Math.cos(a);
            float startY = cy + innerR * (float)Math.sin(a);
            float stopX = cx + outerR * (float) Math.cos(a);
            float stopY = cy + outerR * (float)Math.sin(a);
            canvas.drawLine(startX, startY, stopX, stopY, linePaint);
        }

        //draw the arc text, if we know where TDC is
        if (atdcSet && btdcSet) {
            canvas.save();

            canvas.rotate((float)offsetAngle, cx, cy);
            Path path = new Path();
            float radius = cx - tick90 + numberPaint.ascent() + arcPaint.ascent();
            path.addArc(cx - radius, cy - radius, cx + radius, cy + radius, 270f, 180f);
            arcPaint.setColor(Color.RED);
            canvas.drawTextOnPath(" After Top Dead Center", path, 0f, 20f, arcPaint);
            canvas.drawTextOnPath(" After Top Dead Center", path, 0f, 20f, extPaint);

            float a = 270f - arcPaint.measureText("Before Top Dead Center  ")/radius * 180/(float)Math.PI;
            path = new Path();
            path.addArc(cx - radius, cy - radius, cx + radius, cy + radius, a, 180f);
            arcPaint.setColor(Color.GREEN);

            canvas.drawTextOnPath(" Before Top Dead Center", path, 0f, 20f, arcPaint);
            canvas.drawTextOnPath(" Before Top Dead Center", path, 0f, 20f, extPaint);

            float startY = cy - cx + tick90 - numberPaint.ascent() + numberPaint.descent();
            float stopY = startY - arcPaint.ascent();
            linePaint.setStrokeWidth(4.0f);
            canvas.drawLine(cx, startY, cx, stopY, linePaint);

            canvas.restore();
        }
        //draw the outer circle
        canvas.drawCircle(cx, cy, outerR, ringPaint);
    }
}