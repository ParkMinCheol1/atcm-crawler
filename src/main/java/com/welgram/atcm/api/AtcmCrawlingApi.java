package com.welgram.atcm.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.welgram.atcm.GeneralCrawler;
import com.welgram.atcm.util.HttpClientUtil;
import com.welgram.atcm.vo.AtcmPlanVO;
import com.welgram.atcm.vo.AtcmRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class AtcmCrawlingApi {

    public final static Logger logger = LoggerFactory.getLogger(GeneralCrawler.class);

    public final static String DELIMITER = "/";
    public final static String URL_DOMAIN_PRODUCT = "https://api.nuzal.kr";
    public final static String URL_DOMAIN_DEV = "http://dev-nuzal-api.nuzal.co.kr";
    public final static String URL_DOMAIN_LOCAL = "http://localhost:8081";

    public final static String ATCM_API = "/atcm/api/";
    public final static String ATCM_API_CRAWLING = ATCM_API + "crawling";
    public final static String ATCM_API_TEMPLATE = ATCM_API + "template";
    public final static String ATCM_API_REQUEST = ATCM_API + "requests";

    private String apiUrl = "";



    //apiUrl : GET /atcm/api/crawling/information/{planId}
    public JsonObject getCrawlingInfo(Integer planId) {

        apiUrl = URL_DOMAIN_PRODUCT + ATCM_API_CRAWLING + DELIMITER + "information" + DELIMITER + planId;
        logger.info("apiUrl : {}", apiUrl);

        return HttpClientUtil.sendGET(apiUrl);
    }



    //apiUrl : POST /atcm/api/crawling/information
    public String saveCrawlingResultInfo(AtcmPlanVO atcmPlanVO) throws Exception {

        String params = new Gson().toJson(atcmPlanVO);
        apiUrl = URL_DOMAIN_PRODUCT + ATCM_API_CRAWLING + DELIMITER + "information";

        logger.info("apiUrl : {}", apiUrl);
        logger.info("params : {}", params);

        return HttpClientUtil.sendPOST(apiUrl, params);
    }



    //apiUrl : GET /atcm/api/template/{seq}
    public JsonObject getTemplate(Integer seq) {

        apiUrl = URL_DOMAIN_PRODUCT + ATCM_API_TEMPLATE + DELIMITER + seq;
        logger.info("apiUrl : {}", apiUrl);

        return HttpClientUtil.sendGET(apiUrl);
    }



    //apiUrl : PATCH /atcm/api/requests/{requestSeq}
    public void updateRequestWithReference(AtcmRequestVO atcmRequestVO) throws Exception {

        String params = new Gson().toJson(atcmRequestVO);
        apiUrl = URL_DOMAIN_PRODUCT + ATCM_API_REQUEST + DELIMITER + atcmRequestVO.getRequestSeq();

        logger.info("apiUrl : {}", apiUrl);
        logger.info("params : {}", params);

        HttpClientUtil.sendPATCH(apiUrl, params);
    }
}
