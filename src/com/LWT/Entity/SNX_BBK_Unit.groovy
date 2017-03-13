package com.LWT.Entity

import com.LWT.Base.Global

import com.LWT.DataProcess.SqlDataFunction
import groovy.xml.MarkupBuilder

import java.text.SimpleDateFormat

/**
 * Created by H on 2015/5/6.
 */
class SNX_BBK_Unit {
    private SqlDataFunction dataFunction

    def RATE = 1        //转换系数，1为kg,1000为1ton

    def UnitID          //Unit编号
    def UnitSQLID       //散货数据库ID
    def Line            //Unit Line
    def UnitWeight      //重量，单位KG
    def WeightSumOld    //累积前总重量，单位KG
    def WeightSumNew    //累积后总重量，单位KG
    def UnitCommodity   //货种
    def UnitShiper      //发货人
    def UnitConsignee   //收货人

    def PortType        //码头类别
    def LoadingType     //装船方式
    def DirTakeType     //现提方式
    def DataSource      //数据来源

    def UnitTimeIn      //UnitTimeIn
    def UnitTimeLoad    //UnitTimeLoad
    def UnitTimeOut     //UnitTimeOut

    def Category        //unit 进出口类别(IMPORT, EXPORT)
    def TransState      //unit 状态(Advised, Inbound, EC/In, Yard, EC/Out, Loaded, Departed, Retired)
    def IBVisit         //进口
    def IBMode          //进口方式
    def OBVisit         //出口
    def OBMode          //出口方式

    boolean IsYard      // 是否是堆场上的UNIT(即从集团导入的数据)
    boolean IsDirTake   //是否直提

    List<SNX_VesselVisit> VesselVisitList       //对应的VesselVisit列表
    List<SNX_Vessel> snxVesselList              //附加船舶信息和船期信息
    List<SNX_VesselVisit> snxVesselVisitList


    SNX_BBK_Unit(Result_BBK result_bbk) {

        dataFunction = new SqlDataFunction()
        VesselVisitList = new ArrayList<SNX_VesselVisit>()

        //默认常值
        this.Line = "-1"
        this.UnitSQLID = result_bbk.RecordID

        //生成新UnitID 记录编号-收货人-作业类型-货种-运输工具
        def newID = result_bbk.RecordID + "-" + result_bbk.Consignor + "-" + result_bbk.WorkType + "-" + result_bbk.CargoSpec + "-" + result_bbk.TransType
        this.UnitID = newID

        //是否直提
        this.IsDirTake = result_bbk.DirectTake == 'Y'

        this.UnitWeight = result_bbk.TonsWeight * RATE
        this.WeightSumOld = result_bbk.TonsWeightSum * RATE
        this.WeightSumNew = result_bbk.TonsWeightSum * RATE + this.UnitWeight

        this.UnitCommodity = result_bbk.CargoSpec
        this.UnitShiper = result_bbk.Consignor
        this.UnitConsignee = result_bbk.Consignee

        //二次开发附加字段，N4中为自定义字段
        this.PortType = result_bbk.PortType
        this.LoadingType = result_bbk.LoadingType
        this.DataSource = result_bbk.DataSource
        this.DirTakeType = result_bbk.DirTakeType

        //根据装卸船类型，区分相关内容
        if (result_bbk.WorkType == "X") {
            //卸船
            if (result_bbk.TransType == "S") {
                //船舶类型
                Category = "IMPORT"
                TransState = "RETIRED"
                IBVisit = result_bbk.TransID + 'S'
                IBMode = "VESSEL"
                OBVisit = "GEN_TRUCK"
                OBMode = "TRUCK"
                IsYard = true
            }

            this.UnitTimeIn = result_bbk.WorkingTime
        } else if (result_bbk.WorkType == "Z") {
            //装船
            IsYard = false
            //插入、装
            switch (result_bbk.TransType) {
                case "S":
                    Category = 'EXPORT'
                    IBVisit = result_bbk.VesselID + 'S'
                    IBMode = "VESSEL"
                    OBVisit = result_bbk.TransID + 'S'
                    OBMode = "VESSEL"
                    IsYard = true
                    break;
                case "T":
                    Category = 'IMPORT'
                    IBVisit = result_bbk.TransID + 'S'
                    IBMode = "VESSEL"
                    OBVisit = "GEN_TRAIN"
                    OBMode = "TRAIN"
                    break;
                case "C":
                    Category = 'IMPORT'
                    IBVisit = result_bbk.TransID + 'S'
                    IBMode = "VESSEL"
                    OBVisit = "GEN_TRUCK"
                    OBMode = "TRUCK"
                    break;
                default:
                    Category = 'EXPORT'
                    Category = 'IMPORT'
                    IBVisit = result_bbk.TransID + 'S'
                    IBMode = "VESSEL"
                    OBVisit = "GEN_TRUCK"
                    OBMode = "TRUCK"
                    break;
            }
            TransState = 'DEPARTED'

            this.UnitTimeIn = result_bbk.UnloadTime
        }

        this.UnitTimeLoad = result_bbk.WorkingTime
        this.UnitTimeOut = result_bbk.WorkingTime


        //根据进出口类型，初始化船期列表
        def IbVesselVisitID
        def ObVesselVisitID

        if (result_bbk.TransType == "S" && result_bbk.OperateType == "X" && result_bbk.TransID != null) {
            //卸船
            IbVesselVisitID = result_bbk.TransID
            SNX_VesselVisit IbVesselVisit = new SNX_VesselVisit(IbVesselVisitID)
            this.VesselVisitList.add(IbVesselVisit)
        } else if (result_bbk.TransType == "S" && result_bbk.OperateType == "Z" && result_bbk.VesselID != null && result_bbk.TransID != null ) {
            //装船
            IbVesselVisitID = result_bbk.VesselID
            ObVesselVisitID = result_bbk.TransID
            SNX_VesselVisit IbVesselVisit = new SNX_VesselVisit(IbVesselVisitID)
            SNX_VesselVisit ObVesselVisit = new SNX_VesselVisit(ObVesselVisitID)
            this.VesselVisitList.add(IbVesselVisit)
            this.VesselVisitList.add(ObVesselVisit)
        } else if(result_bbk.VesselID != null) {
            //其他装
            IbVesselVisitID = result_bbk.VesselID
            SNX_VesselVisit IbVesselVisit = new SNX_VesselVisit(IbVesselVisitID)
            this.VesselVisitList.add(IbVesselVisit)
        }

    }

