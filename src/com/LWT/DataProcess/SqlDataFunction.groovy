package com.LWT.DataProcess

import com.LWT.Base.Global
import groovy.sql.GroovyRowResult
import groovy.sql.Sql

import java.text.SimpleDateFormat

class SqlDataFunction {
    private Sql bulkConn
    private Sql baseConn
    private Sql n4Conn
    private String sqlStr = ""
    private String retStr = ""

    SqlDataFunction() {
        bulkConn = Global.bulkOracleConnection.conn
        baseConn = Global.baseOracleConnection.conn
        n4Conn = Global.n4OracleConnection.conn
    }

    /**
     * 从散货数据库中取出所有未处理数据
     * @return
     */
    List<GroovyRowResult> getBulks() {
//		sqlStr = "SELECT * FROM SC_CARGO_INFO where ZYQ='4617' AND SFHQ = 'N' order by 'JLBH'"

//        AND B.ARRBERTHDATE >= '01-10月 -14' AND B.ARRBERTHDATE < '01-11月 -14'

        SimpleDateFormat sdfIn = new SimpleDateFormat("yyyyMMdd")
        SimpleDateFormat sdfSQL = new SimpleDateFormat("dd-MM月 -yy")
        String condition = ""
        if (Global.mainFrameParameter.p_IsDateEnable) {
            String p_sDate = Global.mainFrameParameter.p_DateStart
            String p_eDate = Global.mainFrameParameter.p_DateEnd

            if (p_sDate.length() > 4) {
                Date sDate = sdfIn.parse(p_sDate)
                String sDateStr = sdfSQL.format(sDate)
                condition += " AND A.ZYSJ >= " + sDateStr
                if (p_eDate.length() > 4) {
                    Date eDate = sdfIn.parse(Global.mainFrameParameter.p_DateEnd)
                    if (eDate > sDate) {
                        String eDateStr = sdfSQL.format(eDate)
                        condition += " AND A.ZYSJ <= " + eDateStr
                    }
                }

            } else if (p_eDate.length() > 4) {
                Date eDate = sdfIn.parse(Global.mainFrameParameter.p_DateEnd)
                String eDateStr = sdfSQL.format(eDate)
                condition += " AND A.ZYSJ <= " + eDateStr
            }


        }

        if (Global.mainFrameParameter.p_IsUnitCount) {
            String rowNumStr = Global.mainFrameParameter.p_UnitCount
            condition += " AND ROWNUM <= " + rowNumStr
        }
        sqlStr = """
SELECT * FROM SC_CARGO_INFO A
WHERE
(
ZYQ='4617' AND SFHQ = 'N' AND 
    ((A.YSGJ LIKE 'S' AND
    (select 1 from SWAVEMVCLW.ptddshipdict_lw@WZLW B
    where B.SHIPDICTID = A.CBBH AND STATUS = '已发布'
      AND ROWNUM <= 1) IS NOT NULL
    )
    OR
    (A.YSGJ NOT LIKE 'S' )
    )
)

				"""

        sqlStr += condition
        sqlStr += """

ORDER BY 'JLBH'

"""
        println(sqlStr)
        return bulkConn.rows(sqlStr)
    }

/**
 * 从集团数据库中取出船期信息
 * @param vesselVisitID 船期编号
 * @return
 */
    GroovyRowResult getVesselVisit(def vesselVisitID) {
        if (getVesselVisits(vesselVisitID).size() > 0) {
            return getVesselVisits(vesselVisitID).first()
        } else {
            return null
        }
    }

    List<GroovyRowResult> getVesselVisits(def vesselVisitID) {
        sqlStr = "Select * from ptddshipdict_lw where SHIPDICTID ='${vesselVisitID}' AND STATUS = '已发布'"
        return baseConn.rows(sqlStr)
    }

/**
 * 从集团数据库中取出船期对应的船舶信息
 * @param vesselVisitID 船期编号
 * @return
 */
    GroovyRowResult getVessel(def vesselVisitID) {
        GroovyRowResult result = getVessels(vesselVisitID).first()
        return result
    }

    List<GroovyRowResult> getVessels(def vesselVisitID) {
        sqlStr = """
				select vesselid,vesselcn,vesselen,callsign,country,shipcompany,vesseltype from ptbavessel_lw 
				where rownum <= 1 and vesselid = (select vesselid from ptddshipdict_lw where rownum <= 1 and SHIPDICTID ='${
            vesselVisitID
        }')
				"""
        return baseConn.rows(sqlStr)
    }

/**
 * 检查船期信息是否存在N4系统
 * @param vesselVisitID 船期编号
 * @return
 */
    boolean isVesselVisitInN4(def vesselVisitID) {
        if (vesselVisitID.indexOf("S") < 0) {
            vesselVisitID = vesselVisitID + 'S'
        }
        sqlStr = "select * from argo_carrier_visit where carrier_mode='VESSEL' and ID='${vesselVisitID}'"
        return n4Conn.rows(sqlStr).size() > 0 ? true : false
    }

/**
 * 检查船信息是否存在N4系统
 * @param vesselID 船舶编号
 * @return
 */
    boolean isVesselInN4(def vesselID) {
        sqlStr = "select * from vsl_vessels where ID='${vesselID}'"
        return n4Conn.rows(sqlStr).size() > 0 ? true : false
    }

/**
 * 根据船舶编号取得抵港时间
 * @param vesselID 船舶编号
 * @return
 */
    String getArrBerthDate(def vesselID) {
        sqlStr = "select ARRBERTHDATE from ptddshipdict_lw where SHIPDICTID = '${vesselID}'"
        retStr = baseConn.rows(sqlStr).first().'ARRBERTHDATE'
        return retStr
    }

/**
 * 根据收货单位（货主）编号，取得名称
 * @param shrID 货主编号
 * @return
 */
    String getConsigneeID(String shrID) {
        retStr = ""
        sqlStr = "select ID from REF_BIZUNIT_SCOPED where ROLE = 'SHIPPER' and notes = '${shrID}'"
        def result = n4Conn.rows(sqlStr)
        if (result.size() > 0) {
            retStr = n4Conn.rows(sqlStr).first()."ID"
        }
        return retStr
    }

/**
 * 根据UnitID查找Unit是否存在，存在则返回unit当前量，不存在则返回-1
 * @param unitID
 * @return
 */
    def getBaseUnitAmountInN4(String unitID) {
        sqlStr = """
				select u.GOODS_AND_CTR_WT_KG as WEIGHT from inv_unit u, INV_UNIT_FCY_VISIT ufv
				where 
				u.id = '${unitID}'
				and ufv.unit_gkey = u.gkey
				and ufv.TRANSIT_STATE = 'S40_YARD'
				"""
        List<GroovyRowResult> result = n4Conn.rows(sqlStr);
        def unitAmount = result.size() > 0 ? result.first()."WEIGHT" : -1
        return unitAmount
    }

}
