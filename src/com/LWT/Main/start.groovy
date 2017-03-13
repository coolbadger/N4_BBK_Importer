package com.LWT.Main

/**
 * Created by H on 2015/5/13.
 */
import com.LWT.Base.Global
import com.LWT.DataProcess.SNXProcess
import org.apache.log4j.Logger

class start {
    public static Logger startLogger = Logger.getLogger(start.class);

    static main(args) {
        if (args != []){
            String newSql = args[0].toString().replace('(~)',' ')
            Global.argoSQL = newSql
            startLogger.info("执行新查询为："+newSql)
        }else {
            startLogger.info("无传入参数，执行默认sql")
        }
        nowStart();
    }

    static void nowStart(){

        Global.myLogger.info("\n****Init****")

        Global.LOOP_SECONDS = 6
        new start().processStart()

        Global.myLogger.info("\n****end****")

        System.exit(0)
    }


    void processStart() {

        SNXProcess snxProcess = new SNXProcess()
        snxProcess.trySendSNX()

        Date now = new Date()
        println " ***** " + now + " **** "


//        try {
//
//            def loop_Seconds = Global.LOOP_SECONDS * 1000
//
//            while (true) {
//                Date now = new Date()
//                println " ***** " + now + " **** "
//                Thread.sleep(loop_Seconds)
//            }
//        } catch (Exception e) {
//            e.printStackTrace()
//            println "Exit!"
//            System.exit(0)
//        }
    }
}