    //生成SNX
    def createSNX() {
        def snxUnitString = new StringWriter()
        def snxUnits = new MarkupBuilder(snxUnitString)
        snxUnits.'argo:snx'('xmlns:argo': 'http://www.navis.com/argo', 'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance', 'xsi:schemaLocation': 'http://www.navis.com/argo snx.xsd') {
            SNX_BBK_Unit u = this
            'unit'('id': u.UnitID, 'transit-state': u.TransState, 'category': u.Category, 'freight-kind': 'BBK', 'line': u.Line)
                    {

                        'contents'('weight-kg': u.UnitWeight, 'commodity-id': u.UnitCommodity, 'shipper-id': u.UnitShiper, 'consignee-id': u.UnitConsignee)
                        'equipment'(
                                'eqid': u.UnitID,
                                'class': 'CTR',
                                'role': "PRIMARY",
                                'type': 'UN'
                        )
                        'position'('loc-type': 'YARD', 'location': 'LWT', 'slot': 'BK')
                        'routing'((u.Category == 'IMPORT' ? 'pod-1' : 'pol'): 'WNZ')
                                {
                                    'carrier'('direction': 'IB', 'qualifier': 'DECLARED', 'mode': u.IBMode, 'id': u.IBVisit)
                                    'carrier'('direction': 'IB', 'qualifier': 'ACTUAL', 'mode': u.IBMode, 'id': u.IBVisit)
                                    'carrier'('direction': 'OB', 'qualifier': 'DECLARED', 'mode': u.OBMode, 'id': u.OBVisit)
                                    'carrier'('direction': 'OB', 'qualifier': 'ACTUAL', 'mode': u.OBMode, 'id': u.OBVisit)
                                }

                        // 事件
                        'non-move-history'() {
                            // 累积前量，累积后量
                            def eventType = ""

                            if (u.Category == 'EXPORT') {
                                eventType = "BBK_UNIT_LOAD"
                                'event'(
                                        'id': eventType,
                                        'quantity': u.UnitWeight,
                                        'quantity-unit': 'METRIC_TONNES',
                                        'note': "装船：" + u.UnitWeight,
                                        'user-id': "snx:admin",
                                        'is-billable': "Y"
                                )

                            } else {
                                if (u.IsYard == true) {
                                    eventType = "BBK_UNIT_DISCHARGE"
                                    'event'(
                                            'id': eventType,
                                            'shipper-id': u.UnitShiper,
                                            'consignee-id': u.UnitConsignee,
                                            'quantity': u.UnitWeight,
                                            'quantity-unit': 'METRIC_TONNES',
                                            'note': "卸船：" + u.UnitWeight,
                                            'user-id': "snx:admin",
                                            'is-billable': "Y"
                                    )

                                } else {
                                    if (u.IsDirTake == true) {
                                        eventType = "BBK_UNIT_DIRECT_DELIVERY"
                                        'event'(
                                                'id': eventType,
                                                'shipper-id': u.UnitShiper,
                                                'consignee-id': u.UnitConsignee,
                                                'quantity': u.UnitWeight,
                                                'quantity-unit': 'METRIC_TONNES',
                                                'note': "直提：" + u.UnitWeight,
                                                'user-id': "snx:admin",
                                                'is-billable': "Y"
                                        )

                                    } else {
                                        eventType = "BBK_UNIT_DELIVERY"
                                        'event'(
                                                'id': "STORAGE",
                                                'shipper-id': u.UnitShiper,
                                                'consignee-id': u.UnitConsignee,
                                                'quantity': u.UnitWeight,
                                                'quantity-unit': 'METRIC_TONNES',
                                                'note': "堆存：" + u.UnitWeight,
                                                'user-id': "snx:admin",
                                                'is-billable': "Y"
                                        )
                                        'event'(
                                                'id': eventType,
                                                'shipper-id': u.UnitShiper,
                                                'consignee-id': u.UnitConsignee,
                                                'quantity': u.UnitWeight,
                                                'quantity-unit': 'METRIC_TONNES',
                                                'note': "提货：" + u.UnitWeight,
                                                'user-id': "snx:admin",
                                                'is-billable': "Y"
                                        )

                                    }
                                }
                            }

                            'event'('id': eventType + "_SUM_OLD", 'quantity': WeightSumOld, 'quantity-unit': 'METRIC_TONNES', 'note': "作业前累积量(吨)：" + WeightSumOld, 'user-id': "snx:admin", 'is-billable': "Y")
                            'event'('id': eventType + "_SUM_NEW", 'quantity': WeightSumNew, 'quantity-unit': 'METRIC_TONNES', 'note': "作业后累积量(吨)：" + WeightSumNew, 'user-id': "snx:admin", 'is-billable': "Y")
                        }
                        //附加自定义字段，码头类型，装船类型，
                        //<unit-flex unit-flex-6="PORT_TYPE" unit-flex-7="LOADING_TYPE" unit-flex-8="DATA_SOURCE" unit-flex-9="DIRDEV_TYPE"/>
                        'unit-flex'('unit-flex-6': u.PortType, 'unit-flex-7': u.LoadingType, 'unit-flex-8': u.DataSource, 'unit-flex-9': u.DirTakeType)
                        'timestamps'('time-in': u.UnitTimeIn, 'time-load': u.UnitTimeLoad, 'time-out': u.UnitTimeOut)

                    }
        }
        return snxUnitString.toString()
    }

