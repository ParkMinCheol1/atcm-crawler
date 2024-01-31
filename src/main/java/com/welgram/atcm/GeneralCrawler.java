package com.welgram.atcm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.welgram.atcm.api.AtcmCrawlingApi;
import com.welgram.atcm.cli.AtcmCrawlerCommand;
import com.welgram.atcm.enums.Gender;
import com.welgram.atcm.helper.SeleniumCrawlingHelper;
import com.welgram.atcm.strategy.AtcmTreatyEqualStrategy;
import com.welgram.atcm.util.InsuranceUtil;
import com.welgram.atcm.vo.AtcmCrawlingInfo;
import com.welgram.atcm.vo.AtcmPlanVO;
import com.welgram.atcm.vo.AtcmRequestVO;
import com.welgram.atcm.vo.AtcmTreatyVO;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;



public abstract class GeneralCrawler {

    public final static Logger logger = LoggerFactory.getLogger(GeneralCrawler.class);

    public final static Duration DRIVER_WAIT_TIME = Duration.ofSeconds(30);                                          //드라이버 대기시간 30초

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected SeleniumCrawlingHelper helper;

    private ChromeOptions chromeOptions;
    private AtcmCrawlingApi atcmCrawlingApi;

//    private HashMap<String, String> codeMasterMap = new HashMap<>();
//    private HashMap<Integer, String> productMasterMap = new HashMap<>();

//    private AtcmCrawlerCommand atcmCrawlerCommand;

//    public AtcmCrawlerCommand getAtcmCrawlerCommand() {
//        return atcmCrawlerCommand;
//    }
//
//    public void setAtcmCrawlerCommand(AtcmCrawlerCommand atcmCrawlerCommand) {
//        this.atcmCrawlerCommand = atcmCrawlerCommand;
//    }

    public GeneralCrawler() {
        this.atcmCrawlingApi = new AtcmCrawlingApi();
    }



    //드라이버 시작
    private void startDriver(String url) throws Exception {

        //드라이버 정보 초기화
        initDriverInfo();

        long startTime = 0;
        long endTime = 0;

        logger.info("드라이버를 시작합니다.");
        logger.info("크롤링 url : {}", url);
        startTime = System.currentTimeMillis();
        //웹 페이지가 로드될 때까지 최대 DRIVER_WAIT_TIME 초 대기
        driver.manage().timeouts().pageLoadTimeout(DRIVER_WAIT_TIME);
        driver.get(url);
        driver.manage().window().maximize();
        endTime = System.currentTimeMillis();

        logger.info("페이지 로딩에 걸린 시간 : {} msec", endTime - startTime);
    }



    //드라이버 종료
    private void stopDriver() throws Exception {

        logger.info("드라이버를 종료합니다.");

        if(driver != null) {
            driver.close();
            driver.quit();
        }
    }



    //드라이버 정보 초기화
    private void initDriverInfo() throws Exception {

        // TODO | 크롬드라이버 경로 변경
        System.setProperty("webdriver.chrome.driver","C:\\crawler\\driver\\chromedriver.exe");
        this.chromeOptions = new ChromeOptions();
        this.driver = new ChromeDriver(chromeOptions);
        this.wait = new WebDriverWait(driver, DRIVER_WAIT_TIME);
        this.helper = new SeleniumCrawlingHelper(driver, wait);
    }



    protected static void executeCommand(GeneralCrawler generalCrawler, String[] args) {

        int exitCode = new CommandLine(new AtcmCrawlerCommand(generalCrawler)).execute(args);
        System.exit(exitCode);
    }



