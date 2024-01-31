package com.welgram.atcm.enums;



public enum Gender {

    MALE        ("M", "남성", "남", "남자"),
    FEMALE      ("F", "여성", "여", "여자");

    private final String code;
    private final String desc1;
    private final String desc2;
    private final String desc3;



    Gender(String code, String desc1, String desc2,String desc3) {
        this.code = code;
        this.desc1 = desc1;
        this.desc2 = desc2;
        this.desc3 = desc3;
    }



    public String getCode() {
        return code;
    }



    public String getDesc1() {

        return desc1;
    }



    public String getDesc2() {

        return desc2;
    }



    public String getDesc3() {

        return desc3;
    }



    public static Gender fromCode(String code) {

        Gender result = null;

        for (Gender gender : Gender.values()) {

            if (gender.code.equalsIgnoreCase(code)) {
                result = gender;
                break;
            }
        }

        return result;
    }
}
