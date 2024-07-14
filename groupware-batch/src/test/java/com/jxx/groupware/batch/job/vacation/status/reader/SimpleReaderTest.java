package com.jxx.groupware.batch.job.vacation.status.reader;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;


@Slf4j
class SimpleReaderTest {

    @Test
    void test() {
        List<String> fruits = List.of("orange", "banana");

        Iterator<String> iterator = fruits.iterator();

        String n1 = iterator.hasNext() ? iterator.next() : null;
        String n2 = iterator.hasNext() ? iterator.next() : null;
        String n3 = iterator.hasNext() ? iterator.next() : null;

        log.info("n1 {} n2 {} n3 {}", n1, n2, n3);
    }
}