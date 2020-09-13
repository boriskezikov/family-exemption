package ru.family.demo.model;

import java.util.List;

@Deprecated
public record Worksheet(
    Integer age,
    Gender gender,
    Family family,
    SocialStatus socialStatus,
    Boolean married,
    Integer children,
    List<Integer> childrenAges,
    Boolean previousConviction,
    Integer salary,
    String address
) {

    public enum Gender {
        MALE, FEMALE
    }

    public enum SocialStatus {
        PUPIL,
        UNDERGRADUATE,
        STUDENT,
        EMPLOYED,
        UNEMPLOYED,
        RETIRED
    }

    public enum Family {
        FULL, ONE_PARENT, NONE
    }
}
