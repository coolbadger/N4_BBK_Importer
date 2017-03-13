package com.LWT.Entity

import groovy.sql.GroovyRowResult

/**
 * Created by H on 2015/5/6.
 */
class Result_VesselVisit {
    def VesselVisitID       //���κ�
    def VesselID            //�������
    def LineOpID            //����˾
    def Country             //����
    def Terminal            //������ͷ�����,���壺4617��
    def ArriveDate          //�ִ�ʱ��
    def LeaveDate           //�뿪ʱ��

    def OutVoyage           //���ں���
    def InVoyage            //���ں���
    Result_VesselVisit(GroovyRowResult rowResult) {
        if (rowResult != null) {
            this.VesselVisitID = rowResult['SHIPDICTID']
            this.VesselID = rowResult['VESSELID']
            this.LineOpID = rowResult['SHIPCOMPANY']
            this.Country = rowResult['COUNTRY']
            this.OutVoyage = rowResult['EXVOYAGE']
            this.InVoyage = rowResult['INVOYAGE']
            this.Terminal = rowResult['TERMINAL']

            this.ArriveDate = rowResult['ARRBERTHDATE'].toString()[0..-3]
            this.LeaveDate = this.ArriveDate
        }
    }
}
