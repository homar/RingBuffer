package pl.homar.codewise;

import pl.homar.codewise.domain.InternalMessage;
import pl.homar.codewise.domain.Message;

import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

class RingBufferMessageCollector extends AbstractMessageCollector {

    private final RingBuffer ringBuffer;
    private final DateProvider dateProvider;

    RingBufferMessageCollector(RingBuffer ringBuffer, DateProvider dateProvider) {
        this.ringBuffer = ringBuffer;
        this.dateProvider = dateProvider;
    }

    @Override
    public void writeEvent(Message message) {
        ringBuffer.put(new InternalMessage(message.getUserAgent(), message.getResponseCode(), dateProvider.getTimestamp()));
    }

    @Override
    public List<Message> get100LastMessages() {
        long currentTimestamp = dateProvider.getTimestamp();
        InternalMessage[] internalMessages = ringBuffer.read();
        return stream(internalMessages)
                .filter(this::isValid)
                .filter(m -> !isTooOld(m, currentTimestamp))
                .map(m -> m.toRawMessage())
                .collect(toList());
    }

    @Override
    public long getNumberOfMessagesWithFailure() {
        long currentTimestamp = dateProvider.getTimestamp();
        InternalMessage[] internalMessages = ringBuffer.read();
        return stream(internalMessages)
                .filter(this::isValid)
                .filter(m -> !isTooOld(m, currentTimestamp))
                .filter(this::hasErrorResponseCode)
                .count();
    }
}
