package pl.homar.codewise;

import pl.homar.codewise.domain.InternalMessage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.System.arraycopy;

class LockBasedRingBuffer implements RingBuffer {

	private final int size;
	private final InternalMessage[] items;
	private long position;
	private Lock lock;

	LockBasedRingBuffer(int size) {
		this.size = size;
		this.items = new InternalMessage[size];
		this.position = 0;
		this.lock = new ReentrantLock();
		fill();
	}

	public void put(InternalMessage e) {
		lock.lock();
		try {
			items[(int) position % size] = e;
			position++;
		} finally {
			lock.unlock();
		}
	}

	public InternalMessage[] read() {
		InternalMessage[] result = new InternalMessage[size];
		lock.lock();
		try {
			arraycopy(items, 0, result, 0, size);
		}finally {
			lock.unlock();
		}
		return result;
	}

	public long getPosition() {
		return position;
	}

	private void fill() {
		for (int i = 0; i < items.length; i++) {
			items[i] = InternalMessage.EMPTY;
		}
	}

}
