package com.LWT.Entity

import groovy.sql.GroovyRowResult

/**
 * Created by H on 2015/5/6.
 */
class Result_Vessel {
    def VesselID        //船舶编号
    def VesselCN        //中文名
    def VesselEN        //英文名
    def CallSign        //呼号
    def Country         //国籍
    def ShipCompany     //船公司
    def VesselType      //船类型

    def VesselClassID   //船类型编号
    def LineOperator    //船公司

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
