package com.LWT.Logger;

import org.apache.log4j.Logger;

/**
 * Created by Badger on 15/7/14.
 */
public class MyLogger {
    private static Logger myLogger = Logger.getLogger(MyLogger.class);

    private MyLoggerListener myLoggerListener;

    public MyLogger() {

    }

    public void addListener(MyLoggerListener _listener) {
        this.myLoggerListener = _listener;

    }

    //信息内容以及是否回写文件
    public void info(String infoMessage, boolean writeFile) {
        infoMessage += "\n";
        myLoggerListener.LogInfo(infoMessage);
        if (writeFile) {
            myLogger.info(infoMessage);
        }
        else{
            System.out.print(infoMessage);
        }
    }

    public void info(String infoMessage) {
        info(infoMessage, true);
    }

    public void error(String errorMessage) {
        errorMessage += "\n";
        myLoggerListener.LogError(errorMessage);
        myLogger.error(errorMessage);
    }
}
