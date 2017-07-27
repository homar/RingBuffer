package pl.homar.codewise;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pl.homar.codewise.domain.InternalMessage;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import static java.util.Arrays.stream;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class RingBufferTest {
    private static final int size = 100;

    @Parameterized.Parameters
    public static Collection<Supplier<RingBuffer>> ringBuffers() {
        Supplier<RingBuffer> c1 = () -> new LockBasedRingBuffer(size);
        Supplier<RingBuffer> c2 = () -> new LockFreeRingBuffer(size);
        return Arrays.asList(c1, c2);
    }

    private RingBuffer ringBuffer;

    public RingBufferTest(Supplier<RingBuffer> ringBufferSupplier) {
        this.ringBuffer = ringBufferSupplier.get();
    }

    @Test
    public void shouldReturnCorrectData() throws InterruptedException {
        final int numberOfWriters = 20;
        final int numberOfWritesPerReader = 5;
        Thread[] writers = new Thread[numberOfWriters];
        for(int i = 0; i < numberOfWriters; i++){
            writers[i] = new Writer(ringBuffer, numberOfWritesPerReader, i);
        }
        for(int i = 0; i < numberOfWriters; i++){
            writers[i].start();
        }
        for(int i = 0; i < numberOfWriters; i++){
            writers[i].join();
        }
        CountingReader countingReader = new CountingReader(ringBuffer);
        countingReader.start();
        countingReader.join();
        InternalMessage[] internalMessages = countingReader.result;
        assertEquals(numberOfWriters, stream(internalMessages).filter(m -> m.getTimestamp() == numberOfWritesPerReader-1).count());
        assertEquals(numberOfWritesPerReader, stream(internalMessages).filter(m -> m.getUserAgent().equals("userAgent2")).count());
        assertEquals(size, internalMessages.length);
        assertEquals(numberOfWritesPerReader*numberOfWriters, ringBuffer.getPosition());
    }

    @Test
    public void shouldNotLostAnyUpdate() throws InterruptedException {
        int numberOfWriters = 400;
        int numberOfReaders = 400;
        int numberOfReadsPerReader = 150;
        int numberOfWritesPerWriter = 100;
        Thread[] writers = new Thread[numberOfWriters];
        Thread[] readers = new Thread[numberOfReaders];
        for(int i = 0; i < numberOfWriters; i++){
            writers[i] = new Writer(ringBuffer, numberOfWritesPerWriter, i);
        }
        for(int i = 0; i < numberOfReaders; i++){
            readers[i] = new Reader(ringBuffer, numberOfReadsPerReader);
        }
        for(int i = 0; i < numberOfWriters; i++){
            writers[i].start();
        }
        for(int i = 0; i < numberOfReaders; i++){
            readers[i].start();
        }
        for(int i = 0; i < numberOfWriters; i++){
            writers[i].join();
        }
        for(int i = 0; i < numberOfReaders; i++){
            readers[i].join();
        }
        assertEquals(0, Arrays.stream(ringBuffer.read()).filter(im -> im.getTimestamp() < 0).count());
        assertEquals(numberOfWritesPerWriter*numberOfWriters, ringBuffer.getPosition());
    }
}


class Writer extends Thread {
    private final RingBuffer ringBuffer;
    private final int number;
    private final int id;

    public Writer(RingBuffer ringBuffer, int number, int id) {
        this.ringBuffer = ringBuffer;
        this.number = number;
        this.id = id;
    }

    @Override
    public void run() {
        for (int i = 0; i < number; i++) {
            InternalMessage internalMessage = new InternalMessage("userAgent" + id, 400 - id, i);
            ringBuffer.put(internalMessage);
        }
    }
}

class Reader extends Thread {
    private final RingBuffer ringBuffer;
    private final int number;

    public Reader(RingBuffer ringBuffer, int number) {
        this.ringBuffer = ringBuffer;
        this.number = number;
    }

    @Override
    public void run() {
        for(int i = 0; i < number; i++) {
            InternalMessage[] read = ringBuffer.read();
            assert read.length == 100;
        }
    }
}

class CountingReader extends Thread {
    private final RingBuffer ringBuffer;
    InternalMessage[] result;

    public CountingReader(RingBuffer ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    @Override
    public void run() {
            result = ringBuffer.read();
    }
}