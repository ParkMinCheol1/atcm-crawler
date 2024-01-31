package com.welgram.atcm.vo;

import com.welgram.atcm.cli.AtcmCrawlerCommand;
import com.welgram.atcm.enums.Gender;
import com.welgram.atcm.util.Birthday;
import com.welgram.atcm.util.InsuranceUtil;


//
public class AtcmCrawlingInfo {

    private AtcmCrawlerCommand atcmCrawlerCommand;

    private AtcmPlanVO atcmPlanVO;

    private String productName;
    private String productCode;
    private String category;
    private String crawlUrl;

    private Birthday birthday;
    private Gender gender;
    private final Birthday babyBirthday = InsuranceUtil.getBirthday(0);
    private final Gender babyGender = Gender.MALE;

    private final String job = "초등학교 교사";
    private final String pregnancyWeek = "12";
    private final String largeAmountContract = "고액적립";
    private final String healthType = "표준체";
    private final String medicalBeneficiary = "일반(비대상)";
    private final String lifeDesignCostAge = "65";                      // 생애설계 자금나이 // DGB 생애설계케이스에서 65고정나이로 진행했었음 // 보험료 고정게런티 없음
    private final String lifeDesignCostPeriod = "15년";                 //


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCrawlUrl() {
        return crawlUrl;
    }

    public void setCrawlUrl(String crawlUrl) {
        this.crawlUrl = crawlUrl;
    }

    public Birthday getBirthday() {
        return birthday;
    }

    public void setBirthday(Birthday birthday) {
        this.birthday = birthday;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Birthday getBabyBirthday() {
        return babyBirthday;
    }

    public Gender getBabyGender() {
        return babyGender;
    }

    public String getJob() {
        return job;
    }

    public String getPregnancyWeek() {
        return pregnancyWeek;
    }

    public String getLargeAmountContract() {
        return largeAmountContract;
    }

    public String getHealthType() {
        return healthType;
    }

    public String getMedicalBeneficiary() {
        return medicalBeneficiary;
    }

    public String getLifeDesignCostAge() {
        return lifeDesignCostAge;
    }

    public String getLifeDesignCostPeriod() {
        return lifeDesignCostPeriod;
    }

    public AtcmPlanVO getAtcmPlanVO() {
        return atcmPlanVO;
    }

    public void setAtcmPlanVO(AtcmPlanVO atcmPlanVO) {
        this.atcmPlanVO = atcmPlanVO;
    }

    public AtcmCrawlerCommand getAtcmCrawlerCommand() {
        return atcmCrawlerCommand;
    }

    public void setAtcmCrawlerCommand(AtcmCrawlerCommand atcmCrawlerCommand) {
        this.atcmCrawlerCommand = atcmCrawlerCommand;
    }
}