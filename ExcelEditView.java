package com.xxx.xxx;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * 此控件为均分输入框控件
 * 使用说明：XML文件中设置好文字大小，设置好宽度。高度使用wrap_content更佳，亦可设置固定高度
 * （随着输入的行数变化会导致高度成倍增加）
 * 允许设置每行显示的文字个数
 * 允许设置最多显示多少行
 * 允许设置密码符显示
 * 允许设置多行输入
 *
 * Created by yueer on 2015/10/22.
 */
public class ExcelEditView extends EditText {

    private int mMaxLength = 6;    //一行显示的最大字符数
    private int mColorId = Color.BLACK;      //字体颜色
    private boolean isPassword = false;    //是否需要显示密码符
    private float mHeight = 0.0f;       //默认情况的高度
    private int mMaxLine = 0;          //最大的行数：如果为0,---表示支持多行输入   不为0，--则为该行

    public ExcelEditView(Context context){
        super(context);
        init();
    }

    public ExcelEditView(Context context, AttributeSet set){
        super(context, set);
        init();
    }

    private void init(){
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                Editable editable = ExcelEditView.this.getText();
                int len = editable.length();

                if(mMaxLine > 0 && len > mMaxLength*mMaxLine)
                {
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    String newStr = str.substring(0,mMaxLength*mMaxLine);
                    ExcelEditView.this.setText(newStr);
                    editable = ExcelEditView.this.getText();

                    //新字符串的长度
                    int newLen = editable.length();
                    //旧光标位置超过字符串长度
                    if(selEndIndex > newLen)
                    {
                        selEndIndex = editable.length();
                    }
                    //设置新光标所在的位置
                    Selection.setSelection(editable, selEndIndex);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setIsPassword(boolean isPassword){
        this.isPassword = isPassword;
    }

    public void setmMaxLine(int line){
        this.mMaxLine = line;
    }

    public void setmMaxLength(int leng){
        this.mMaxLength = leng;
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        mColorId = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        char[] txt = this.getText().toString().toCharArray();      //取出字符数组
        int txtLine = getLineFromCharArray(txt);      //计算有多少行
        if (mMaxLine > 0 && txtLine > mMaxLine){  //进行行数的上限处理
            txtLine = mMaxLine;
        }
        if (this.isPassword){   //密码符的转义
            for (int i=0; i<txt.length; i++){
                txt[i] = '*';
            }
        }
        if (mHeight == 0){      //获取最初控件的高度
            mHeight = this.getHeight();
        }
        float width = this.getWidth();
        float height = mHeight * txtLine;
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = (int)height;
        this.setLayoutParams(params);       //动态设置控件高度
        float per = width / (mMaxLength+1);         //宽度等分
        float perHeight = height / (txtLine + 1);    //高度等分

        Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
        countPaint.setColor(mColorId);
        countPaint.setTextSize(this.getTextSize());
        countPaint.setTypeface(this.getTypeface());
        countPaint.setTextAlign(Paint.Align.CENTER);
        Rect textBounds = new Rect();
        String numberStr = "1";
        countPaint.getTextBounds(numberStr, 0, numberStr.length(), textBounds);//get text bounds, that can get the text width and height
        float textHeight = (float)(textBounds.bottom - textBounds.top);
        float textWidth = (float)(textBounds.right = textBounds.left);       //计算该控件中能够显示的单一文字的高度和宽度
        for (int line = 0; line < txtLine; line++) {
            for (int i = 0; i < mMaxLength && txt.length > (i+line*mMaxLength); i++) {
                canvas.drawText(String.valueOf(txt[i+line*mMaxLength]), (i + 1) * per - textWidth, perHeight * (line + 1) + textHeight / 2, countPaint);       //进行绘制
            }
        }
    }

    private int getLineFromCharArray(char[] txt){
        int line = ((txt.length - 1) / mMaxLength) + 1;
        return line;
    }
}
