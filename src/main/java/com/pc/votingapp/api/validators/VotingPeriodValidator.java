package com.pc.votingapp.api.validators;

import org.springframework.beans.PropertyAccessorFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
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
        if (votingDateStart != null && votingDateEnd != null) {
            var votingStart = LocalDate.ofInstant(votingDateStart.toInstant(), ZoneId.systemDefault());
            var votingEnd = LocalDate.ofInstant(votingDateEnd.toInstant(), ZoneId.systemDefault());
            var gap = Period.between(votingStart, votingEnd).getDays();

            isValid = votingStart.isBefore(votingEnd);
            isValid = isValid && gap >= 7 && gap < 31;
        }

        return isValid;
    }
}
