package com.LWT.Entity

import groovy.sql.GroovyRowResult

/**
 * Created by H on 2015/5/6.
 */
class Result_Vessel {
    def VesselID        //�������
    def VesselCN        //������
    def VesselEN        //Ӣ����
    def CallSign        //����
    def Country         //����
    def ShipCompany     //����˾
    def VesselType      //������

    def VesselClassID   //�����ͱ��
    def LineOperator    //����˾

    Result_Vessel(GroovyRowResult rowResult) {
        if (rowResult != null) {
            this.VesselID = rowResult['VESSELID']
            this.VesselCN = rowResult['VESSELCN']
            this.VesselEN = rowResult['VESSELEN']
            this.CallSign = rowResult['CALLSIGN']
            this.Country = rowResult['COUNTRY']
            this.ShipCompany = rowResult['SHIPCOMPANY']
            this.VesselType = rowResult['VESSELTYPE']

            this.VesselClassID = "AB"
            this.LineOperator = "-1"

        }
    }
}
