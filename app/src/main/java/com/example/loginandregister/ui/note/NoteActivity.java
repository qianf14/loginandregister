package com.example.loginandregister.ui.note;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginandregister.R;
import com.example.loginandregister.viewmodel.note.NoteViewModel;
import com.example.loginandregister.utils.MarkdownUtils;
import com.google.android.material.tabs.TabLayout;

/**
 * 笔记主界面Activity，负责笔记编辑和导出功能。
 * 通过NoteViewModel管理所有笔记相关数据和事件，MVVM解耦。
 */
public class NoteActivity extends AppCompatActivity {
    private static final String TAG = "NoteActivity";
    private static final int EXPORT_REQUEST_CODE = 1001;
    
    // UI控件
    private Toolbar toolbar;
    private EditText etNoteContent;
    private TextView tvPreviewContent;
    private ScrollView editView;
    private ScrollView previewView;
    private TabLayout tabLayout;
    
    // 笔记ViewModel
    private NoteViewModel noteViewModel;
    
    // 当前是否为预览模式
    private boolean isPreviewMode = false;

    /**
     * Activity生命周期-创建，初始化UI和ViewModel，设置事件监听。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        
        // 设置Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // 设置标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("笔记");
        }
        
        // 设置白天模式下Toolbar字体为黑色
        setToolbarTextColorForDayMode();

        // 绑定布局控件
        initViews();
        
        // 初始化NoteViewModel
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        // 观察ViewModel的LiveData，自动响应UI变化
        observeViewModel();
        
        // 设置事件监听器
        setupListeners();

        // 加载笔记内容
        noteViewModel.loadNote();
    }
    
    /**
     * 设置白天模式下Toolbar字体为黑色
     */
    private void setToolbarTextColorForDayMode() {
        // 检查当前是否为白天模式
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if (nightMode == AppCompatDelegate.MODE_NIGHT_NO || 
            (nightMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM && 
             getResources().getConfiguration().uiMode % 16 == 0)) {
            // 白天模式下设置Toolbar字体为黑色
            if (toolbar != null) {
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
                // 设置菜单图标颜色为黑色
                if (toolbar.getOverflowIcon() != null) {
                    toolbar.getOverflowIcon().setTint(ContextCompat.getColor(this, R.color.black));
                }
            }
        }
    }
    
    /**
     * 初始化视图控件
     */
    private void initViews() {
        etNoteContent = findViewById(R.id.etNoteContent);
        tvPreviewContent = findViewById(R.id.tvPreviewContent);
        editView = findViewById(R.id.editView);
        previewView = findViewById(R.id.previewView);
        tabLayout = findViewById(R.id.tabLayout);
    }
    
    /**
     * 观察ViewModel的LiveData
     */
    private void observeViewModel() {
        noteViewModel.getNoteContent().observe(this, content -> {
            if (content != null) {
                etNoteContent.setText(content);
                etNoteContent.setSelection(content.length()); // 将光标移到末尾
                updatePreview(content); // 更新预览
            }
        });
        
        noteViewModel.getLoadingState().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                // 显示加载状态
                Toast.makeText(this, "正在加载笔记...", Toast.LENGTH_SHORT).show();
            }
        });
        
        noteViewModel.getSaveResult().observe(this, success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(this, "笔记已保存", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        noteViewModel.getExportResult().observe(this, success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(this, "笔记已导出", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "导出失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    /**
     * 设置事件监听器
     */
    private void setupListeners() {
        // TabLayout选择监听器
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // 编辑模式
                        editView.setVisibility(View.VISIBLE);
                        previewView.setVisibility(View.GONE);
                        isPreviewMode = false;
                        break;
                    case 1: // 预览模式
                        editView.setVisibility(View.GONE);
                        previewView.setVisibility(View.VISIBLE);
                        isPreviewMode = true;
                        // 更新预览内容
                        updatePreview(etNoteContent.getText().toString());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 不需要处理
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 不需要处理
            }
        });
        
        // EditText文本变化监听器，实现实时预览
        etNoteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 不需要处理
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 实时更新预览（仅在预览模式下）
                if (isPreviewMode) {
                    updatePreview(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 不需要处理
            }
        });
    }
    
    /**
     * 更新预览内容
     * @param markdown Markdown文本
     */
    private void updatePreview(String markdown) {
        if (markdown == null) return;
        
        try {
            // 使用MarkdownUtils转换为格式化文本
            CharSequence formattedText = MarkdownUtils.markdownToFormattedText(markdown);
            tvPreviewContent.setText(formattedText);
        } catch (Exception e) {
            Log.e(TAG, "updatePreview: 更新预览失败", e);
            tvPreviewContent.setText(markdown);
        }
    }

    /**
     * 创建菜单选项
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    /**
     * 处理菜单项点击事件
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            // 保存笔记并返回
            saveNote();
            finish();
            return true;
        } else if (id == R.id.action_save) {
            // 保存笔记
            saveNote();
            return true;
        } else if (id == R.id.action_export) {
            // 导出笔记为文本
            exportNote();
            return true;
        } else if (id == R.id.action_export_pdf) {
            // 导出笔记为PDF
            exportNoteAsPdf();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * 导出笔记为文本
     */
    private void exportNote() {
        String content = etNoteContent.getText().toString();
        noteViewModel.exportNote(content);
    }
    
    /**
     * 导出笔记为PDF
     */
    private void exportNoteAsPdf() {
        String content = etNoteContent.getText().toString();
        noteViewModel.exportNoteAsPdf(content);
    }

    /**
     * 保存笔记内容
     */
    private void saveNote() {
        String content = etNoteContent.getText().toString();
        noteViewModel.saveNote(content);
    }

    /**
     * Activity生命周期-暂停，自动保存笔记
     */
    @Override
    protected void onPause() {
        super.onPause();
        saveNote();
    }
    
    /**
     * 处理Activity返回结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == EXPORT_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                // 用户选择了导出位置，可以在这里处理文件导出
                // 这里可以调用NoteViewModel的导出方法
                String content = etNoteContent.getText().toString();
                noteViewModel.exportNote(content);
            }
        }
    }
}
