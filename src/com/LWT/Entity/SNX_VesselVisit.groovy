package com.LWT.Entity

import com.LWT.DataProcess.SqlDataFunction
import com.LWT.DataProcess.TenTo36
import groovy.xml.MarkupBuilder

/**
 * Created by H on 2015/5/6.
 */
class SNX_VesselVisit {
    def SqlDataFunction sqlDataFun

    def VesselVisitID           //�������κ�
    def SNXVesselVisitID       //��־λɢ���ĺ��κţ�VesselVisitID +��S����
    def VisitPhase              //����״̬
    def VesselID                //�������
    def VesselIDConverted       //�������̺�Ĵ������
    def ServiceID               //����ID
    def LineOperator            //���߲�����(����˾)
    def OutCallNum              //���ں�������
    def OutVoyNum               //���ں���
    def InCallNum               //���ں�������
    def InVoyNum                //���ں���
    def ETA                     //Ԥ�Ƶָ�ʱ��
    def ETD                     //Ԥ�����ʱ��
    def ATA                     //ʵ�ʵ���ʱ��
    def ATD                     //ʵ�����ʱ��
    def TimeStartWork           //��ʼ����ʱ��
    def TimeEndWork             //��������ʱ��

    def VesselIDInSQL           //�����Ǳߵ�ȷ�����

    def IsInN4                  //N4ϵͳ���Ƿ����

    SNX_Vessel Vessel           //���ڶ�Ӧ��ֻ


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

    // ���ɶ�Vessel VisitԪ�ص�SNX
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
