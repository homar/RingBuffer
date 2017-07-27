package pl.homar.codewise;

public class MessageCollectors {
    public static MessageCollector LockedBasedRingBufferMessageCollector(int size) {
      return new RingBufferMessageCollector(new LockBasedRingBuffer(size), new DateProvider());
    }

    public static MessageCollector LockFreeRingBufferMessageCollector(int size) {
        return new RingBufferMessageCollector(new LockFreeRingBuffer(size), new DateProvider());
    }
}
