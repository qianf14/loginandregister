package com.example.loginandregister.utils;

import android.text.Html;
import android.util.Log;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Arrays;
import java.util.List;

/**
 * Markdown解析工具类，用于将Markdown文本转换为HTML格式
 */
public class MarkdownUtils {
    private static final String TAG = "MarkdownUtils";
    
    // Markdown解析器
    private static Parser parser;
    // HTML渲染器
    private static HtmlRenderer renderer;
    
    // 静态初始化
    static {
        try {
            // 创建扩展列表
            List<Extension> extensions = Arrays.asList(
                TablesExtension.create(),
                StrikethroughExtension.create()
            );
            
            // 创建解析器
            parser = Parser.builder()
                    .extensions(extensions)
                    .build();
            
            // 创建渲染器
            renderer = HtmlRenderer.builder()
                    .extensions(extensions)
                    .build();
        } catch (Exception e) {
            Log.e(TAG, "MarkdownUtils: 初始化解析器失败", e);
        }
    }
    
    /**
     * 将Markdown文本转换为HTML格式
     * @param markdown Markdown文本
     * @return HTML格式文本
     */
    public static String markdownToHtml(String markdown) {
        try {
            if (parser == null || renderer == null) {
                Log.e(TAG, "markdownToHtml: 解析器未正确初始化");
                return markdown;
            }
            
            // 解析Markdown文本
            Node document = parser.parse(markdown);
            
            // 渲染为HTML
            String html = renderer.render(document);
            
            Log.d(TAG, "markdownToHtml: Markdown解析完成");
            return html;
        } catch (Exception e) {
            Log.e(TAG, "markdownToHtml: Markdown解析失败", e);
            return markdown;
        }
    }
    
    /**
     * 将Markdown文本转换为格式化的CharSequence
     * @param markdown Markdown文本
     * @return 格式化的CharSequence
     */
    public static CharSequence markdownToFormattedText(String markdown) {
        try {
            String html = markdownToHtml(markdown);
            if (html != null) {
                // 将HTML转换为Spanned文本
                return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT);
            }
        } catch (Exception e) {
            Log.e(TAG, "markdownToFormattedText: 转换为格式化文本失败", e);
        }
        return markdown;
    }
}
