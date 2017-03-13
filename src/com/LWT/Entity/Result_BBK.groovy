package com.LWT.Entity

import groovy.sql.GroovyRowResult

import java.text.SimpleDateFormat

/**
 * Created by H on 2015/5/6.
 */
class Result_BBK {
    //VARCHAR����
    def RecordID        //��¼���
    def TransID        //�������
    def WorkArea        //��ҵ��
    def CargoSpec       //����
    def Consignee       //�ջ���
    def Consignor       //������
    def VesselID      //ж����������ţ�
    def DetailCargoType //��ϸ����

    def PortType        //��ͷ���
    def LoadingType     //װ����ʽ
    def DirTakeType     //���᷽ʽ
    def DataSource      //������Դ

    //DATE����
    def UnloadTime      //ж��ʱ��
    def WorkingTime     //��ҵʱ��

    //NUMBER����
    def TonsWeight      //��������λ T=1000KG
    def TonsWeightSum   //�ۼƶ�����������ǰ��������λ T=1000KG
    def Stowage         //����
    def WaterGauge      //ˮ����

    //CHAR(1)����
    def WorkType        //��ҵ����
    def TransType       //���乤��
    def DirectTake      //�Ƿ�����
    def YardMove        //�Ƿ��Ƴ�
    def OperateType     //��������

    //��־λ
    def Processed       //�Ƿ��ȡ
    def ProcessTime     //��ȡʱ��


    /*
    20150812 ���������ֶ�����
     */
    def FeeItem         //������Ŀ


    Result_BBK(GroovyRowResult rowResult) {
        //VARCHAR����
        this.RecordID = rowResult['JLBH']
        this.TransID = rowResult['CBBH']
        this.WorkArea = rowResult['ZYQ']
        this.CargoSpec = rowResult['HZ']
        this.Consignee = rowResult['FHR']
        this.Consignor = rowResult['SHR']
        this.VesselID = rowResult['XCCM']
        this.DetailCargoType = rowResult['MXHZ']

        this.PortType = rowResult['MTLB']
        this.LoadingType = rowResult['ZCFS']
        this.DataSource = rowResult['SJLY']
        this.DirTakeType = rowResult['XTFS']

        //DATE����
        this.UnloadTime = rowResult['XCSJ'] == null ? '0001-01-01 00:00:00' : rowResult['XCSJ'].toString()[0..-3]
        this.WorkingTime = rowResult['ZYSJ'] == null ? '0001-01-01 00:00:00' : rowResult['ZYSJ'].toString()[0..-3]

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        Date startDate = sdf.parse(this.UnloadTime)
        Date endDate = sdf.parse(this.WorkingTime)

        if (startDate > endDate) this.UnloadTime = this.WorkingTime;

        //NUMBER����
        this.TonsWeight = rowResult['DS'] == null ? 0 : rowResult['DS']
        this.TonsWeightSum = rowResult['LJDS'] == null ? 0 : rowResult['LJDS']
        this.Stowage = rowResult['PZJS'] == null ? 0 : rowResult['PZJS']
        this.WaterGauge = rowResult['SCS'] == null ? 0 : rowResult['SCS']

        //CHAR(1)����
        this.WorkType = rowResult['ZYLX']
        this.TransType = rowResult['YSGJ']
        this.DirectTake = rowResult['SFXT']
        this.YardMove = rowResult['SFYC']
        this.OperateType = rowResult['CZLX']

        //��־λ
        this.Processed = rowResult['SFHQ']
        this.ProcessTime = rowResult['HQSJ']

        //20150812 �����ֶ�
        this.FeeItem = rowResult['FSXM']
    }
}
