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

    def RATE = 1        //ת��ϵ����1Ϊkg,1000Ϊ1ton

    def UnitID          //Unit���
    def UnitSQLID       //ɢ�����ݿ�ID
    def Line            //Unit Line
    def UnitWeight      //��������λKG
    def WeightSumOld    //�ۻ�ǰ����������λKG
    def WeightSumNew    //�ۻ�������������λKG
    def UnitCommodity   //����
    def UnitShiper      //������
    def UnitConsignee   //�ջ���

    def PortType        //��ͷ���
    def LoadingType     //װ����ʽ
    def DirTakeType     //���᷽ʽ
    def DataSource      //������Դ

    def UnitTimeIn      //UnitTimeIn
    def UnitTimeLoad    //UnitTimeLoad
    def UnitTimeOut     //UnitTimeOut

    def Category        //unit ���������(IMPORT, EXPORT)
    def TransState      //unit ״̬(Advised, Inbound, EC/In, Yard, EC/Out, Loaded, Departed, Retired)
    def IBVisit         //����
    def IBMode          //���ڷ�ʽ
    def OBVisit         //����
    def OBMode          //���ڷ�ʽ

    boolean IsYard      // �Ƿ��Ƕѳ��ϵ�UNIT(���Ӽ��ŵ��������)
    boolean IsDirTake   //�Ƿ�ֱ��

    List<SNX_VesselVisit> VesselVisitList       //��Ӧ��VesselVisit�б�
    List<SNX_Vessel> snxVesselList              //���Ӵ�����Ϣ�ʹ�����Ϣ
    List<SNX_VesselVisit> snxVesselVisitList


    SNX_BBK_Unit(Result_BBK result_bbk) {

        dataFunction = new SqlDataFunction()
        VesselVisitList = new ArrayList<SNX_VesselVisit>()

        //Ĭ�ϳ�ֵ
        this.Line = "-1"
        this.UnitSQLID = result_bbk.RecordID

        //������UnitID ��¼���-�ջ���-��ҵ����-����-���乤��
        def newID = result_bbk.RecordID + "-" + result_bbk.Consignor + "-" + result_bbk.WorkType + "-" + result_bbk.CargoSpec + "-" + result_bbk.TransType
        this.UnitID = newID

        //�Ƿ�ֱ��
        this.IsDirTake = result_bbk.DirectTake == 'Y'

        this.UnitWeight = result_bbk.TonsWeight * RATE
        this.WeightSumOld = result_bbk.TonsWeightSum * RATE
        this.WeightSumNew = result_bbk.TonsWeightSum * RATE + this.UnitWeight

        this.UnitCommodity = result_bbk.CargoSpec
        this.UnitShiper = result_bbk.Consignor
        this.UnitConsignee = result_bbk.Consignee

        //���ο��������ֶΣ�N4��Ϊ�Զ����ֶ�
        this.PortType = result_bbk.PortType
        this.LoadingType = result_bbk.LoadingType
        this.DataSource = result_bbk.DataSource
        this.DirTakeType = result_bbk.DirTakeType

        //����װж�����ͣ������������
        if (result_bbk.WorkType == "X") {
            //ж��
            if (result_bbk.TransType == "S") {
                //��������
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
            //װ��
            IsYard = false
            //���롢װ
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


        //���ݽ��������ͣ���ʼ�������б�
        def IbVesselVisitID
        def ObVesselVisitID

        if (result_bbk.TransType == "S" && result_bbk.OperateType == "X" && result_bbk.TransID != null) {
            //ж��
            IbVesselVisitID = result_bbk.TransID
            SNX_VesselVisit IbVesselVisit = new SNX_VesselVisit(IbVesselVisitID)
            this.VesselVisitList.add(IbVesselVisit)
        } else if (result_bbk.TransType == "S" && result_bbk.OperateType == "Z" && result_bbk.VesselID != null && result_bbk.TransID != null ) {
            //װ��
            IbVesselVisitID = result_bbk.VesselID
            ObVesselVisitID = result_bbk.TransID
            SNX_VesselVisit IbVesselVisit = new SNX_VesselVisit(IbVesselVisitID)
            SNX_VesselVisit ObVesselVisit = new SNX_VesselVisit(ObVesselVisitID)
            this.VesselVisitList.add(IbVesselVisit)
            this.VesselVisitList.add(ObVesselVisit)
        } else if(result_bbk.VesselID != null) {
            //����װ
            IbVesselVisitID = result_bbk.VesselID
            SNX_VesselVisit IbVesselVisit = new SNX_VesselVisit(IbVesselVisitID)
            this.VesselVisitList.add(IbVesselVisit)
        }

    }

    //����SNX
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

                        // �¼�
                        'non-move-history'() {
                            // �ۻ�ǰ�����ۻ�����
                            def eventType = ""

                            if (u.Category == 'EXPORT') {
                                eventType = "BBK_UNIT_LOAD"
                                'event'(
                                        'id': eventType,
                                        'quantity': u.UnitWeight,
                                        'quantity-unit': 'METRIC_TONNES',
                                        'note': "װ����" + u.UnitWeight,
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
                                            'note': "ж����" + u.UnitWeight,
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
                                                'note': "ֱ�᣺" + u.UnitWeight,
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
                                                'note': "�Ѵ棺" + u.UnitWeight,
                                                'user-id': "snx:admin",
                                                'is-billable': "Y"
                                        )
                                        'event'(
                                                'id': eventType,
                                                'shipper-id': u.UnitShiper,
                                                'consignee-id': u.UnitConsignee,
                                                'quantity': u.UnitWeight,
                                                'quantity-unit': 'METRIC_TONNES',
                                                'note': "�����" + u.UnitWeight,
                                                'user-id': "snx:admin",
                                                'is-billable': "Y"
                                        )

                                    }
                                }
                            }

                            'event'('id': eventType + "_SUM_OLD", 'quantity': WeightSumOld, 'quantity-unit': 'METRIC_TONNES', 'note': "��ҵǰ�ۻ���(��)��" + WeightSumOld, 'user-id': "snx:admin", 'is-billable': "Y")
                            'event'('id': eventType + "_SUM_NEW", 'quantity': WeightSumNew, 'quantity-unit': 'METRIC_TONNES', 'note': "��ҵ���ۻ���(��)��" + WeightSumNew, 'user-id': "snx:admin", 'is-billable': "Y")
                        }
                        //�����Զ����ֶΣ���ͷ���ͣ�װ�����ͣ�
                        //<unit-flex unit-flex-6="PORT_TYPE" unit-flex-7="LOADING_TYPE" unit-flex-8="DATA_SOURCE" unit-flex-9="DIRDEV_TYPE"/>
                        'unit-flex'('unit-flex-6': u.PortType, 'unit-flex-7': u.LoadingType, 'unit-flex-8': u.DataSource, 'unit-flex-9': u.DirTakeType)
                        'timestamps'('time-in': u.UnitTimeIn, 'time-load': u.UnitTimeLoad, 'time-out': u.UnitTimeOut)

                    }
        }
        return snxUnitString.toString()
    }

    //��ȡ��ҵʱ��
    def getWorkingTime(Result_BBK result) {
        String operationTime = result.WorkingTime
        // ��������Ϊ����ʱ����ҵʱ��Ϊ����ʱ��
        if (result.TransType == "S") {
            operationTime = dataFunction.getArrBerthDate(result.TransID)
        }
        if (operationTime != null) {
            operationTime = operationTime[0..-3]
        } else {
            //���ڴ���д��־
            log.writeLog("��ҵʱ��(ZYSJ)Ϊ�գ���¼���(JLBH)��" + result.RecordID)
        }
        return operationTime
    }

    def getDischTime(Result_BBK result) {
        String dischargeTime
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        Date MinData = sdf.parse("2014-01-01 00:00:00")

        if (result.UnloadTime == null || result.UnloadTime <= MinData) {
            log.writeLog("ж��ʱ��(XCSJ)Ϊ�գ���������ҵʱ��(ZYSJ)�滻����¼���(JLBH)��" + result.RecordID)
            if (result.WorkingTime == null) {
                log.writeLog("��ҵʱ��(ZYSJ)Ϊ�գ���¼���(JLBH)��" + result.RecordID)
            } else {
                //��ҵʱ��
                dischargeTime = result.WorkingTime
            }
        } else {
            //ж��ʱ��
            dischargeTime = result.UnloadTime
        }
        dischargeTime = dischargeTime[0..-3]
        return dischargeTime
    }

}
