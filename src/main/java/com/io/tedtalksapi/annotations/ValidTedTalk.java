package com.io.tedtalksapi.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TedTalkValidator.class)
@Documented
public @interface ValidTedTalk {
    String message() default
            "Future releases cannot have url, likeCount, or viewCount";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

