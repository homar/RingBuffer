package pl.homar.codewise;

import pl.homar.codewise.domain.InternalMessage;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static java.lang.System.arraycopy;

class LockFreeRingBuffer implements RingBuffer {

    private final int size;
    private final AtomicReferenceArray<InternalMessage> items;
    private AtomicLong position;
    private AtomicLong cursor;

    LockFreeRingBuffer(int size) {
        this.size = size;
        this.items = new AtomicReferenceArray<>(size);
        this.position = new AtomicLong(0);
        this.cursor = new AtomicLong(-1);
        fill();
    }

    public void put(InternalMessage e) {
        long pos = position.getAndIncrement();
        while (pos >= cursor.get() + size) ;
        items.set((int) (pos % size), e);
        while (!cursor.compareAndSet(pos - 1, pos));
    }

    public InternalMessage[] read() {
        InternalMessage[] result = new InternalMessage[size];
        for(int i = 0; i < size; i++) {
            result[i] = items.get(i);
        }
        return result;
    }

    public long getPosition() {
        return position.get();
    }

    private void fill() {
        for (int i = 0; i < size; i++) {
            items.set(i, InternalMessage.EMPTY);
        }
    }


}
