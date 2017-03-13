package com.LWT.Base

import com.navis.argo.webservice.types.v1_0.GenericInvokeResponseWsType
import com.navis.argo.webservice.types.v1_0.QueryResultType
import com.navis.argo.webservice.types.v1_0.ResponseType
import com.navis.argo.webservice.types.v1_0.ScopeCoordinateIdsWsType
import com.navis.www.services.argoservice.ArgoServiceLocator
import com.navis.www.services.argoservice.ArgoServicePort
import org.apache.log4j.Logger

import javax.xml.rpc.Stub

class N4Operator {
    public static Logger n4Logger = Logger.getLogger(N4Operator.class);

    public boolean isconnected

    static String OK = "0"
    static String INFO = "1"
    static String WARNINGS = "2"
    static String ERRORS = "3"

    String ARGO_SERVICE_URL
    String USERNAME = ""
    String PASSWORD = ""

    String Operator_Id
    String Complex_Id
    String Facility_Id
    String Yard_Id

    ScopeCoordinateIdsWsType scope
    ArgoServiceLocator service
    ArgoServicePort port

    String STATUS = ""
    String PAYLOAD = ""
    String[] RESULTS = null

    N4Operator() {
    }
    // ��ʼ��
    def initRequest() {
        try {
            n4Logger.info("Connecting...")
            // ?ָ���� Operator/Complex/Facility/Yard : WZCT/WZT/LWT_Old/LWT_Old
            this.scope = new ScopeCoordinateIdsWsType()
            this.scope.setOperatorId(this.Operator_Id)
            this.scope.setComplexId(this.Complex_Id)
            this.scope.setFacilityId(this.Facility_Id)
            this.scope.setYardId(this.Yard_Id)

            // ȷ��Web��������
            this.service = new ArgoServiceLocator()
            this.port = this.service.getArgoServicePort(new URL(this.ARGO_SERVICE_URL))
            Stub stub = (Stub) this.port

            // ָ���û���������
            stub._setProperty(Stub.USERNAME_PROPERTY, this.USERNAME)
            stub._setProperty(Stub.PASSWORD_PROPERTY, this.PASSWORD)

            isconnected = true

            n4Logger.info("ok!")
        } catch (Exception e) {
            //			e.printStackTrace()
            isconnected = false
            n4Logger.info("error!")
        }
    }

    // ���ʹ�XML�����󣬷���ִ�н��״̬
    def sendRequestWithXml(String _xmlString) {
        // ��������
        GenericInvokeResponseWsType response = this.port.genericInvoke(this.scope, _xmlString)
        this.PAYLOAD = response.getResponsePayLoad()
        // ����API��Ӧ
        ResponseType commonResponse = response.getCommonResponse()
        // ��ȡִ��״̬
        this.STATUS = commonResponse.getStatus()

        if (commonResponse.getQueryResults() == null) {
            this.RESULTS = new String[0]
        } else {
            this.RESULTS = new String[commonResponse.getQueryResults().length]
            commonResponse.getQueryResults().eachWithIndex { QueryResultType resultType, int i ->
                this.RESULTS[i] = resultType.getResult()
            }
        }
        return this.STATUS

    }
}

