package com.jxx.groupware.api.excel.application;

import com.jxx.groupware.api.excel.ExcelFileReadException;
import com.jxx.groupware.core.vacation.domain.entity.CompanyVacationTypePolicy;
import com.jxx.groupware.core.vacation.domain.entity.VacationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
public class CompanyVacationTypePolicyExcelReader implements ExcelReader<CompanyVacationTypePolicy>{

    private static final List<String> COLUMN_NAMES = List.of("회사코드", "휴가유형", "휴가일수");
    private static final String PURPOSE = "휴가 정책 조회";
    private static final String SHEET_NAME = "특별 휴가 정책";

    private final Sheet sheet;
    private final int lastRowNum;

    public CompanyVacationTypePolicyExcelReader(InputStream inputStream) throws IOException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        try {
            this.sheet = workbook.getSheet(SHEET_NAME);
        } catch (NullPointerException e) {
            log.warn("특별 휴가 정책 시트가 존재하지 않습니다.");
            throw new ExcelFileReadException(PURPOSE, "특별 휴가 정책 시트가 존재하지 않습니다.", e);
        }
        this.lastRowNum = sheet.getLastRowNum();
        validateColumnNames();
    }

    private void validateColumnNames() {
        Row row = sheet.getRow(0);
        int columnNameSize = COLUMN_NAMES.size();
        for (int columnNameIndex = 0; columnNameIndex < columnNameSize; columnNameIndex++) {
            Cell cell = row.getCell(columnNameIndex);
            if (!COLUMN_NAMES.get(columnNameIndex).equals(cell.getStringCellValue())) {
                throw new ExcelFileReadException(PURPOSE, "올바른 형식이 아닙니다.");
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
                .orElseThrow(() -> new ExcelFileReadException(PURPOSE, vacationTypeDescription + " 휴가 유형은 존재하지 않습니다."));

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
