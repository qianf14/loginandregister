package com.example.loginandregister.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.loginandregister.R;

public class CircleProgressBar extends View {
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;
    private RectF rectF;
    
    private float progress = 0f;
    private float maxProgress = 100f;
    private float minProgress = 0f;
    private float strokeWidth = 20f;
    
    private int backgroundColor = Color.parseColor("#E0E0E0");
    private int startColor = Color.GREEN;
    private int endColor = Color.RED;
    
    private boolean showText = true;
    private String progressTextSuffix = "%";
    
    private OnProgressChangedListener progressChangedListener;
    private GestureDetector gestureDetector;
    
    public CircleProgressBar(Context context) {
        super(context);
        init(null);
    }
    
    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }
    
    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    
    private void init(AttributeSet attrs) {
        // 初始化画笔
        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setColor(backgroundColor);
        
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        
        rectF = new RectF();
        
        // 初始化手势检测器
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                showContextMenu();
            }
        });
        
        // 从XML属性中读取自定义属性
        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.CircleProgressBar,
                    0, 0);
            
            try {
                progress = a.getFloat(R.styleable.CircleProgressBar_progress, progress);
                maxProgress = a.getFloat(R.styleable.CircleProgressBar_maxProgress, maxProgress);
                minProgress = a.getFloat(R.styleable.CircleProgressBar_minProgress, minProgress);
                strokeWidth = a.getDimension(R.styleable.CircleProgressBar_strokeWidth, strokeWidth);
                backgroundColor = a.getColor(R.styleable.CircleProgressBar_backgroundColor, backgroundColor);
                startColor = a.getColor(R.styleable.CircleProgressBar_startColor, startColor);
                endColor = a.getColor(R.styleable.CircleProgressBar_endColor, endColor);
                showText = a.getBoolean(R.styleable.CircleProgressBar_showText, showText);
                progressTextSuffix = a.getString(R.styleable.CircleProgressBar_progressTextSuffix);
                if (progressTextSuffix == null) {
                    progressTextSuffix = "%";
                }
            } finally {
                a.recycle();
            }
        }
        
        // 更新画笔属性
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setColor(backgroundColor);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(centerX, centerY) - strokeWidth / 2;
        
        // 设置矩形边界
        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        
        // 绘制背景环
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);
        
        // 绘制进度环
        if (progress > 0) {
            // 创建颜色渐变，调整起始位置使0%左右两边颜色不一样
            Shader shader = new SweepGradient(centerX, centerY, new int[]{startColor, endColor}, null);
            
            // 旋转渐变以调整起始位置到12点钟方向
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            matrix.postRotate(-90, centerX, centerY);
            shader.setLocalMatrix(matrix);
            
            progressPaint.setShader(shader);
            
            // 计算扫描角度
            float sweepAngle = 360 * (progress / maxProgress);
            
            // 从12点钟方向开始绘制进度弧（-90度）
            canvas.drawArc(rectF, -90, sweepAngle, false, progressPaint);
        }
        
        // 绘制进度文本
        if (showText) {
            String text = Math.round(progress) + progressTextSuffix;
            canvas.drawText(text, centerX, centerY + (textPaint.descent() - textPaint.ascent()) / 2, textPaint);
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }
    
    public boolean showContextMenu() {
        // 子类需要重写此方法以显示上下文菜单
        if (contextMenuListener != null) {
            contextMenuListener.onContextMenuRequested(this);
            return true;
        }
        return false;
    }
    
    // Getter和Setter方法
    public float getProgress() {
        return progress;
    }
    
    public void setProgress(float progress) {
        this.progress = Math.max(minProgress, Math.min(maxProgress, progress));
        invalidate();
        
        if (progressChangedListener != null) {
            progressChangedListener.onProgressChanged(this.progress);
        }
    }
    
    public void resetProgress() {
        setProgress(0);
    }
    
    public float getMaxProgress() {
        return maxProgress;
    }
    
    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }
    
    public float getMinProgress() {
        return minProgress;
    }
    
    public void setMinProgress(float minProgress) {
        this.minProgress = minProgress;
        invalidate();
    }
    
    public float getStrokeWidth() {
        return strokeWidth;
    }
    
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        backgroundPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeWidth(strokeWidth);
        invalidate();
    }
    
    public int getBackgroundColor() {
        return backgroundColor;
    }
    
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        backgroundPaint.setColor(backgroundColor);
        invalidate();
    }
    
    public int getStartColor() {
        return startColor;
    }
    
    public void setStartColor(int startColor) {
        this.startColor = startColor;
        invalidate();
    }
    
    public int getEndColor() {
        return endColor;
    }
    
    public void setEndColor(int endColor) {
        this.endColor = endColor;
        invalidate();
    }
    
    public boolean isShowText() {
        return showText;
    }
    
    public void setShowText(boolean showText) {
        this.showText = showText;
        invalidate();
    }
    
    public String getProgressTextSuffix() {
        return progressTextSuffix;
    }
    
    public void setProgressTextSuffix(String progressTextSuffix) {
        this.progressTextSuffix = progressTextSuffix;
        invalidate();
    }
    
    public void setOnProgressChangedListener(OnProgressChangedListener listener) {
        this.progressChangedListener = listener;
    }
    
    public void setOnContextMenuListener(OnContextMenuListener listener) {
        this.contextMenuListener = listener;
    }
    
    // 回调接口
    public interface OnProgressChangedListener {
        void onProgressChanged(float progress);
    }
    
    private OnContextMenuListener contextMenuListener;
    
    public interface OnContextMenuListener {
        void onContextMenuRequested(CircleProgressBar progressBar);
    }
}
