package pl.homar.codewise;

import pl.homar.codewise.domain.InternalMessage;

interface RingBuffer {
    void put(InternalMessage e);
    InternalMessage[] read();
    long getPosition();
}