    //获取作业时间
    def getWorkingTime(Result_BBK result) {
        String operationTime = result.WorkingTime
        // 运输类型为船的时候，作业时间为船期时间
        if (result.TransType == "S") {
            operationTime = dataFunction.getArrBerthDate(result.TransID)
        }
        if (operationTime != null) {
            operationTime = operationTime[0..-3]
        } else {
            //日期错误，写日志
            log.writeLog("作业时间(ZYSJ)为空！记录编号(JLBH)：" + result.RecordID)
        }
        return operationTime
    }

    def getDischTime(Result_BBK result) {
        String dischargeTime
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        Date MinData = sdf.parse("2014-01-01 00:00:00")

        if (result.UnloadTime == null || result.UnloadTime <= MinData) {
            log.writeLog("卸船时间(XCSJ)为空！尝试以作业时间(ZYSJ)替换！记录编号(JLBH)：" + result.RecordID)
            if (result.WorkingTime == null) {
                log.writeLog("作业时间(ZYSJ)为空！记录编号(JLBH)：" + result.RecordID)
            } else {
                //作业时间
                dischargeTime = result.WorkingTime
            }
        } else {
            //卸船时间
            dischargeTime = result.UnloadTime
        }
        dischargeTime = dischargeTime[0..-3]
        return dischargeTime
    }

}
