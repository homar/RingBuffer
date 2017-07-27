package pl.homar.codewise;

import pl.homar.codewise.domain.InternalMessage;

import java.util.concurrent.atomic.AtomicLong;

import static java.lang.System.arraycopy;

class LockFreeRingBuffer implements RingBuffer {

    private final int size;
    private final InternalMessage[] items;
    private AtomicLong position;
    private AtomicLong cursor;

    LockFreeRingBuffer(int size) {
        this.size = size;
        this.items = new InternalMessage[size];
        this.position = new AtomicLong(0);
        this.cursor = new AtomicLong(-1);
        fill();
    }

    public void put(InternalMessage e) {
        while (position.get() % size == (cursor.get() - 1) % size) ;
        long pos = position.getAndIncrement();
        items[(int) pos % size] = e;
        while (!cursor.compareAndSet(pos - 1, pos)) ;
    }

    public InternalMessage[] read() {
        InternalMessage[] result = new InternalMessage[size];
        arraycopy(items, 0, result, 0, size);
        return result;
    }

    public long getPosition() {
        return position.get();
    }

    private void fill() {
        for (int i = 0; i < items.length; i++) {
            items[i] = InternalMessage.EMPTY;
        }
    }


}
