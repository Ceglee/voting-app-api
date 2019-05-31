package com.pc.votingapp.api.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = VotingPeriodValidator.class)
@Documented
public @interface VotingPeriod {
    String votingStart();
    String votingEnd();

    String message() default "{voting.period.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
