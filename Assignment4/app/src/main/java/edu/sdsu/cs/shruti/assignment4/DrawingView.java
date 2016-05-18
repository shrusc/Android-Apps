package edu.sdsu.cs.shruti.assignment4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawingView extends View implements SensorEventListener {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final int NO_OF_CIRCLES = 15;
    private final ArrayList<Circle> circlesArray = new ArrayList<>(NO_OF_CIRCLES);
    private final GestureDetector detector;
    private int screenWidth;
    private int screenHeight;
    private long lastUpdateTime = 0;
    private Circle touchedCircle = null;

    private class Circle {
        private float radius;
        private float centerX;
        private float centerY;
        private float velocityX = 0;
        private float velocityY = 0;
        private boolean circleToGrow = false;
        private boolean circleInMotion = false;


        Circle(float centerX, float centerY, float radius) {
            this.radius = radius;
            this.centerX = centerX;
            this.centerY = centerY;
        }

        public void setCircleToGrow (boolean circleToGrow) {
            this.circleToGrow = circleToGrow;
        }

        public void setVelocity (float velocityX, float velocityY) {
            this.velocityX = velocityX;
            this.velocityY = velocityY;
        }

        public float getMass() {
            return radius * radius * radius / 1000f;  // Normalize by a factor
        }

        public void resolveCollision (Circle circle) {
            float xDistance = this.centerX - circle.centerX;
            float yDistance = this.centerY - circle.centerY;
            float sumRadius = this.radius + circle.radius;
            float squareRadius = sumRadius * sumRadius;
            float distanceSquare = (xDistance * xDistance) + (yDistance * yDistance);
            if (distanceSquare <= squareRadius) {
                double xVelocityDifference = circle.velocityX - this.velocityX;
                double yVelocityDifference = circle.velocityY - this.velocityY;
                double dotProduct = xDistance * xVelocityDifference + yDistance * yVelocityDifference;

                //vector maths, used for checking if the objects moves towards one another.
                if(dotProduct > 0) {
                    double collisionScale = dotProduct / distanceSquare;
                    double xCollision = xDistance * collisionScale;
                    double yCollision = yDistance * collisionScale;
                    double combinedMass = this.getMass() + circle.getMass();
                    double collisionWeightForThis = 2 * circle.getMass() / combinedMass;
                    double collisionWeightForCircle = 2 * this.getMass() / combinedMass;

                    this.velocityX += collisionWeightForThis * xCollision;
                    this.velocityY += collisionWeightForThis * yCollision;
                    circle.velocityX -= collisionWeightForCircle * xCollision;
                    circle.velocityY -= collisionWeightForCircle * yCollision;

                    //Move the balls if they are stationary
                    if(this.velocityX != 0 || this.velocityY != 0)
                        this.circleInMotion = true;
                    if(circle.velocityX != 0 || circle.velocityY !=0)
                        circle.circleInMotion = true;
                }
            }
        }

        public void accelerate(float xAcceleration, float yAcceleration, double timeDeltaSeconds) {
            this.velocityX = (float) (this.velocityX - xAcceleration * timeDeltaSeconds);
            this.velocityY = (float) (this.velocityY + yAcceleration * timeDeltaSeconds);
            this.centerX = (float) (this.centerX + 2 * this.velocityX * timeDeltaSeconds);
            this.centerY = (float) (this.centerY + 2 * this.velocityY * timeDeltaSeconds);
        }

        public void bounceIfHitEdge() {
            if ((this.centerX + this.radius) > screenWidth || (this.centerX - this.radius) < 0) {
                this.velocityX = (float) (-0.95 * this.velocityX);
                if (this.centerX + this.radius > screenWidth) {
                    this.centerX = screenWidth - this.radius;
                } else if (this.centerX - this.radius < 0) {
                    this.centerX = this.radius;
                }
            }
            if ((this.centerY + this.radius) > screenHeight || (this.centerY - this.radius) < 0) {
                this.velocityY = (float) (-0.95 * this.velocityY);
                if (this.centerY + this.radius > screenHeight) {
                    this.centerY = screenHeight - this.radius;
                } else if (this.centerY - this.radius < 0) {
                    this.centerY = this.radius;
                }
            }
            if (Math.abs(this.velocityX) < 0.01)
                this.velocityX = 0;
            if (Math.abs(this.velocityY) < 0.01)
                this.velocityY = 0;
            if(this.velocityX == 0 && this.velocityY == 0)
                this.circleInMotion = false;
        }

        @Override
        public String toString() {
            return "Circle[" + centerX + ", " + centerY + ", " + radius + "]";
        }
    }

    public DrawingView(Context context) {
        super(context);
        paint.setColor(Color.RED);
        detector = new GestureDetector(context, new GestureListener());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        screenWidth = canvas.getWidth();
        screenHeight = canvas.getHeight();
        for (Circle circle : circlesArray) {
            canvas.drawCircle(circle.centerX, circle.centerY, circle.radius, paint);
            if (circle.circleToGrow) {
                circle.radius += 10;
            }
            if (circle.circleInMotion) {
                circle.centerX += circle.velocityX;
                circle.centerY += circle.velocityY;
                circle.bounceIfHitEdge();
            }
        }
        for (int i = 0; i < circlesArray.size(); i++) {
            for (int j = i+1; j < circlesArray.size(); j++) {
                circlesArray.get(i).resolveCollision(circlesArray.get(j));
            }
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        super.onTouchEvent(event);
        detector.onTouchEvent(event);
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
                touchedCircle = getTouchedCircle(event.getX(), event.getY());
                if(null == touchedCircle) {
                    if (circlesArray.size() != NO_OF_CIRCLES) {
                        float radius = 30;
                        Circle newCircle = new Circle(event.getX(), event.getY(), radius);
                        newCircle.setCircleToGrow(true);
                        circlesArray.add(newCircle);
                        invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(null != touchedCircle) {
                    if(ifCircleInBounds(event.getX(), event.getY(), touchedCircle.radius)) {
                        touchedCircle.centerX = event.getX();
                        touchedCircle.centerY = event.getY();
                        invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                for (Circle circle : circlesArray) {
                    circle.setCircleToGrow(false);
                }
                break;

            default:
                break;
        }
        return true;
    }

    private Circle getTouchedCircle(float xTouch, float yTouch) {
        Circle touched = null;
        for (Circle circle : circlesArray) {
            if ((circle.centerX - xTouch)*(circle.centerX - xTouch) + (circle.centerY - yTouch)*(circle.centerY - yTouch)
                    <= circle.radius * circle.radius) {
                touched = circle;
                break;
            }
        }
        return touched;
    }

    private boolean ifCircleInBounds(float x, float y, float radius) {
        boolean inBounds = true;
        if ((x + radius) > screenWidth  || (x - radius < 0)) {
           inBounds = false;
        }
        else if ((y + radius) > screenHeight || (y - radius) < 0) {
            inBounds = false;
        }
        return inBounds;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent startEvent, MotionEvent endEvent, float velocityX, float velocityY) {
            if(null == touchedCircle) {
                return false;
            }
            else {
                touchedCircle.circleInMotion = true;
                touchedCircle.setVelocity(velocityX / 8, velocityY / 8);
                invalidate();
                return true;
            }
        }
    }

    public void onSensorChanged(SensorEvent event) {
        if (lastUpdateTime == 0) {
            lastUpdateTime = event.timestamp;
            return;
        }
        long timeDelta = event.timestamp - lastUpdateTime;
        lastUpdateTime = event.timestamp;
        float xAcceleration = round(event.values[0]);
        float yAcceleration = round(event.values[1]);
        for (Circle circle : circlesArray) {
            if (circle.circleInMotion) {
                circle.accelerate(xAcceleration, yAcceleration, timeDelta/1000000000.0f);
            }
        }
        for (int i = 0; i < circlesArray.size(); i++) {
            for (int j = i+1; j < circlesArray.size(); j++) {
                circlesArray.get(i).resolveCollision(circlesArray.get(j));
            }
        }
        invalidate();
    }

    private float round(float value) {
        return Math.round(value*100)/100.0f;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void clearCircles() {
        circlesArray.clear();
    }

}
