package com.LWT.Entity

import com.LWT.DataProcess.SqlDataFunction
import com.LWT.DataProcess.TenTo36
import groovy.xml.MarkupBuilder

/**
 * Created by H on 2015/5/6.
 */
class SNX_VesselVisit {
    def SqlDataFunction sqlDataFun

    def VesselVisitID           //船舶航次号
    def SNXVesselVisitID       //标志位散货的航次号（VesselVisitID +“S”）
    def VisitPhase              //船期状态
    def VesselID                //船舶编号
    def VesselIDConverted       //编码缩短后的船舶编号
    def ServiceID               //航线ID
    def LineOperator            //航线操作者(船公司)
    def OutCallNum              //出口航次数量
    def OutVoyNum               //出口航次
    def InCallNum               //进口航次数量
    def InVoyNum                //进口航次
    def ETA                     //预计抵港时间
    def ETD                     //预计离港时间
    def ATA                     //实际到达时间
    def ATD                     //实际离港时间
    def TimeStartWork           //开始工作时间
    def TimeEndWork             //结束工作时间

    def VesselIDInSQL           //集团那边的确报编号

    def IsInN4                  //N4系统中是否存在

    SNX_Vessel Vessel           //船期对应船只


    def SNX_VesselVisit(def vesselVisitID) {
        sqlDataFun = new SqlDataFunction()
        Result_VesselVisit resultVesselVisit = new Result_VesselVisit(sqlDataFun.getVesselVisit(vesselVisitID))

        this.VesselVisitID = vesselVisitID
        this.SNXVesselVisitID = vesselVisitID + "S"
        this.VisitPhase = 'CLOSED'
        this.ServiceID = 'LWT'
        this.LineOperator = resultVesselVisit.LineOpID
        this.OutCallNum  = ''
        this.InCallNum = ''
        this.OutVoyNum = resultVesselVisit.OutVoyage
        this.InVoyNum = resultVesselVisit.InVoyage
        this.ETA = resultVesselVisit.ArriveDate


        this.Vessel = new SNX_Vessel(vesselVisitID)
        this.VesselID = this.Vessel.VesselID == null ? '' : this.Vessel.VesselID
        BigInteger vesselIDNum = new BigInteger(this.VesselID.toString())
        this.VesselIDConverted = TenTo36.baseString(vesselIDNum)

        this.IsInN4 = sqlDataFun.isVesselVisitInN4(vesselVisitID)

    }

    // 生成多Vessel Visit元素的SNX
    def createSNX() {
        def snxVVStringWriter = new StringWriter()
        def snx = new MarkupBuilder(snxVVStringWriter)
        snx.'argo:snx'('xmlns:argo': 'http://www.navis.com/argo', 'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance', 'xsi:schemaLocation': 'http://www.navis.com/argo snx.xsd') {
            'vessel-visit'(
                    'id': this.SNXVesselVisitID,
                    'visit-phase': this.VisitPhase,
                    'vessel-id': this.VesselID,
                    'service-id': this.ServiceID,
                    'operator-id': this.LineOperator,
                    'out-call-number': this.OutCallNum,
                    'out-voy-nbr': this.OutVoyNum,
                    'in-call-number': this.InCallNum,
                    'in-voy-nbr': this.InVoyNum,
                    'is-no-client-access': 'N',
                    'is-dray-off': 'N',
                    'is-common-carrier': 'N',
                    'facility': 'LWT',
                    'ETA': this.ETA,
                    // 'ETD':it.VV_ETD,
                    // 'ATA':it.VV_ETA,
                    // 'notes':it.VV_notes,
            )
        }
        return snxVVStringWriter.toString()
    }
}
