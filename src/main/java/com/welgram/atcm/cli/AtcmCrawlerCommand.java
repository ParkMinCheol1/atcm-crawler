package com.welgram.atcm.cli;

import com.welgram.atcm.GeneralCrawler;
import com.welgram.atcm.vo.AtcmCrawlingInfo;
import java.util.concurrent.Callable;
import picocli.CommandLine.Option;

public class AtcmCrawlerCommand implements Callable<Boolean> {
    private GeneralCrawler generalCrawler;

    @Option(names = {"-r"})
    private Integer requestSeq;

    @Option(names = {"-p"})
    private Integer planId;

    @Option(names = {"-t"})
    private Integer templateSeq;

    @Option(names = {"-a"})
    private Integer age;

    @Option(names = {"-g"})
    private String gender;

    public AtcmCrawlerCommand(GeneralCrawler generalCrawler) {
        this.generalCrawler = generalCrawler;
    }



    @Override
    public Boolean call() throws Exception {

        AtcmCrawlingInfo info = new AtcmCrawlingInfo();
        info.setAtcmCrawlerCommand(this);

        return generalCrawler.execute(info);
    }



    public Integer getRequestSeq() {
        return requestSeq;
    }



    public Integer getPlanId() {
        return planId;
    }



    public Integer getTemplateSeq() {
        return templateSeq;
    }



    public Integer getAge() {
        return age;
    }



    public String getGender() {
        return gender;
    }
}
