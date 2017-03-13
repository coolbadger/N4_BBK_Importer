package com.LWT.Entity

import com.LWT.DataProcess.SqlDataFunction
import groovy.sql.GroovyRowResult
import groovy.xml.MarkupBuilder

/**
 * Created by H on 2015/5/6.
 */
class SNX_Vessel {
    def SqlDataFunction sqlDataFun

    //Vessel vessel = new Vessel(vesselID,vesselName,loydsID,vesselClassID,lineOperator,country,callSign)
    def VesselID        //�������α��
    def VesselName      //��������������
    def LoydsID         //LoydsID
    def VesselClassID   //������
    def LineOperator    //LineOperator
    def Country         //����
    def CallSign        //����
    def Notes           //��ע


    def vessel_unit_system = 'SI'
    def vessel_temperature_units = 'C'
    def vessel_stowage_scheme = 'UNKNOWN'

    boolean hasCountry  //�й���
    boolean IsInN4      //n4���Ƿ����

    //���ݺ��κţ����Ҳ���ʼ��������Ϣ
    def SNX_Vessel(def vesselVisitID) {

        sqlDataFun = new SqlDataFunction()
        Result_Vessel resultVessel = new Result_Vessel(sqlDataFun.getVessel(vesselVisitID))

        //this.VesselClassID = resultVessel.VesselClassID
        //this.LineOperator = resultVessel.LineOperator
        this.VesselClassID = 'AB'
        this.LineOperator = -1

        this.VesselID = resultVessel.VesselID
        this.Notes = resultVessel.VesselID
        this.VesselName = resultVessel.VesselCN
        this.LoydsID = resultVessel.VesselID.toString().length() <= 8 ? resultVessel.VesselID : resultVessel.VesselID.toString()[-1..-8]
        this.CallSign = resultVessel.CallSign

        if (resultVessel.Country != null && (resultVessel.Country.startsWith('�й�') || resultVessel.Country.startsWith('CHINA'))) {
            // ����Ϊ�й�
            this.Country = 'CN'
            hasCountry = true
        } else {
            this.Country = ''
            // Ĭ���޹���
            hasCountry = false
        }

        this.IsInN4 = sqlDataFun.isVesselInN4(vesselVisitID)
    }

    def createSNX(){
        // ���ɵ�SNX
        def snxVesselStringWriter =  new StringWriter()
        def snx = new MarkupBuilder(snxVesselStringWriter)
        snx.'argo:snx'('xmlns:argo':'http://www.navis.com/argo', 'xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance', 'xsi:schemaLocation':'http://www.navis.com/argo snx.xsd') {
            'vessel'(
                    'id':this.VesselID,
                    'lloyds-id':this.LoydsID,
                    'name':this.VesselName,
                    'vessel-class':this.VesselClassID,
                    'owner':this.LineOperator,
                    'unit-system':this.vessel_unit_system,
                    'temperature-units':this.vessel_temperature_units,
                    'stowage-scheme':this.vessel_stowage_scheme,
                    'country-id':this.Country,
                    'radio-call-sign':this.CallSign)
        }
        //this.vessel_SNX = snxVesselStringWriter.toString()
        return snxVesselStringWriter.toString()
    }
}
