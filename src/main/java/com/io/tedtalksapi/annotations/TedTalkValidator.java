package com.io.tedtalksapi.annotations;

import com.io.tedtalksapi.entity.TedTalksRecordT;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TedTalkValidator implements ConstraintValidator<ValidTedTalk, TedTalksRecordT> {

    private static final int MINIMUM_TED_YEAR = 1984;

    @Override
    public boolean isValid(TedTalksRecordT dto, ConstraintValidatorContext ctx) {
        if (dto == null || dto.getReleasedDate() == null) {
            return true; // skip null checks
        }

        // Validate year is not before 1984 (TED's founding year)
        if (dto.getReleasedDate().getYear() < MINIMUM_TED_YEAR) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(
                    "TED was founded in 1984. Release date cannot be before " + MINIMUM_TED_YEAR)
                    .addPropertyNode("releasedDate")
                    .addConstraintViolation();
            return false;
        }

        // Only validate if release date is in the future
        if (!dto.getReleasedDate().isAfter(LocalDate.now())) {
            return true;
        }

        // Map of field names to violation messages
        Map<String, Object> fieldMap = Map.of(
                "likeCount", dto.getLikeCount(),
                "viewCount", dto.getViewCount());

        // Collect violations
        List<String> violations = fieldMap.entrySet().stream()
                .filter(entry -> {
                    Object val = entry.getValue();
                    if (val == null)
                        return false;
                    if (val instanceof BigInteger && ((BigInteger) val).compareTo(BigInteger.ZERO) == 0)
                        return false;
                    return true;
                })
                .map(Map.Entry::getKey)
                .toList();

        if (!violations.isEmpty()) {
            ctx.disableDefaultConstraintViolation();
            violations.forEach(field -> ctx.buildConstraintViolationWithTemplate(
                    field + " must be 0 for future release dates").addPropertyNode(field).addConstraintViolation());
            return false;
        }

        return true;
    }
}
