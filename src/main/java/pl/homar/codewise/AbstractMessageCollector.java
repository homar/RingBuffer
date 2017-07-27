package pl.homar.codewise;

import pl.homar.codewise.domain.InternalMessage;

public abstract class AbstractMessageCollector implements MessageCollector {
    private static final long MINS_5 = 1000*60^5;

    protected boolean isTooOld(InternalMessage m, long timestamp) {
        return timestamp - m.getTimestamp() > MINS_5;
    }

    protected boolean hasInvalidResponseCode(InternalMessage m) {
        return m.getResponseCode() >= 400;
    }

}
