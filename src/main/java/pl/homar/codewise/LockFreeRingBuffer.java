package pl.homar.codewise;

import pl.homar.codewise.domain.InternalMessage;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.System.arraycopy;

class LockFreeRingBuffer implements RingBuffer {

    private final int size;
    private final InternalMessage[] items;
    private AtomicLong position;
    private AtomicInteger cursor;

    LockFreeRingBuffer(int size) {
        this.size = size;
        this.items = new InternalMessage[size];
        this.position = new AtomicLong(1);
        this.cursor = new AtomicInteger(0);
        fill();
    }

    public void put(InternalMessage e) {
        while (position.get() % size == cursor.get() - 1) ;
        int pos = (int) position.getAndIncrement() % size;
        items[pos] = e;
        int prevPos = (pos == 0) ? size - 1 : pos - 1;
        while (!cursor.compareAndSet(prevPos, pos)) ;
    }

    public InternalMessage[] read() {
        InternalMessage[] result = new InternalMessage[size];
        arraycopy(items, 0, result, 0, size);
        return result;
    }

    public long getPosition() {
        return position.get() - 1;
    }

    private void fill() {
        for (int i = 0; i < items.length; i++) {
            items[i] = InternalMessage.EMPTY;
        }
    }


}