    private String saveCrawlingResultInfo(AtcmCrawlingInfo atcmCrawlingInfo) throws Exception {

        //상품 & 가설 정보 세팅
        AtcmPlanVO atcmPlanVO = atcmCrawlingInfo.getAtcmPlanVO();
        atcmPlanVO.setProductName(atcmCrawlingInfo.getProductName());
        atcmPlanVO.setProductCode(atcmCrawlingInfo.getProductCode());
        atcmPlanVO.setSiteUrl(atcmCrawlingInfo.getCrawlUrl());
        atcmPlanVO.setAge(atcmCrawlingInfo.getBirthday().getAge());
        atcmPlanVO.setGender(atcmCrawlingInfo.getGender().getCode());
        atcmPlanVO.setCategoryCode(atcmCrawlingInfo.getCategory());

        return atcmCrawlingApi.saveCrawlingResultInfo(atcmPlanVO);
    }



    public class CustomTypeAdapter extends TypeAdapter<Object> {

        @Override
        public void write(JsonWriter out, Object value) throws IOException {

            if (value instanceof Duration) {
                out.value(((Duration) value).getSeconds());

            } else if (value instanceof File) {
                out.value(((File) value).getAbsolutePath());

            } else {
                throw new IOException("Unsupported type");
            }
        }

        @Override
        public Object read(JsonReader in) throws IOException {

            String type = in.nextString();
            if (type.equals("Duration")) {
                return Duration.ofSeconds(in.nextLong());

            } else if (type.equals("File")) {
                return new File(in.nextString());

            } else {
                throw new IOException("Unsupported type");
            }
        }
    }



    private AtcmCrawlingInfo getAtcmCrawlingInfo(AtcmCrawlingInfo atcmCrawlingInfo) {

        //명령어 옵션 읽기
        AtcmCrawlerCommand command = atcmCrawlingInfo.getAtcmCrawlerCommand();
        int requestSeq = command.getRequestSeq();
        int planId = command.getPlanId();
        int templateSeq = command.getTemplateSeq();
        int age = command.getAge();
        String gender = command.getGender();

        /**
         * 크롤링 시 필요한 데이터 조회.
         * 기존의 상품, 가입설계 구조를 기반으로 크롤링 데이터 정보를 세팅한다.
         * product + plan_master + plan_mapper + product_master
         */
        JsonObject jsonResponse = atcmCrawlingApi.getCrawlingInfo(planId);
        JsonObject jsonCrawlingInfo = jsonResponse.getAsJsonObject("data");

        Gson gson
            = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new CustomTypeAdapter())
                .registerTypeAdapter(File.class, new CustomTypeAdapter())
                .create();

        atcmCrawlingInfo = gson.fromJson(jsonCrawlingInfo, AtcmCrawlingInfo.class);
//        atcmCrawlingInfo = new Gson().fromJson(jsonCrawlingInfo, AtcmCrawlingInfo.class);

        atcmCrawlingInfo.setBirthday(InsuranceUtil.getBirthday(age));
        atcmCrawlingInfo.setGender(Gender.fromCode(gender));

        atcmCrawlingInfo.getAtcmPlanVO().setRequestSeq(requestSeq);
        atcmCrawlingInfo.getAtcmPlanVO().setPlanId(planId);
        atcmCrawlingInfo.setAtcmCrawlerCommand(command);

//        //템플릿 조회
//        AtcmTemplate template = new AtcmTemplate();
//        jsonResponse = atcmCrawlingApi.getTemplate(templateSeq);
//        JsonObject jsonTemplate = jsonResponse.getAsJsonObject("data");
//        template = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().fromJson(jsonTemplate, AtcmTemplate.class);
//
//
//        //템플릿 정보를 바탕으로 특약 조건 다시 세팅하기(주계약, 선택특약에 모두 적용)
//        List<AtcmTreaty> treatyList = atcmCrawlingInfo.getAtcmPlan().getAtcmTreatyList();
//        for(AtcmTreaty treaty : treatyList) {
//            treaty.setInsPeriod(template.getInsPeriod());
//            treaty.setInsType(template.getInsType());
//            treaty.setPayPeriod(template.getPayPeriod());
//            treaty.setPayType(template.getPayType());
//            treaty.setAssureMoney(BigInteger.valueOf(template.getAssureMoney()));
//        }

