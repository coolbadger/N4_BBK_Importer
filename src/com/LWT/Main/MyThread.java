package com.LWT.Main;

import com.LWT.Base.Global;
import com.LWT.Base.GlobalLogger;
import com.LWT.DataProcess.SNXProcess;
import com.LWT.ImporterUI.MainFrameInfo;

import java.util.Date;

/**
 * Created by Badger on 15/7/14.
 */
public class MyThread extends Thread {
    private MainFrameInfo mainFrameParameter;

    public MyThread(MainFrameInfo frameParameter){
        this.mainFrameParameter = frameParameter;
    }

    public void run(){
        startProcess();
    }

    public void startProcess() {

        GlobalLogger.myLogger.info("****‘ÿ»Î****");
        Global.mainFrameParameter = this.mainFrameParameter;

        nowStart();

        Date now = new Date();
        GlobalLogger.myLogger.info(" ***** " + now + " **** ");
    }

    private void nowStart(){

        GlobalLogger.myLogger.info("****Init****");

        Global.LOOP_SECONDS = 6;
        processStart();

        GlobalLogger.myLogger.info("****end****");

    }


    private void processStart() {

        SNXProcess snxProcess = new SNXProcess();
        snxProcess.trySendSNX();


    }


}
