package org.studentmanagement.providers;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public interface DateProvider {
    Date getDateFrom(Instant time);
}