        return atcmCrawlingInfo;
    }



    public boolean execute(AtcmCrawlingInfo atcmCrawlingInfo) {

        int requestSeq = atcmCrawlingInfo.getAtcmCrawlerCommand().getRequestSeq();
        boolean result = true;
        long startTime;
        long endTime;

        try {

            startTime = System.currentTimeMillis();

            //크롤링 시 필요한 데이터 세팅하기
            atcmCrawlingInfo = getAtcmCrawlingInfo(atcmCrawlingInfo);

            //드라이버 시작
            startDriver(atcmCrawlingInfo.getCrawlUrl());

            //범용 스크립트 실행
            runScript(atcmCrawlingInfo);

            //크롤링 결과 전송
            String response = saveCrawlingResultInfo(atcmCrawlingInfo);

            endTime = System.currentTimeMillis();

            logger.info("크롤링 걸린 시간 : {} sec", TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));

//            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            result = false;

            //요청테이블에 에러메세지 업데이트
            sendErrorMsg(requestSeq, e.getMessage());

        } finally {
            //드라이버 종료
            try {
                stopDriver();

                if(result) {
                    System.exit(0);
                } else {
//                    sendErrorMsg(requestSeq, "원인 불명");
                    System.exit(1);
                }

            } catch (Exception e) {
                e.printStackTrace();

                //요청테이블에 에러메세지 업데이트
                sendErrorMsg(requestSeq, e.getMessage());

                System.exit(1);
            }
        }

        return result;
    }



    private void sendErrorMsg(int requestSeq, String errorMsg) {

        //TODO target 테이블에 에러메세지 update API 호출
        AtcmRequestVO atcmRequestVO = new AtcmRequestVO();
        atcmRequestVO.setRequestSeq(requestSeq);
        atcmRequestVO.setErrorMsg(errorMsg);

        try {
            atcmCrawlingApi.updateRequestWithReference(atcmRequestVO);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 로그 출력 및 비교
     *
     * ex)
     * params
     * (title = 생년월일, expected = 961109, actual = 991212)
     *
     * [출력결과]
     * ==========================================
     * expected 생년월일 : 961109
     * actual 생년월일 : 991212
     * ==========================================
     *
     * @param title 항목명
     * @param expected 세팅할 값
     * @param actual 실제 세팅된 값
     * @throws Exception
     */
    protected void printLogAndCompare(String title, String expected, String actual) throws Exception {

        logger.info("==========================================");
        logger.info("expected {} : {}", title, expected);
        logger.info("actual   {} : {}", title, actual);
        logger.info("==========================================");

        if(expected.equals(actual)) {
            logger.info("result : {} 일치", title);
            logger.info("==========================================");

        } else {
            throw new Exception(title + " 값 불일치");
        }
    }



    /**
     * 원수사 특약 목록과 가입설계 특약 목록 정보를 비교
     *
     * ex)
     * [homepageTreatyList]                                         [welgramTreatyList]
     * 특약명      가입금액        보험기간        납입기간            특약명      가입금액        보험기간        납입기간
     * 특약1      10000000        10년만기        10년납              특약1      10000000        10년만기        10년납
     * 특약2      10000000        10년만기        10년납              특약22     10000000        10년만기        10년납
     * 특약3      10000000        10년만기        10년납              특약33     10000000        10년만기        10년납
     * 특약4      10000000        10년만기        10년납              특약4      10000000        10년만기        10년납
     *
     * toAddTreatyList::Treaty      => 가입설계에 추가해야하는 특약 목록         [특약2, 특약3]
     * toRemoveTreatyList::Treaty   => 가입설계에서 제거해야하는 특약 목록       [특약22, 특약33]
     *
     * @param homepageTreatyList 원수사 특약 목록
     * @param welgramTreatyList 가입설계 특약 목록
     * @param strategy 특약을 비교하는 기준 전략(특약 목록을 비교할 때 특약의 같다는 기준을 어떻게 설정할지에 대한 전략)
     * @return true : 일치, false : 불일치
     */
    public boolean advancedCompareTreaties(
            List<AtcmTreatyVO> homepageTreatyList,
            List<AtcmTreatyVO> welgramTreatyList,
            AtcmTreatyEqualStrategy strategy
    ) {

        boolean result = false;
        List<AtcmTreatyVO> toAddTreatyList = new ArrayList<>();
        List<AtcmTreatyVO> toRemoveTreatyList = new ArrayList<>();

        // todo | 가입설계에 추가해야할 특약처리
        for (AtcmTreatyVO homepageTreaty : homepageTreatyList) {
            boolean isEqual = false;
            List<Boolean> isEquals = new ArrayList<>();

            for (AtcmTreatyVO welgramTreaty : welgramTreatyList) {
                isEqual = strategy.isEqual(homepageTreaty, welgramTreaty);
                isEquals.add(isEqual);
            }

            //가입설계에 추가해야할 특약리스트에 담는다.
            if (!isEquals.contains(true)) {
                toAddTreatyList.add(homepageTreaty);
            }
        }

        // todo | 가입설계에 제거해야할 특약처리
        for (AtcmTreatyVO welgramTreaty : welgramTreatyList) {
            boolean isEqual = false;
            List<Boolean> isEquals = new ArrayList<>();

            for (AtcmTreatyVO homepageTreaty : homepageTreatyList) {
                isEqual = strategy.isEqual(welgramTreaty, homepageTreaty);
                isEquals.add(isEqual);
            }

            //가입설계에서 제거해야할 특약리스트에 담는다.
            if (!isEquals.contains(true)) {
                toRemoveTreatyList.add(welgramTreaty);
            }
        }

        // todo | 수정할 특약 처리 - toAddTreatyList와 toRemoveTreatyList에서 특약명이 같은 케이스가 수정할 특약
        List<String> toModifyTreatyNameList = new ArrayList<>();
        for (AtcmTreatyVO homepageTreaty : toAddTreatyList) {
            String homepageTreatyName = homepageTreaty.getTreatyName();

            for (AtcmTreatyVO welgramTreaty : toRemoveTreatyList) {
                String welgramTreatyName = welgramTreaty.getTreatyName();

                if (homepageTreatyName.equals(welgramTreatyName)) {
                    logger.info("■■■■■■■■■■■■ 아래의 정보대로 가입설계 특약 내용을 변경해주세요 ■■■■■■■■■■■■");
                    strategy.printDifferentInfo(welgramTreaty, homepageTreaty);

                    toModifyTreatyNameList.add(homepageTreatyName);
                    break;
                }
            }
        }

        //가입설계에 추가/삭제해야하는 특약목록 중 수정해야하는 케이스는 제거해줘야한다.
        toAddTreatyList.removeIf(t -> toModifyTreatyNameList.contains(t.getTreatyName()));
        toRemoveTreatyList.removeIf(t -> toModifyTreatyNameList.contains(t.getTreatyName()));

        if (toAddTreatyList.size() > 0) {
            logger.info("■■■■■■■■■■■■ 가입설계에 다음의 특약들을 추가해주세요 ■■■■■■■■■■■■");
            toAddTreatyList.forEach(strategy::printInfo);
        }

        if (toRemoveTreatyList.size() > 0) {
            logger.info("■■■■■■■■■■■■ 가입설계에서 다음의 특약들을 제거해주세요 ■■■■■■■■■■■■");
            toRemoveTreatyList.forEach(strategy::printInfo);
        }

        //추가돼야하는 특약 목록 or 제거돼야하는 특약 목록 or 수정돼야하는 특약 목록이 하나라도 있는 경우에는 특약 불일치로 간주함.
        if (toAddTreatyList.size() > 0 || toRemoveTreatyList.size() > 0 || toModifyTreatyNameList.size() > 0) {
            result = false;
        } else {
            result = true;
        }

        return result;
    }



    //크롤링 스크립트 실행
    protected abstract AtcmCrawlingInfo runScript(AtcmCrawlingInfo atcmCrawlingInfo) throws Exception;
}
