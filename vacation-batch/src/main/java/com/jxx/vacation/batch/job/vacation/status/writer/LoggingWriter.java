package com.jxx.vacation.batch.job.vacation.status.writer;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@Slf4j
public class LoggingWriter<T> implements ItemWriter<T> {

    @Override
    public void write(Chunk<? extends T> chunk) throws Exception {
        Chunk<? extends T>.ChunkIterator iterator = chunk.iterator();

        iterator.forEachRemaining(item -> log.info("item {}", item));
    }
}
