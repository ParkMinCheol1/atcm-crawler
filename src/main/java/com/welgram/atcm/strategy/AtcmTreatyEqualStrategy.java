package com.welgram.atcm.strategy;

import com.welgram.atcm.vo.AtcmTreatyVO;

public interface AtcmTreatyEqualStrategy {

    boolean isEqual(AtcmTreatyVO t1, AtcmTreatyVO t2);

    void printInfo(AtcmTreatyVO t);

    void printDifferentInfo(AtcmTreatyVO asIs, AtcmTreatyVO toBe);
}
