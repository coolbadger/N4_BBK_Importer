package com.LWT.DataProcess

import com.LWT.Base.Global
import com.LWT.Base.GlobalLogger
import com.LWT.Entity.Result_BBK
import com.LWT.Entity.SNX_BBK_Unit
import groovy.sql.GroovyRowResult

class BaseDataProcess {
    boolean overWriteVesselVisit = true
    private SqlDataFunction lwtData
    private List<SNX_BBK_Unit> bbkUnitList

    BaseDataProcess() {
        lwtData = new SqlDataFunction()
    }

    List<SNX_BBK_Unit> getBBKUnitList() {
        return bbkUnitList
    }


    void refreshUnitList() {
        GlobalLogger.myLogger.info(" ***** update unit start: ***** ")
        bbkUnitList = new ArrayList<SNX_BBK_Unit>()

        //���������ݿ���ȡ������δ��������
        List<GroovyRowResult> Bulks = lwtData.getBulks();
        int CountNum = 1
        int TotalCount = Bulks.size()
        GlobalLogger.myLogger.info("����" + TotalCount + "������")
        Bulks.each { row ->
            //��ӡ�������
            GlobalLogger.myLogger.info("��������:" + CountNum++ + "/" + TotalCount, false)
            Result_BBK resultBBK = new Result_BBK(row)

            //��ʼ��SNX_BBK_UNIT
            SNX_BBK_Unit snx_bbk_unit = new SNX_BBK_Unit(resultBBK)

            if (resultBBK.OperateType == "I") {
                bbkUnitList.add(snx_bbk_unit)
            }
        }
        GlobalLogger.myLogger.info(" *****  update unit end:  ***** ", false)
    }


}