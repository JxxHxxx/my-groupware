package com.jxx.vacation.core.common.history;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

        for (String masterFieldName : masterFieldNames) {
            historyEntity.getDeclaredField(masterFieldName);
        }
    }

    public void validate(Map<String, String> changeNeedFields) throws NoSuchFieldException {
        List<String> masterFieldNames = Arrays
                .stream(masterEntity.getDeclaredFields())
                .map(field -> changeNeedFields.containsKey(field.getName()) ? changeNeedFields.get(field.getName()) : field.getName())
                .toList();

        for (String masterFieldName : masterFieldNames) {
            historyEntity.getDeclaredField(masterFieldName);
        }

    }

    public void validate(List<String> exceptFields) throws NoSuchFieldException {
        List<String> masterFieldNames = Arrays
                .stream(masterEntity.getDeclaredFields())
                .filter(field -> !exceptFields.contains(field.getName()))
                .map(field -> field.getName())
                .toList();

        for (String masterFieldName : masterFieldNames) {
            historyEntity.getDeclaredField(masterFieldName);
        }
    }

    /**
     * @param changeNeedFields : key : master entity field name value : history entity field name
     */
    public void validate(List<String> exceptFields, Map<String, String> changeNeedFields) throws NoSuchFieldException {
        List<String> masterFieldNames = Arrays
                .stream(masterEntity.getDeclaredFields())
                .filter(field -> !exceptFields.contains(field.getName()))
                .map(field -> changeNeedFields.containsKey(field.getName()) ? changeNeedFields.get(field.getName()) : field.getName())
                .toList();

        for (String masterFieldName : masterFieldNames) {
            historyEntity.getDeclaredField(masterFieldName);
        }
    }
}
