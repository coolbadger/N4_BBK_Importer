package com.LWT.DataProcess

import com.LWT.Base.Global
import com.LWT.Base.GlobalLogger
import com.LWT.Base.N4Operator
import com.LWT.Entity.SNX_VesselVisit
import com.LWT.Entity.SNX_Vessel
import groovy.sql.Sql

class SNXProcess {
    private Sql bulkConn
    private Sql baseConn
    private N4Operator np

    private List<SNX_Vessel> sendedVesselList
    private List<SNX_VesselVisit> sendedVesselVisitList

    SNXProcess() {
        bulkConn = Global.bulkOracleConnection.conn
        baseConn = Global.baseOracleConnection.conn
        np = Global.n4Operator
    }

    def trySendSNX() {
        BaseDataProcess dataProcess = new BaseDataProcess()
        def msgInfo
        def sendResult
        String writeBackSqlStr

        dataProcess.refreshUnitList()

        msgInfo = " *****  Send start:  ***** "
        GlobalLogger.myLogger.info(msgInfo)

        sendedVesselList = new ArrayList<SNX_Vessel>()
        sendedVesselVisitList = new ArrayList<SNX_VesselVisit>()


        int CountNum = 1
        int TotalCount = dataProcess.bbkUnitList.size()
        dataProcess.bbkUnitList.each { unit ->
            //打印处理进度
            msgInfo = "处理发送中 :" + CountNum++ + "/" + TotalCount
            GlobalLogger.myLogger.info(msgInfo)
            //先检查船期信息
            unit.VesselVisitList.each { vesselVisit ->
                //防止船期ID为空
                if (vesselVisit.VesselVisitID == null) {
                    msgInfo = vesselVisit.VesselVisitID + "is null continue"
                    GlobalLogger.myLogger.info(msgInfo)
                    return
                }

                if (vesselVisit.IsInN4) {
                    msgInfo = "N4已存在该船期，船期ID：" + vesselVisit.VesselVisitID
                    GlobalLogger.myLogger.info(msgInfo)
                    return
                } else if (vesselVisitIsSended(vesselVisit)) {
                    msgInfo = "已发送过该船期，船期ID：" + vesselVisit.VesselVisitID
                    GlobalLogger.myLogger.info(msgInfo)
                    return
                } else {
                    SNX_Vessel vessel = vesselVisit.Vessel
                    if (vessel.IsInN4) {
                        msgInfo = "N4已存在该船，船ID：" + vessel.VesselID
                        GlobalLogger.myLogger.info(msgInfo)
                    } else if (vesselIsSended(vessel)) {
                        msgInfo = "已发送过该船，船ID：" + vessel.VesselID
                        GlobalLogger.myLogger.info(msgInfo)
                    } else {
                        //发送VesselSNX
                        sendSnx(vessel.createSNX(),"发送Unit信息成功")
//                        msgInfo = vessel.createSNX()
//                        GlobalLogger.myLogger.info(GlobalLogger.myLogger)
                        sendedVesselList.add(vessel)


                    }

                    //发送VesselVisitNSX
                    sendSnx(vesselVisit.createSNX(),"发送Unit信息成功")
//                    msgInfo = vesselVisit.createSNX()
//                    GlobalLogger.myLogger.info(msgInfo)

                    sendedVesselVisitList.add(vesselVisit)
                }

            }

            //发送UnitSNX
            if (unit.IBMode == "VESSEL")
                sendSnx(unit.createSNX(),"发送Unit信息成功")

            if (Global.WRITE_BACK) {
                writeBackSqlStr = "update SC_CARGO_INFO set SFHQ = 'Y' where JLBH = ${unit.UnitSQLID}"
                writeBackOracle(bulkConn, sendResult, writeBackSqlStr)
            }
        }


        msgInfo = " *****   Send End:   ***** "
        GlobalLogger.myLogger.info(msgInfo)
    }

    def sendSnx(Object inSNX, String message) {
        def result = "0"
//        result = np.sendRequestWithXml(inSNX)

        if (result.equals("0") || result.equals("1")) {
            GlobalLogger.myLogger.info(message)
            return result
        } else {
            // 导数据错误，直接返回
            GlobalLogger.myLogger.info("导数据有误！返回：")
            GlobalLogger.myLogger.info(result)
            GlobalLogger.myLogger.info("错误日志:")
            GlobalLogger.myLogger.info(np.PAYLOAD)
            GlobalLogger.myLogger.info(np.RESULTS)
            GlobalLogger.myLogger.info("SNX为：")
            GlobalLogger.myLogger.info(inSNX)
            return result
        }

    }

    private def writeBackOracle(Sql sqlConn, def inResult, String inSqlStr) {
        if (inResult.equals("0") || inResult.equals("1")) {
            try {
                sqlConn.executeUpdate(inSqlStr)
                sqlConn.commit()
                GlobalLogger.myLogger.info("SQL（已执行）为：")
                GlobalLogger.myLogger.info(inSqlStr)
            } catch (Exception e) {
                GlobalLogger.myLogger.info("回写数据库有误！")
                GlobalLogger.myLogger.info("SQL为：")
                GlobalLogger.myLogger.info(inSqlStr)
            }
        } else {
            GlobalLogger.myLogger.info("SQL（未执行）为：")
            GlobalLogger.myLogger.info(inSqlStr)
            return
        }
    }

    private boolean vesselIsSended(SNX_Vessel inVessel) {
        boolean isSended = false
        sendedVesselList.each { vessel ->
            if (vessel.VesselID.equals(inVessel.VesselID)) {
                isSended = true
            }
        }
        return isSended
    }

    private boolean vesselVisitIsSended(SNX_VesselVisit inVesselVisit) {
        boolean isSended = false
        sendedVesselVisitList.each { VV ->
            if (VV.VesselVisitID.equals(inVesselVisit.VesselVisitID)) {
                isSended = true
                return
            }
        }
        return isSended
    }
}
