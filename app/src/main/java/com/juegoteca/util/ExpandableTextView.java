package com.juegoteca.util;


import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.mijuegoteca.R;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * User: Bazlur Rahman Rokon https://stackoverflow.com/a/18667717
 * Date: 9/7/13 - 3:33 AM
 */


public class ExpandableTextView extends TextView {
    private static final int DEFAULT_TRIM_LENGTH = 200;
    private static String ellipsis = "";

    private CharSequence originalText;
    private CharSequence trimmedText;
    private BufferType bufferType;
    private boolean trim = true;
    private int trimLength;

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ellipsis = context.getString(R.string.more);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        this.trimLength = typedArray.getInt(R.styleable.ExpandableTextView_trimLength, DEFAULT_TRIM_LENGTH);
        typedArray.recycle();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trim = !trim;
                setText();
                requestFocusFromTouch();
            }
        });
    }

    public void setText() {
        super.setText(getDisplayableText(), bufferType);
    }

    private CharSequence getDisplayableText() {
        return trim ? trimmedText : originalText;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        trimmedText = getTrimmedText(text);
        bufferType = type;
        setText();
    }

    private CharSequence getTrimmedText(CharSequence text) {
        if (originalText != null && originalText.length() > trimLength) {
            StringBuilder sb = new StringBuilder(ellipsis);
            SpannableString spanString = new SpannableString(sb.toString());
            spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
            String trimmed = text.toString().trim().replaceAll("(?<=.{"+trimLength+"})\\b.*", "... ");
            SpannableStringBuilder ssb = new SpannableStringBuilder(trimmed).append(spanString);
            return ssb;
        } else {
            return originalText;
        }
    }


    public CharSequence getOriginalText() {
        return originalText;
    }

    public void setTrimLength(int trimLength) {
        this.trimLength = trimLength;
        trimmedText = getTrimmedText(originalText);
        setText();
    }

    public int getTrimLength() {
        return trimLength;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }
}
