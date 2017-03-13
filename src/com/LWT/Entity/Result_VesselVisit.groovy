package com.LWT.Entity

import groovy.sql.GroovyRowResult

/**
 * Created by H on 2015/5/6.
 */
class Result_VesselVisit {
    def VesselVisitID       //航次号
    def VesselID            //船舶编号
    def LineOpID            //船公司
    def Country             //国籍
    def Terminal            //靠泊码头（编号,龙湾：4617）
    def ArriveDate          //抵达时间
    def LeaveDate           //离开时间

    def OutVoyage           //出口航次
    def InVoyage            //进口航次
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
