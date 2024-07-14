package com.jxx.groupware.api.vacation.query;

import com.jxx.groupware.core.vacation.domain.entity.VacationStatus;
import com.jxx.groupware.core.vacation.projection.VacationProjection;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mybatis.spring.boot.test.autoconfigure.AutoConfigureMybatis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.jxx.groupware.core.vacation.domain.entity.VacationStatus.*;
import static org.assertj.core.api.Assertions.*;

/**
 * INSERT QUERY 참고 vacation_dynamic_mapper.sql
 */

@Slf4j
@SpringBootTest
@AutoConfigureMybatis // @MybatisTest -> @Transactional 선언 되있어서 rollback 됨
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VacationDynamicMapperTest {

    @Autowired
    DataSource dataSource;
    @Autowired
    VacationDynamicMapper vacationDynamicMapper;

    @BeforeAll
    void init() {
        try {
            Connection connection = dataSource.getConnection();
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("/sql/mapper/vacation_dynamic_mapper.sql"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("조건에 맞춰 조회된다. ")
    @Test
    void vacation_dynamic_search_case_one() {
        List<VacationStatus> vacationStatuses = new ArrayList<>();
        vacationStatuses.add(CREATE);
        vacationStatuses.add(ONGOING);
        VacationSearchCondition cond = new VacationSearchCondition(
                "JXX", null, "U00001",
                vacationStatuses, null, null, null);
        List<VacationProjection> result = vacationDynamicMapper.search(cond);
        assertThat(result).extracting("vacationStatus").containsOnly(CREATE, ONGOING);
        assertThat(result).extracting("requesterId").containsOnly("U00001");
    }

    @DisplayName("list 조건이 Empty 더라도 조건에 맞춰 조회된다.")
    @Test
    void vacation_dynamic_search_case_list_cond_empty() {
        List<VacationStatus> emptyCond = new ArrayList<>();
        VacationSearchCondition cond = new VacationSearchCondition(
                "JXX", null, "U00001",
                emptyCond, null, null, null);
        List<VacationProjection> result = vacationDynamicMapper.search(cond);
        assertThat(result).extracting("vacationStatus").contains(CREATE, ONGOING);
        assertThat(result.size()).isEqualTo(2);
    }

    @DisplayName("list 조건이 null이 더라도 조건에 맞춰 조회된다.")
    @Test
    void vacation_dynamic_search_case_list_cond_null() {
        VacationSearchCondition cond = new VacationSearchCondition(
                "JXX", null, "U00001",
                null, null, null, null);
        List<VacationProjection> result = vacationDynamicMapper.search(cond);
        assertThat(result).extracting("vacationStatus").contains(CREATE, ONGOING);
        assertThat(result.size()).isEqualTo(2);
    }
}