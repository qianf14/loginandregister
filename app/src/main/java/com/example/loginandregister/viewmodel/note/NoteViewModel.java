package com.example.loginandregister.viewmodel.note;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.loginandregister.utils.NoteFileUtils;

/**
 * 笔记功能的ViewModel，负责笔记内容的加载、保存和导出。
 * 通过LiveData将笔记内容和操作结果通知UI。
 */
public class NoteViewModel extends AndroidViewModel {
    private static final String TAG = "NoteViewModel";
    // 笔记内容
    private final MutableLiveData<String> noteContent = new MutableLiveData<>();
    // 加载状态
    private final MutableLiveData<Boolean> loadingState = new MutableLiveData<>();
    // 保存结果
    private final MutableLiveData<Boolean> saveResult = new MutableLiveData<>();
    // 导出结果
    private final MutableLiveData<Boolean> exportResult = new MutableLiveData<>();

    /**
     * 构造方法
     */
    public NoteViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 获取笔记内容的LiveData
     */
    public LiveData<String> getNoteContent() {
        return noteContent;
    }

    /**
     * 获取加载状态的LiveData
     */
    public LiveData<Boolean> getLoadingState() {
        return loadingState;
    }

    /**
     * 获取保存结果的LiveData
     */
    public LiveData<Boolean> getSaveResult() {
        return saveResult;
    }

    /**
     * 获取导出结果的LiveData
     */
    public LiveData<Boolean> getExportResult() {
        return exportResult;
    }

    /**
     * 加载笔记内容
     */
    public void loadNote() {
        Log.d(TAG, "loadNote: 开始加载笔记");
        loadingState.setValue(true);
        
        NoteFileUtils.loadNoteAsync(getApplication(), new NoteFileUtils.NoteLoadCallback() {
            @Override
            public void onSuccess(String content) {
                Log.d(TAG, "loadNote: 笔记加载成功");
                noteContent.postValue(content);
                loadingState.postValue(false);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "loadNote: 笔记加载失败", e);
                noteContent.postValue("");
                loadingState.postValue(false);
            }
        });
    }

    /**
     * 保存笔记内容
     * @param content 笔记内容
     */
    public void saveNote(String content) {
        Log.d(TAG, "saveNote: 开始保存笔记");
        
        NoteFileUtils.saveNoteAsync(getApplication(), content, new NoteFileUtils.NoteSaveCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "saveNote: 笔记保存成功");
                saveResult.postValue(true);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "saveNote: 笔记保存失败", e);
                saveResult.postValue(false);
            }
        });
    }

    /**
     * 导出笔记内容
     * @param content 笔记内容
     */
    public void exportNote(String content) {
        Log.d(TAG, "exportNote: 开始导出笔记");
        
        NoteFileUtils.exportNoteAsync(getApplication(), content, new NoteFileUtils.NoteExportCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "exportNote: 笔记导出成功");
                exportResult.postValue(true);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "exportNote: 笔记导出失败", e);
                exportResult.postValue(false);
            }
        });
    }
}
