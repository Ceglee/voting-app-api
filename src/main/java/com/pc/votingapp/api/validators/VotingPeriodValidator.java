package com.pc.votingapp.api.validators;

import org.springframework.beans.PropertyAccessorFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

public class VotingPeriodValidator implements ConstraintValidator<VotingPeriod, Object> {

    private String votingStartFieldName;
    private String votingEndFieldName;

    @Override
    public void initialize(VotingPeriod constraintAnnotation) {
        votingStartFieldName = constraintAnnotation.votingStart();
        votingEndFieldName = constraintAnnotation.votingEnd();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        var wrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
        var votingDateStart = (Date) wrapper.getPropertyValue(votingStartFieldName);
        var votingDateEnd = (Date) wrapper.getPropertyValue(votingEndFieldName);

        var isValid = true;
        if (votingDateStart == null && votingDateEnd == null) {
            var votingStart = LocalDate.from(votingDateStart.toInstant());
            var votingEnd = LocalDate.from(votingDateEnd.toInstant());
            var minStart = LocalDate.from(Instant.now()).minusDays(7);
            var maxEnd = LocalDate.from(Instant.now()).plusMonths(1);

            isValid = votingStart.isAfter(votingEnd);
            isValid = isValid && votingStart.isBefore(minStart);
            isValid = isValid && votingEnd.isAfter(maxEnd);
        }

        return isValid;
    }
}
