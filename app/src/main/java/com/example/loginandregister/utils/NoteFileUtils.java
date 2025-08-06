package com.example.loginandregister.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 笔记文件操作工具类，用于加载、保存和导出笔记内容。
 */
public class NoteFileUtils {
    private static final String TAG = "NoteFileUtils";
    // 内部存储文件名
    private static final String INTERNAL_NOTE_FILE = "note.md";
    // 外部存储目录名
    private static final String EXTERNAL_NOTES_DIR = "Notes";
    // 外部存储文件名前缀
    private static final String EXTERNAL_NOTE_FILE_PREFIX = "note_";

    /**
     * 笔记加载回调接口
     */
    public interface NoteLoadCallback {
        void onSuccess(String content);
        void onError(Exception e);
    }

    /**
     * 笔记保存回调接口
     */
    public interface NoteSaveCallback {
        void onSuccess();
        void onError(Exception e);
    }

    /**
     * 笔记导出回调接口
     */
    public interface NoteExportCallback {
        void onSuccess();
        void onError(Exception e);
    }

    /**
     * 异步加载笔记内容
     * @param context 上下文
     * @param callback 加载回调
     */
    public static void loadNoteAsync(Context context, NoteLoadCallback callback) {
        ThreadPoolUtils.getInstance().execute(() -> {
            try {
                String content = loadNote(context);
                callback.onSuccess(content);
            } catch (Exception e) {
                Log.e(TAG, "loadNoteAsync: 加载笔记失败", e);
                callback.onError(e);
            }
        });
    }

    /**
     * 异步保存笔记内容
     * @param context 上下文
     * @param content 笔记内容
     * @param callback 保存回调
     */
    public static void saveNoteAsync(Context context, String content, NoteSaveCallback callback) {
        ThreadPoolUtils.getInstance().execute(() -> {
            try {
                saveNote(context, content);
                callback.onSuccess();
            } catch (Exception e) {
                Log.e(TAG, "saveNoteAsync: 保存笔记失败", e);
                callback.onError(e);
            }
        });
    }

    /**
     * 异步导出笔记内容
     * @param context 上下文
     * @param content 笔记内容
     * @param callback 导出回调
     */
    public static void exportNoteAsync(Context context, String content, NoteExportCallback callback) {
        ThreadPoolUtils.getInstance().execute(() -> {
            try {
                exportNote(context, content);
                callback.onSuccess();
            } catch (Exception e) {
                Log.e(TAG, "exportNoteAsync: 导出笔记失败", e);
                callback.onError(e);
            }
        });
    }

    /**
     * 从内部存储加载笔记内容
     * @param context 上下文
     * @return 笔记内容
     * @throws IOException IO异常
     */
    private static String loadNote(Context context) throws IOException {
        Log.d(TAG, "loadNote: 开始从内部存储加载笔记");
        File file = new File(context.getFilesDir(), INTERNAL_NOTE_FILE);
        
        if (!file.exists()) {
            Log.d(TAG, "loadNote: 笔记文件不存在，返回空字符串");
            return "";
        }
        
        StringBuilder content = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        
        Log.d(TAG, "loadNote: 笔记加载完成，内容长度=" + content.length());
        return content.toString();
    }

    /**
     * 保存笔记内容到内部存储
     * @param context 上下文
     * @param content 笔记内容
     * @throws IOException IO异常
     */
    private static void saveNote(Context context, String content) throws IOException {
        Log.d(TAG, "saveNote: 开始保存笔记到内部存储");
        File file = new File(context.getFilesDir(), INTERNAL_NOTE_FILE);
        
        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {
            osw.write(content);
        }
        
        Log.d(TAG, "saveNote: 笔记保存完成");
    }

    /**
     * 导出笔记内容到外部存储
     * @param context 上下文
     * @param content 笔记内容
     * @throws IOException IO异常
     */
    private static void exportNote(Context context, String content) throws IOException {
        Log.d(TAG, "exportNote: 开始导出笔记到外部存储");
        
        // 检查外部存储是否可用
        if (!isExternalStorageWritable()) {
            throw new IOException("外部存储不可写");
        }
        
        // 创建导出目录
        File notesDir = new File(context.getExternalFilesDir(null), EXTERNAL_NOTES_DIR);
        if (!notesDir.exists()) {
            if (!notesDir.mkdirs()) {
                throw new IOException("无法创建导出目录");
            }
        }
        
        // 生成文件名（带时间戳）
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = EXTERNAL_NOTE_FILE_PREFIX + timestamp + ".md";
        File file = new File(notesDir, fileName);
        
        // 写入文件
        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {
            osw.write(content);
        }
        
        Log.d(TAG, "exportNote: 笔记导出完成，文件路径=" + file.getAbsolutePath());
    }

    /**
     * 检查外部存储是否可写
     * @return true表示可写，false表示不可写
     */
    private static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}
