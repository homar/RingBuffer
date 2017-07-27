package pl.homar.codewise;

import pl.homar.codewise.domain.InternalMessage;
import pl.homar.codewise.domain.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentLinkedQueueBasedMessageCollector extends AbstractMessageCollector {

    private final Queue<InternalMessage> queue;
    private final int size;
    private final DateProvider dateProvider;

    ConcurrentLinkedQueueBasedMessageCollector(int size, DateProvider dateProvider) {
        this.queue = new ConcurrentLinkedQueue<>();
        this.dateProvider = dateProvider;
        this.size = size;
    }

    @Override
    public void writeEvent(Message message) {
        queue.add(new InternalMessage(message.getUserAgent(), message.getResponseCode(), dateProvider.getTimestamp()));
    }

    @Override
    public List<Message> get100LastMessages() {
        long currentTimestamp = dateProvider.getTimestamp();
        Iterator<InternalMessage> it = queue.iterator();
        int counter = 0;
        List<Message> result = new ArrayList<>();
        while(it.hasNext() && counter < size) {
            InternalMessage m = it.next();
            if(!isTooOld(m, currentTimestamp)) {
                result.add(m.toRawMessage());
            }
            counter++;
        }
        return result;
    }

    @Override
    public long getNumberOfMessagesWithFailure() {
        return 0;
    }
}
