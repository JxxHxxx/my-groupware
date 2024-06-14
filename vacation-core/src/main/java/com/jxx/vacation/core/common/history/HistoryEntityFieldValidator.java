package com.jxx.vacation.core.common.history;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class HistoryEntityFieldValidator {

    private final Class masterEntity;
    private final Class historyEntity;

    public HistoryEntityFieldValidator(Class masterEntity, Class historyEntity) {
        this.masterEntity = masterEntity;
        this.historyEntity = historyEntity;
    }

    public void validate() throws NoSuchFieldException {
        List<String> masterFieldNames = Arrays
                .stream(masterEntity.getDeclaredFields())
                .map(field -> field.getName())
                .toList();

        valid(masterFieldNames);
    }

    public void validate(Map<String, String> convertFields) throws NoSuchFieldException {
        List<String> masterFieldNames = Arrays
                .stream(masterEntity.getDeclaredFields())
                .map(field -> convertFields.containsKey(field.getName()) ? convertFields.get(field.getName()) : field.getName())
                .toList();

        valid(masterFieldNames);

    }

    public void validate(List<String> exceptFields) throws NoSuchFieldException {
        List<String> masterFieldNames = Arrays
                .stream(masterEntity.getDeclaredFields())
                .filter(field -> !exceptFields.contains(field.getName()))
                .map(field -> field.getName())
                .toList();

        valid(masterFieldNames);
    }

    /**
     * @param convertFields : key : master entity field name value : history entity field name
     */
    public void validate(List<String> exceptFields, Map<String, String> convertFields) throws NoSuchFieldException {
        List<String> masterFieldNames = Arrays
                .stream(masterEntity.getDeclaredFields())
                .filter(field -> !exceptFields.contains(field.getName()))
                .map(field -> convertFields.containsKey(field.getName()) ? convertFields.get(field.getName()) : field.getName())
                .toList();

        valid(masterFieldNames);
    }

    private void valid(List<String> masterFieldNames) {
        List<String> historyEntityHasNotFields = new ArrayList<>();
        String targetField = "";
        for (String masterFieldName : masterFieldNames) {
            try {
                targetField = masterFieldName;
                historyEntity.getDeclaredField(masterFieldName);
            } catch (NoSuchFieldException e) {
                historyEntityHasNotFields.add(targetField);
            }
        }

        if (!historyEntityHasNotFields.isEmpty()) {
            log.error("\n현재 히스토리 엔티티에 존재하지 않는 필드 리스트 " +
                    "{}", historyEntityHasNotFields);
            throw new RuntimeException("마스터/히스토리 엔티티 동기화 검증 실패");
        } else {
            log.info("{}->{} 필드 일치 검증 성공" , masterEntity, historyEntity);
        }
    }
}
