package com.github.rongi.rotate_layout.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

//import com.github.rongi.rotate_layout.R;

import static android.view.View.MeasureSpec.UNSPECIFIED;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Rotates first view in this layout by specified angle.
 * <p>
 * This layout is supposed to have only one view. Behaviour of the views
 * after the first one is not defined.
 * <p>
 * XML attributes
 * See com.github.rongi.rotate_layout.R.styleable#RotateLayout RotateLayout Attributes,
 */
// the class is named RotateLayout but it is a ViewGroup that should have only one view inside
public class RotateLayout extends ViewGroup {

  private int angle;

  private final Matrix rotateMatrix = new Matrix();

  private final Rect viewRectRotated = new Rect();

  private final RectF tempRectF1 = new RectF();
  private final RectF tempRectF2 = new RectF();

  private final float[] viewTouchPoint = new float[2];
  private final float[] childTouchPoint = new float[2];

  private boolean angleChanged = true;

  public RotateLayout(Context context) {
    this(context, null);
  }

  public RotateLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RotateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs);
    // below instructions refer to angle attribute inside RotateLayout styleable structure in attrs.xml file
    final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RotateLayout);
    this.angle = a.getInt(R.styleable.RotateLayout_angle, 0);
    a.recycle();

    setWillNotDraw(false);
  }

  /**
   * Returns current angle of this layout
   */
  public int getAngle() {
    return angle;
  }

  /**
   * Sets current angle of this layout.
   */
  public void setAngle(int angle) {
    if (this.angle != angle) {
      this.angle = angle;
      this.angleChanged = true;
      requestLayout();
      invalidate();
    }
  }

  /**
   * Returns this layout's child view or null if there is not any
   */
  public View getChildView() {
    if (getChildCount() > 0) {
      return getChildAt(0);
    } else {
      return null;
    }
  }

  // This is a first phase of views positioning mechanism. Second phase is layout
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final View childView = getChildView();
    if (childView != null) {
      if (abs(angle % 180) == 0) { // standard perfect situation when angle is like 0 or 180 (or 360), divisible by 180 with no remainder
        // no inspection of SuspiciousNameCombination
        measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(
                resolveSize(childView.getMeasuredWidth(), widthMeasureSpec),
                resolveSize(childView.getMeasuredHeight(), heightMeasureSpec));
      } else if (abs(angle % 180) == 90) { // other perfect situation when child view is rotated perpendicularly like 90 or 270 degrees
        measureChild(childView, heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(
                resolveSize(childView.getMeasuredHeight(), widthMeasureSpec),
                resolveSize(childView.getMeasuredWidth(), heightMeasureSpec));
      } else {
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, UNSPECIFIED);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, UNSPECIFIED);
        measureChild(childView, childWidthMeasureSpec, childHeightMeasureSpec);

        // below are trigonometric calculations to create a larger rectangle that encompasses entirely smaller
        // child view's rectangle with some redundant remaining space not belonging to child viee's rectangle
        // imperfect situation compared to when angle is like 0, 90, 180, 270, 360 when there is no redundant space
        int measuredWidth = (int) ceil(childView.getMeasuredWidth() * abs(cos(angle_c()))
                + childView.getMeasuredHeight() * abs(sin(angle_c())));
        int measuredHeight = (int) ceil(childView.getMeasuredWidth() * abs(sin(angle_c()))
                + childView.getMeasuredHeight() * abs(cos(angle_c())));

        setMeasuredDimension(
          resolveSize(measuredWidth, widthMeasureSpec),
          resolveSize(measuredHeight, heightMeasureSpec));
      }
    } else {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
  }

  // This function is probably called when layout is constructed/inflated
  // This is a second phase of views positioning mechanism. First phase is measurement
  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    int layoutWidth = right - left;
    int layoutHeight = bottom - top; // definitely reversed Y axis direction

    if (this.angleChanged || changed) {
      final RectF layoutRect = tempRectF1;
      layoutRect.set(0, 0, layoutWidth, layoutHeight);
      final RectF layoutRectRotated = tempRectF2;
      rotateMatrix.setRotate(angle, layoutRect.centerX(), layoutRect.centerY());
      rotateMatrix.mapRect(layoutRectRotated, layoutRect);
      layoutRectRotated.round(this.viewRectRotated); // rounding all float coordinates into nearest, integer coordinates
      angleChanged = false;
    }

    final View childView = getChildView();
    if (childView != null) {
      int childLeft = (layoutWidth - childView.getMeasuredWidth()) / 2; // this equation causes the child to be aligned into center of parent layout
      int childTop = (layoutHeight - childView.getMeasuredHeight()) / 2; // this equation causes the child to be aligned into center of parent layout
      int childRight = childLeft + childView.getMeasuredWidth();
      int childBottom = childTop + childView.getMeasuredHeight();
      childView.layout(childLeft, childTop, childRight, childBottom); // this function repositions child's view in the scene
    }
  }

  // this function is almost identical as the one in answer mentioned here
  // https://stackoverflow.com/questions/1930963/rotating-a-view-in-android
  @Override
  protected void dispatchDraw(Canvas canvas) {
    canvas.save(); // rotation around the center of this layout/view group by -angle
    canvas.rotate(-angle, this.getWidth() / 2f, this.getHeight() / 2f);
    super.dispatchDraw(canvas);
    canvas.restore();
  }

  @Override
  public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
    invalidate();
    return super.invalidateChildInParent(location, dirty);
  }

  // this function remaps positions of touch events so that rotated child view is clicked in correct rotated position
  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    this.viewTouchPoint[0] = event.getX();
    this.viewTouchPoint[1] = event.getY();

    rotateMatrix.mapPoints(childTouchPoint, viewTouchPoint);

    event.setLocation(childTouchPoint[0], childTouchPoint[1]);
    boolean result = super.dispatchTouchEvent(event);
    event.setLocation(viewTouchPoint[0], viewTouchPoint[1]); // is this function call necessary?

    return result;
  }

  /**
   * Circle angle, from 0 to TAU
   */
  private Double angle_c() {
    // True circle constant, not that petty imposter known as "PI"
    double TAU = 2 * PI;
    return TAU * angle / 360;
  }

}
