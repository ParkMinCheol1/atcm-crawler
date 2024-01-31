package com.welgram.atcm.strategy;

import com.welgram.common.strategy.TreatyNameComparator;
import com.welgram.common.strategy.TreatyNameComparators;
import com.welgram.crawler.FinLogger;
import com.welgram.atcm.enums.InsType;
import com.welgram.atcm.enums.PayType;
import com.welgram.atcm.vo.AtcmTreatyVO;
import java.util.function.Function;


/**
 *
 * 특약 객체의 "같다" 전략을 정의합니다.
 *
 */
public class AtcmTreatyEqualStrategy1 implements AtcmTreatyEqualStrategy {

    public FinLogger logger = FinLogger.getFinLogger(this.getClass());

    private TreatyNameComparator treatyNameComparator = TreatyNameComparators.allApplied;

    public AtcmTreatyEqualStrategy1() { }

    public AtcmTreatyEqualStrategy1(Function<String, String> nameHandler) {

        this.treatyNameComparator = treatyNameComparator;
    }

    @Override
    public boolean isEqual(AtcmTreatyVO t1, AtcmTreatyVO t2) {

        //특약명, 가입금액, 보험기간, 납입기간이 같으면 같은 특약으로 간주합니다.
        return treatyNameComparator.equals(t1.getTreatyName(), t2.getTreatyName())
            && t1.getAssureMoney().equals(t2.getAssureMoney())
            && t1.getInsPeriod().equals(t2.getInsPeriod())
            && t1.getInsType().equals(t2.getInsType())
            && t1.getPayPeriod().equals(t2.getPayPeriod())
            && t1.getPayType().equals(t2.getPayType());
    }



    @Override
    public void printInfo(AtcmTreatyVO t) {

        logger.info("=========================================================================");
        logger.info("특약명 : " + t.getTreatyName());
        logger.info("특약 가입금액 : " + t.getAssureMoney());
        logger.info("특약 보험기간 : " + t.getInsPeriod() + InsType.fromCode(t.getInsType()).getDesc());
        logger.info("특약 납입기간 : " + t.getPayPeriod() + PayType.fromCode(t.getPayType()).getDesc());
        logger.info("=========================================================================");
    }



    /**
     * 두 특약 정보를 비교하여 출력합니다.
     * @param treatyAsIs 가입설계 특약 정보(기존의 특약 정보)
     * @param treatyToBe 원수사 특약 정보(최신 특약 정보)
     */
    @Override
    public void printDifferentInfo(AtcmTreatyVO treatyAsIs, AtcmTreatyVO treatyToBe) {

        logger.info("=========================================================================");
        logger.info("특약명 : {}", treatyAsIs.getTreatyName());
        logger.info("특약 가입금액 : {} -> {}", treatyAsIs.getAssureMoney(), treatyToBe.getAssureMoney());
        logger.info("특약 보험기간 : {} -> {}",
            treatyAsIs.getInsPeriod() + InsType.fromCode(treatyAsIs.getInsType()).getDesc()
            , treatyToBe.getInsPeriod() + InsType.fromCode(treatyToBe.getInsType()).getDesc());
        logger.info("특약 납입기간 : {} -> {}",
            treatyAsIs.getPayPeriod() + PayType.fromCode(treatyAsIs.getPayType()).getDesc()
            , treatyToBe.getPayPeriod() + PayType.fromCode(treatyToBe.getPayType()).getDesc());
        logger.info("=========================================================================");
    }
}

