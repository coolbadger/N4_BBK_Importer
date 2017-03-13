package com.LWT.Entity

import groovy.sql.GroovyRowResult

import java.text.SimpleDateFormat

/**
 * Created by H on 2015/5/6.
 */
class Result_BBK {
    //VARCHAR类型
    def RecordID        //记录编号
    def TransID        //船舶编号
    def WorkArea        //作业区
    def CargoSpec       //货种
    def Consignee       //收货人
    def Consignor       //发货人
    def VesselID      //卸船船名（编号）
    def DetailCargoType //明细货种

    def PortType        //码头类别
    def LoadingType     //装船方式
    def DirTakeType     //现提方式
    def DataSource      //数据来源

    //DATE类型
    def UnloadTime      //卸船时间
    def WorkingTime     //作业时间

    //NUMBER类型
    def TonsWeight      //吨数，单位 T=1000KG
    def TonsWeightSum   //累计吨数（不含当前量），单位 T=1000KG
    def Stowage         //配载
    def WaterGauge      //水尺数

    //CHAR(1)类型
    def WorkType        //作业类型
    def TransType       //运输工具
    def DirectTake      //是否现提
    def YardMove        //是否移场
    def OperateType     //操作类型

    //标志位
    def Processed       //是否获取
    def ProcessTime     //获取时间


    /*
    20150812 新增费收字段如下
     */
    def FeeItem         //费收项目


    Result_BBK(GroovyRowResult rowResult) {
        //VARCHAR类型
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

        //DATE类型
        this.UnloadTime = rowResult['XCSJ'] == null ? '0001-01-01 00:00:00' : rowResult['XCSJ'].toString()[0..-3]
        this.WorkingTime = rowResult['ZYSJ'] == null ? '0001-01-01 00:00:00' : rowResult['ZYSJ'].toString()[0..-3]

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        Date startDate = sdf.parse(this.UnloadTime)
        Date endDate = sdf.parse(this.WorkingTime)

        if (startDate > endDate) this.UnloadTime = this.WorkingTime;

        //NUMBER类型
        this.TonsWeight = rowResult['DS'] == null ? 0 : rowResult['DS']
        this.TonsWeightSum = rowResult['LJDS'] == null ? 0 : rowResult['LJDS']
        this.Stowage = rowResult['PZJS'] == null ? 0 : rowResult['PZJS']
        this.WaterGauge = rowResult['SCS'] == null ? 0 : rowResult['SCS']

        //CHAR(1)类型
        this.WorkType = rowResult['ZYLX']
        this.TransType = rowResult['YSGJ']
        this.DirectTake = rowResult['SFXT']
        this.YardMove = rowResult['SFYC']
        this.OperateType = rowResult['CZLX']

        //标志位
        this.Processed = rowResult['SFHQ']
        this.ProcessTime = rowResult['HQSJ']

        //20150812 新增字段
        this.FeeItem = rowResult['FSXM']
    }
}
