package com.jxx.vacation.api.excel.presentation;

import com.jxx.vacation.api.excel.application.CompanyVacationTypePolicyExcelReader;
import com.jxx.vacation.api.excel.application.ExcelReader;
import com.jxx.vacation.core.vacation.domain.entity.CompanyVacationTypePolicy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
public class ExcelApiController {
    @GetMapping("/api/excel/read-file")
    public ResponseEntity<?> getTestFile(@RequestParam("file") MultipartFile file) throws IOException {
        ExcelReader<CompanyVacationTypePolicy> excelReader = new CompanyVacationTypePolicyExcelReader(file.getInputStream());
        List<CompanyVacationTypePolicy> response = excelReader.readAllRow();
        return ResponseEntity.ok(response);
    }
}
