package org.studentmanagement.providers.implementations;

import org.springframework.stereotype.Component;
import org.studentmanagement.providers.TimeProvider;

import java.time.Instant;

@Component
public class TimeProviderImpl implements TimeProvider {
    @Override
    public Instant getCurrentTime() {
        return Instant.now();
    }
}
