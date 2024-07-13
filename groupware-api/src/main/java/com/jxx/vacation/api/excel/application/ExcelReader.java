package com.jxx.vacation.api.excel.application;


import java.util.List;

public interface ExcelReader<T> {
    T readOneRow(int rowNum);
    List<T> readAllRow();
}
