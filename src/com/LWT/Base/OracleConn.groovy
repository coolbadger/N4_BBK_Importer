package com.LWT.Base

import groovy.sql.Sql
import org.apache.log4j.Logger

import java.text.SimpleDateFormat

class OracleConn {
    public def isConnected
    // 数据库地址，账号，密码，连接驱动
    def DB
    def USER
    def PASSWORD
    def DRIVER
    Sql conn

    // 构造函数
    OracleConn(String _DataBase, String _User, String _Password, String _Driver) {
        //　初始化连接数据库
        DB = _DataBase
        USER = _User
        PASSWORD = _Password
        DRIVER = _Driver
        try {
            GlobalLogger.myLogger.info("Connecting... \t")
            conn = Sql.newInstance(DB, USER, PASSWORD, DRIVER)
            GlobalLogger.myLogger.info("ok!")
            isConnected = true
        } catch (Exception e) {
            			e.printStackTrace()
            isConnected = false
            GlobalLogger.myLogger.info("error")
        }
    }

    // 数据库连接测试
    def connTest(String inSql) {
        def sql = inSql
        if (sql == null || sql.equals("")) {
            sql = """
				select a.TABLE_NAME,b.COMMENTS 
				from user_tables a,user_tab_comments b 
				WHERE a.TABLE_NAME=b.TABLE_NAME 
				order by TABLE_NAME
				"""
        }
        conn.rows(sql).each { println it }
    }

    // 日期差
    int storeDays(String startDateStr, String endDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        Date startDate = sdf.parse(startDateStr)
        Date endDate = sdf.parse(endDateStr)
        long diff = endDate.getTime() - startDate.getTime()
        long days = diff / (1000 * 60 * 60 * 24)
        return (int) days
    }

}
