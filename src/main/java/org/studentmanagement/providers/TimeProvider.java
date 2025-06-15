package org.studentmanagement.providers;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public interface TimeProvider {
    Instant getCurrentTime();
}
