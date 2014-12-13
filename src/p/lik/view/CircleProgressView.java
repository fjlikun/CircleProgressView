package p.lik.view;

import p.lik.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * @author lik
 * @since 2014年12月10日 上午10:19:12
 */

public class CircleProgressView extends View {
	// 颜色透明度mask
	static final int ALPHA_MASK = 0xff000000;
	// 圆形颜色
	private int mCircleColor = 0xffffffff;
	// 进度背景颜色
	private int mProgressBackgroundColor = 0xffe8e5e5;
	// 进度颜色
	private int mProgressColor = 0xffff6f48;
	// 字体颜色
	private int mTextColor = 0xffff6f48;
	private int mProgressSize = 16;

	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);;
	private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);;

	private RectF mCircleBound;
	private RectF mProgressBound;
	// 进度
	private int mProgress = 30;

	public CircleProgressView(Context context) {
		this(context, null);
	}

	public CircleProgressView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleProgressView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CircleProgressView);

		mProgress = a.getInteger(R.styleable.CircleProgressView_Progress, 0);
		mCircleColor = a
				.getColor(R.styleable.CircleProgressView_CircleColor, 0);
		mProgressBackgroundColor = a.getColor(
				R.styleable.CircleProgressView_ProgressBackgroundColor,
				0xffe8e5e5);
		mProgressColor = a.getColor(
				R.styleable.CircleProgressView_ProgressColor, 0xffff6f48);
		mProgressSize = (int) a.getDimension(
				R.styleable.CircleProgressView_ProgressSize, 6);

		if (a.hasValue(R.styleable.CircleProgressView_Text)) {
			// TODO 中间的文字需要自定义？？？
		}
		int textSize = (int) a.getDimension(
				R.styleable.CircleProgressView_TextSize, 36);
		mTextColor = a.getColor(R.styleable.CircleProgressView_TextColor,
				0xffff6f48);
		a.recycle();

		mPaint.setAntiAlias(true);

		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Style.FILL);
		mTextPaint.setColor(mTextColor);
		mTextPaint.setTextSize(textSize);
	}

	public void setCircleColor(int color) {
		mCircleColor = color;
	}

	public void setRimColor(int color) {
		mProgressBackgroundColor = color;
	}

	public void setProgressColor(int color) {
		mProgressColor = color;
	}

	public void setTextColor(int color) {
		mTextColor = color;
		mTextPaint.setColor(mTextColor);
	}

	public void setTextSize(int size) {
		mTextPaint.setTextSize(size);
	}

	public void setProgress(int progress) {
		mProgress = progress;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getMeasuredSize(widthMeasureSpec);
		int height = getMeasuredSize(heightMeasureSpec);
		
		int usageWidth = width - getPaddingLeft() - getPaddingRight();
		int usageHeight = height - getPaddingTop() - getPaddingBottom();
		int size = Math.max(usageWidth, usageHeight);
		
		setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
	}

	int getMeasuredSize(int sizeMeasureSpec) {
		int size = 0;
		int mode = MeasureSpec.getMode(sizeMeasureSpec);
		if (mode == MeasureSpec.EXACTLY) {
			size = MeasureSpec.getSize(sizeMeasureSpec);
		} else {
			size = Math
					.max(getDefaultMinimumSize(), getSuggestedMinimumWidth());
			if (mode == MeasureSpec.AT_MOST)
				size = Math.min(size, MeasureSpec.getSize(sizeMeasureSpec));
		}
		return size;
	}

	int getDefaultMinimumSize() {
		return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				60, getResources().getDisplayMetrics()) + .5f);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		int width = w - getPaddingLeft() - getPaddingRight();
		int height = h - getPaddingBottom() - getPaddingTop();
		int radius = Math.min(width, height) / 2;

		int cx = width / 2 + getPaddingLeft();
		int cy = height / 2 + getPaddingTop();

		mCircleBound = new RectF(cx - radius, cy - radius, cx + radius, cy
				+ radius);
		radius -= (mProgressSize / 2 + 1);
		mProgressBound = new RectF(cx - radius, cy - radius, cx + radius, cy
				+ radius);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint p = mPaint;

		// 进度内圆背景
		if (0 != (ALPHA_MASK & mCircleColor)) {
			p.setColor(mCircleColor);
			p.setStyle(Style.FILL);
			canvas.drawArc(mCircleBound, 0, 360, true, p);
		}

		// 进度条背景
		if (0 != (ALPHA_MASK & mProgressBackgroundColor) && 0 != mProgressSize) {
			p.setColor(mProgressBackgroundColor);
			p.setStyle(Style.STROKE);
			p.setStrokeWidth(mProgressSize);
			canvas.drawArc(mProgressBound, 360, 360, false, p);
		}

		// 进度
		float angle = mProgress / 100f * 360;
		if (0 != (ALPHA_MASK & mProgressColor) && 0 != mProgressSize) {
			p.setColor(mProgressColor);
			p.setStyle(Style.STROKE);
			p.setStrokeWidth(mProgressSize);
			canvas.drawArc(mProgressBound, -90, angle, false, p);
		}

		// 中间文字
		if (0 != (ALPHA_MASK & mTextColor)) {
			float textHeight = mTextPaint.descent() - mTextPaint.ascent();
			float verticalTextOffset = (textHeight / 2) - mTextPaint.descent();

			String progress = mProgress + "%";
			float horizontalTextOffset = mTextPaint.measureText(progress) / 2;
			canvas.drawText(progress, this.getWidth() / 2
					- horizontalTextOffset, this.getHeight() / 2
					+ verticalTextOffset, mTextPaint);
		}

	}

}
