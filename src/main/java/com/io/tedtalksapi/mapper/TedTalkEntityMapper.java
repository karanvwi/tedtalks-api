package com.io.tedtalksapi.mapper;

import com.io.tedtalksapi.request.TedTalk;
import com.io.tedtalksapi.entity.TedTalksRecordT;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class TedTalkEntityMapper {

    public TedTalksRecordT toEntity(TedTalk dto) {
        if (dto == null) {
            return null;
        }

        TedTalksRecordT record = new TedTalksRecordT();
        record.setAuthor(dto.getAuthor());
        record.setTitle(dto.getTitle());
        record.setReleasedDate(dto.getReleasedDate());

        if (dto.getViewCount() != null) {
            record.setViewCount(BigInteger.valueOf(dto.getViewCount()));
        }
        if (dto.getLikeCount() != null) {
            record.setLikeCount(BigInteger.valueOf(dto.getLikeCount()));
        }

        record.setUrl(dto.getUrl());
        return record;
    }
}
