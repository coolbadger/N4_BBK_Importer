package com.LWT.Base

import com.LWT.ImporterUI.MainFrameInfo

class Global {
    public static def LOOP_SECONDS = 10
    public volatile static MainFrameInfo mainFrameParameter = new MainFrameInfo();

    static boolean WRITE_BACK = false

    static OracleConn baseOracleConnection = baseConnection()
    static OracleConn bulkOracleConnection = bulkConnection()
    static OracleConn n4OracleConnection = n4Connection()

    static N4Operator n4Operator = getN4Operator()


    static String argoSQL = ""

    static boolean baseConnected = false
    static boolean bulkConnection = false
    static boolean n4Connection = false
    static boolean n4PortConnection = false


    private static OracleConn baseConnection() {
        GlobalLogger.myLogger.info("Init baseConnection:")
        //　集团基础数据库
        def DB = 'jdbc:oracle:thin:@192.168.0.74:1521:swave'
        def USER = 'swavemvclw'
        def PASSWORD = 'swavemvclw'
        def DRIVER = 'oracle.jdbc.driver.OracleDriver'
        OracleConn newConn = new OracleConn(DB, USER, PASSWORD, DRIVER)
        baseConnected = newConn.isConnected
        return newConn
    }

    private static OracleConn bulkConnection() {
        GlobalLogger.myLogger.info("Init bulkConnection:")
        //　散货系统数据库
        def DB = 'jdbc:oracle:thin:@192.168.37.103:1521:database'
        def USER = 'lwjk'
        def PASSWORD = 'wzlwjk'
        def DRIVER = 'oracle.jdbc.driver.OracleDriver'

        OracleConn newConn = new OracleConn(DB, USER, PASSWORD, DRIVER)
        bulkConnection = newConn.isConnected
        return newConn
    }

    private static OracleConn n4Connection() {
        GlobalLogger.myLogger.info("Init n4Connection:")
        //　N4系统数据库
        def IP = mainFrameParameter.p_IsTestEnv ? "192.168.37.111" : "192.168.37.110"
        def DB = "jdbc:oracle:thin:@" + IP + ":1521:n4"
        def USER = "n4user"
        def PASSWORD = "n4user"
        def DRIVER = 'oracle.jdbc.driver.OracleDriver'

        OracleConn newConn = new OracleConn(DB, USER, PASSWORD, DRIVER)
        n4Connection = newConn.isConnected
        return newConn
    }


    private static N4Operator getN4Operator() {
        GlobalLogger.myLogger.info("Init N4PortConnection:")
        //N4系统连接
        def IP = mainFrameParameter.p_IsTestEnv ? "192.168.37.111" : "192.168.37.112"
        String ArgoServiceURL = "http://" + IP + ":9080/apex/services/argoservice"
        String OperatorId = "WZCT"
        String ComplexId = "WZT"
        String FacilityId = "LWT"
        String YardId = "LWT"

        String UserName = "admin"
        String Password = "itadmin"

        N4Operator newOperator = new N4Operator()

        newOperator.ARGO_SERVICE_URL = ArgoServiceURL
        newOperator.Operator_Id = OperatorId
        newOperator.Complex_Id = ComplexId
        newOperator.Facility_Id = FacilityId
        newOperator.Yard_Id = FacilityId

        newOperator.USERNAME = UserName
        newOperator.PASSWORD = Password

        newOperator.initRequest()
        n4PortConnection = newOperator.isconnected
        return newOperator;
    }
}
