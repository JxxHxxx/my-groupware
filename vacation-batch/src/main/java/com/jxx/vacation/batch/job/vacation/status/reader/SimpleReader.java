package com.jxx.vacation.batch.job.vacation.status.reader;

import lombok.NoArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@NoArgsConstructor
public class SimpleReader<T> implements ItemReader<T> {

    private  Collection<T> collection;

    public SimpleReader(Collection<T> collection) {
        this.collection = collection;
    }


    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        Iterator<T> iterator = collection.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }
}
