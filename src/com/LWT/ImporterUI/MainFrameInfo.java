package com.LWT.ImporterUI;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Badger on 15/7/15.
 */
public class MainFrameInfo {
    //��ʼ����������
    public boolean p_IsTestEnv;
    public boolean p_IsDateEnable;
    public boolean p_IsWriteBack;
    public boolean p_IsUnitCount;
    public String p_UnitCount;
    public String p_DateStart;
    public String p_DateEnd;

    public MainFrameInfo(){
        p_IsTestEnv = true;
        p_IsDateEnable = false;
        p_IsWriteBack = false;
        p_IsUnitCount = false;
        p_UnitCount = "";
        p_DateStart = "";
        p_DateEnd = "";
    }

    public static class IntNumberDocument extends PlainDocument{

        @Override
        public void insertString(int offset, String s, AttributeSet attrSet) throws BadLocationException {
            if (offset > 8) return;
            //���ַ��������������Ƿ���׳��쳣���׳��쳣��˵�����ַ���Ϊ���֣����أ���������ı�����
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                return;
            }
            super.insertString(offset, s, attrSet);
        }
    }


    public static class IntDateDocument extends PlainDocument{

        @Override
        public void insertString(int offset, String s, AttributeSet attrSet) throws BadLocationException {
            if (offset > 7) return;
            //���ַ��������������Ƿ���׳��쳣���׳��쳣��˵�����ַ���Ϊ���֣����أ���������ı�����
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                return;
            }
            super.insertString(offset, s, attrSet);
        }
    }
}
