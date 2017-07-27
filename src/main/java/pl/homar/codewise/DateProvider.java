package pl.homar.codewise;

import java.time.Instant;

class DateProvider {
    long getTimestamp() {
        return Instant.now().toEpochMilli();
    }
}
