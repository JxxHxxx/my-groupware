package com.jxx.vacation.api.excel.application;

import com.jxx.vacation.core.vacation.domain.entity.CompanyVacationTypePolicy;
import com.jxx.vacation.core.vacation.domain.entity.VacationType;
import com.jxx.vacation.core.vacation.domain.exeception.VacationClientException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
public class CompanyVacationTypePolicyExcelReader implements ExcelReader<CompanyVacationTypePolicy>{

    private final Sheet sheet;
    private final int lastRowNum;
    private static final List<String> columnNames = List.of("회사코드", "휴가유형", "휴가일수");

    public CompanyVacationTypePolicyExcelReader(InputStream inputStream) throws IOException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        try {
            this.sheet = workbook.getSheet("특별 휴가 정책");
        } catch (NullPointerException e) {
            log.warn("특별 휴가 정책 시트가 존재하지 않습니다.");
            throw new VacationClientException("특별 휴가 정책 시트가 존재하지 않습니다.");
        }
        this.lastRowNum = sheet.getLastRowNum();
        validateColumnNames();
    }

    private void validateColumnNames() {
        Row row = sheet.getRow(0);
        int columnNameSize = columnNames.size();
        for (int columnNameIndex = 0; columnNameIndex < columnNameSize; columnNameIndex++) {
            Cell cell = row.getCell(columnNameIndex);
            if (!columnNames.get(columnNameIndex).equals(cell.getStringCellValue())) {
                throw new VacationClientException("올바른 형식이 아닙니다.");
            }
        }
    }

    @Override
    public CompanyVacationTypePolicy readOneRow(int rowNum) {
        Row row = sheet.getRow(rowNum);

        String companyId = row.getCell(0).getStringCellValue();
        String vacationTypeDescription = row.getCell(1).getStringCellValue();

        VacationType vacationType = Arrays.stream(VacationType.values())
                .filter(vt -> vt.getDescription().equals(vacationTypeDescription))
                .findFirst()
                .orElseThrow(() -> new VacationClientException(vacationTypeDescription + " 휴가 유형은 존재하지 않습니다."));

        float vacationDay = (float) row.getCell(2).getNumericCellValue();

        return CompanyVacationTypePolicy.builder()
                .companyId(companyId)
                .vacationType(vacationType)
                .vacationDay(vacationDay)
                .build();
    }

    @Override
    public List<CompanyVacationTypePolicy> readAllRow() {
        List<CompanyVacationTypePolicy> vacationTypePolicies = new ArrayList<>();
        for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
            vacationTypePolicies.add(readOneRow(rowIndex));
        }
        return vacationTypePolicies;
    }
}
