package it.uniba.di.misurapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Classe custom per stampa livella, disegnata tramite un grafico a linee
 */
public class LevelView extends View {
    private Paint paint = new Paint();
    private Paint line = new Paint();
    private int circleRadius;
    private static final int CIRCLE_RADIUS_DP = 15; // Raggio bolla
    public int MAX_ANGLE = 30;
    public float xAngle=0;
    public float yAngle = 0;
    public float zAngle = 0;

    // Costruttore
    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Setup grafico
        line.setColor(Color.GRAY);
        line.setStyle(Paint.Style.FILL);
        line.setStrokeWidth((float) 5.0);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        circleRadius = (int)(CIRCLE_RADIUS_DP * getResources().getDisplayMetrics().density);
    }

    /**
     * Comportamento del grafico alla variazione dei dati
     * Credito a https://github.com/sacps031103 per la creazione del codice estrapolato e adattato al progetto corrente
     * @param canvas disegno del grafico e dell'indicatore a bolla
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine(0, canvas.getHeight()/2, canvas.getWidth(), canvas.getHeight()/2, line);
        canvas.drawLine(canvas.getWidth()/2, 0, canvas.getWidth()/2, canvas.getHeight(), line);

        int x = getWidth()/2;
        int y = getHeight()/2;

        // Comportamento in base all'angolazione del telefono asse x
        if (Math.abs(zAngle) <= MAX_ANGLE) {
            int deltaX = (int) (getWidth()/2 * zAngle / MAX_ANGLE);
            x += deltaX;
        } else if (zAngle > MAX_ANGLE) {
            x = getWidth();
        } else {
            x =0;
        }

        // Comportamento in base all'angolazione del telefono asse y
        if (Math.abs(yAngle) <= MAX_ANGLE) {
            int deltaY = (int) (getHeight()/2* yAngle*-1 / MAX_ANGLE);
            y += deltaY;
        } else if (yAngle > MAX_ANGLE) {
            y = 0;
        } else {
            y = getHeight();
        }

        // Disegno del grafico generale
        canvas.drawCircle(x, y, circleRadius ,paint);
        @SuppressLint("DrawAllocation") Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.RED);
        p.setStrokeWidth((float) 10.0);
        @SuppressLint("DrawAllocation") RectF oval = new RectF( 0, 0, getWidth(), getHeight());
        canvas.drawRect(oval, p);
        invalidate();
    }

}
