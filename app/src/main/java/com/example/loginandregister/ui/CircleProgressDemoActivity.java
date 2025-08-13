package com.example.loginandregister.ui;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginandregister.view.CircleProgressBar;

public class CircleProgressDemoActivity extends AppCompatActivity {
    private CircleProgressBar circleProgressBar;
    private ValueAnimator progressAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.loginandregister.R.layout.activity_circle_progress_demo);

        circleProgressBar = findViewById(com.example.loginandregister.R.id.circleProgressBar);
        
        // 设置进度变化监听器
        circleProgressBar.setOnProgressChangedListener(new CircleProgressBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float progress) {
                // 可以在这里处理进度变化
                // Toast.makeText(CircleProgressDemoActivity.this, "进度: " + (int)progress + "%", Toast.LENGTH_SHORT).show();
            }
        });
        
        // 设置上下文菜单监听器
        circleProgressBar.setOnContextMenuListener(new CircleProgressBar.OnContextMenuListener() {
            @Override
            public void onContextMenuRequested(CircleProgressBar progressBar) {
                showContextMenu(progressBar);
            }
        });
        
        // 开始5秒进度动画
        startProgressAnimation();
    }
    
    private void showContextMenu(final CircleProgressBar progressBar) {
        // 暂停动画
        pauseProgressAnimation();
        
        PopupMenu popupMenu = new PopupMenu(this, progressBar);
        popupMenu.getMenuInflater().inflate(com.example.loginandregister.R.menu.menu_circle_progress, popupMenu.getMenu());
        
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == com.example.loginandregister.R.id.action_set_progress) {
                    // 显示进度调整对话框
                    showProgressAdjustDialog();
                    return true;
                } else if (itemId == com.example.loginandregister.R.id.action_reset_progress) {
                    progressBar.resetProgress();
                    // 重置进度后重新启动动画
                    if (progressAnimator != null) {
                        progressAnimator.cancel();
                    }
                    startProgressAnimation(); // 从0开始动画
                    return true;
                }
                return false;
            }
        });
        
        popupMenu.show();
    }
    
    
    private void startProgressAnimation() {
        startProgressAnimation(0f);
    }
    
    private void startProgressAnimation(float startProgress) {
        progressAnimator = ValueAnimator.ofFloat(startProgress, 100f);
        progressAnimator.setDuration((long) (5000 * (100 - startProgress) / 100)); // 根据剩余进度调整动画时长
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                circleProgressBar.setProgress(progress);
            }
        });
        progressAnimator.start();
    }
    
    private void pauseProgressAnimation() {
        if (progressAnimator != null && progressAnimator.isRunning()) {
            progressAnimator.pause();
        }
    }
    
    private void restartProgressAnimation() {
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
        startProgressAnimation(circleProgressBar.getProgress());
    }
    
    private void showProgressAdjustDialog() {
        // 创建对话框视图
        View dialogView = getLayoutInflater().inflate(com.example.loginandregister.R.layout.dialog_progress_adjust, null);
        
        // 获取视图中的组件
        final TextView progressText = dialogView.findViewById(com.example.loginandregister.R.id.progressText);
        final SeekBar seekBar = dialogView.findViewById(com.example.loginandregister.R.id.seekBar);
        Button btnConfirm = dialogView.findViewById(com.example.loginandregister.R.id.btnConfirm);
        
        // 设置SeekBar的初始进度为当前进度
        int currentProgress = (int) circleProgressBar.getProgress();
        seekBar.setProgress(currentProgress);
        progressText.setText(String.valueOf(currentProgress));
        
        // 设置SeekBar进度变化监听器
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressText.setText(String.valueOf(progress));
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // 创建并显示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        
        // 设置确定按钮点击事件
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置进度为SeekBar的值
                int progress = seekBar.getProgress();
                circleProgressBar.setProgress(progress);
                // 重新启动动画
                restartProgressAnimation();
                // 关闭对话框
                dialog.dismiss();
            }
        });
        
        dialog.show();
    }
}
