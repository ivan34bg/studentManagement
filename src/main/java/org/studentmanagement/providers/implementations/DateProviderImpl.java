package org.studentmanagement.providers.implementations;

import org.springframework.stereotype.Component;
import org.studentmanagement.providers.DateProvider;

import java.time.Instant;
import java.util.Date;

@Component
public class DateProviderImpl implements DateProvider {
    @Override
    public Date getDateFrom(Instant time) {
        return Date.from(time);
    }
}
