package pl.homar.codewise;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import pl.homar.codewise.domain.Message;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class RingBufferInternalMessageCollectorTest {

    private static final int size = 10;
    private static final long MINS_10 = 1000 * 60 * 10;
    private static final long MINUNTE = 1000 * 60;

    @Parameterized.Parameters
    public static Collection<Supplier<RingBuffer>> ringBuffers() {
        Supplier<RingBuffer> c1 = () -> new LockBasedRingBuffer(size);
        Supplier<RingBuffer> c2 = () -> new LockFreeRingBuffer(size);
        return Arrays.asList(c1, c2);
    }

    private DateProvider dateProvider;
    private RingBufferMessageCollector underTest;

    public RingBufferInternalMessageCollectorTest(Supplier<RingBuffer> ringBufferSupplier) {
        this.dateProvider = Mockito.mock(DateProvider.class);
        this.underTest = new RingBufferMessageCollector(ringBufferSupplier.get(), dateProvider);
    }

    @Test
    public void shouldReturnEmptyListWhenThereWasNoWrites() {
        when(dateProvider.getTimestamp()).thenReturn(MINUNTE);

        List<Message> result = underTest.get100LastMessages();

        assertEquals(0, result.size());
    }

    @Test
    public void shouldOnlyReturn1Event() {
        when(dateProvider.getTimestamp()).thenReturn(MINS_10);
        Message message = new Message("userAgent", 100);
        underTest.writeEvent(message);

        List<Message> result = underTest.get100LastMessages();

        assertEquals(1, result.size());
        assertEquals(message, result.get(0));
    }

    @Test
    public void shouldReturnEmptyList() {
        when(dateProvider.getTimestamp()).thenReturn(0L, MINS_10);
        Message message = new Message("userAgent", 100);
        underTest.writeEvent(message);

        List<Message> result = underTest.get100LastMessages();

        assertEquals(0, result.size());
    }

    @Test
    public void shouldReturnOnly10Messages() {
        when(dateProvider.getTimestamp()).thenReturn(MINS_10);
        Message message = new Message("userAgent", 100);
        for (int i = 0; i < 15; i++) {
            underTest.writeEvent(message);
        }

        List<Message> result = underTest.get100LastMessages();

        assertEquals(10, result.size());
        for (int i = 0; i < 10; i++) {
            assertEquals(message, result.get(i));
        }
    }

    @Test
    public void shouldCorrectlyCountNumberOfErrorCodes() {
        when(dateProvider.getTimestamp()).thenReturn(MINS_10);
        Message validMessages = new Message("userAgent", 100);
        Message invalidMessages = new Message("userAgent", 500);
        for (int i = 0; i < 15; i++) {
            underTest.writeEvent(validMessages);
        }
        underTest.writeEvent(invalidMessages);
        underTest.writeEvent(validMessages);

        long result = underTest.getNumberOfMessagesWithFailure();

        assertEquals(1, result);
    }

    @Test
    public void shouldNotSeeOldErrorCode() {
        when(dateProvider.getTimestamp()).thenReturn(0L, MINS_10);
        Message invalidMessages1 = new Message("userAgent", 500);
        Message invalidMessages2 = new Message("userAgent", 500);
        underTest.writeEvent(invalidMessages1);
        underTest.writeEvent(invalidMessages2);

        long result = underTest.getNumberOfMessagesWithFailure();

        assertEquals(1, result);
    }

}