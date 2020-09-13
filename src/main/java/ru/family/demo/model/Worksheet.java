package ru.family.demo.model;

import java.util.List;

public record Worksheet(
    Integer age,
    GENDER gender,
    FAMILY family,
    SOCIAL_STATUS socialStatus,
    Boolean married,
    Integer children,
    List<Integer> childrenAges,
    Boolean previousConviction,
    Integer salary,
    String address
) {

    public enum GENDER {
        MALE, FEMALE
    }

    public enum SOCIAL_STATUS {
        PUPIL,
        UNDERGRADUATE,
        STUDENT,
        EMPLOYED,
        UNEMPLOYED,
        RETIRED
    }

    public enum FAMILY {
        FULL, ONE_PARENT, NONE
    }
}
