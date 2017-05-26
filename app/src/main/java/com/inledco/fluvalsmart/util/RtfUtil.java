package com.inledco.fluvalsmart.util;

import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

/**
 * 富文本工具类 用于同一个组件显示不同颜色,大小的文本等
 * Created by liruya on 2016/9/7.
 */
public class RtfUtil
{
    /**
     * 富文本方法
     * @param style       被追加的富文本
     * @param add         新增的文本String类型
     * @param textColor   新增文本颜色
     * @param textScale   新增文本字体大小 (相对大小)
     * @param bgColor     新增文本背景颜色
     * @param strike      是否使用删除线
     * @param underline   是否使用下划线
     * @param font        新增文本字体
     * @param draw        新增的图片
     * @return
     */
    public static SpannableStringBuilder getRtf(SpannableStringBuilder style,
                                                String add,
                                                int textColor,
                                                float textScale,
                                                int bgColor,
                                                boolean strike,
                                                boolean underline,
                                                int font,
                                                Drawable draw)
    {
        int start;
        int end;
        if (add == null || add.equals(""))
        {
            if (draw == null)
            {
                return style;
            }
            else
            {
                add = "";
            }
        }
        if (style == null)
        {
            style = new SpannableStringBuilder();
        }
        start = style.length();
        end = start + add.length();
        style.append(add);
        style.setSpan( new ForegroundColorSpan( textColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan( new RelativeSizeSpan( textScale), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan( new BackgroundColorSpan( bgColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (strike)
        {
            style.setSpan( new StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (underline)
        {
            style.setSpan( new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        style.setSpan( new StyleSpan( font), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (draw != null)
        {
            style.append("\r\n");
            draw.setBounds(0, 0, 384, 256);
            style.setSpan( new ImageSpan( draw), end, end+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return style;
    }
}
