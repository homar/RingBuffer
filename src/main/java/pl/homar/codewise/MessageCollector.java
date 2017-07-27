package pl.homar.codewise;


import pl.homar.codewise.domain.Message;

import java.util.List;

public interface MessageCollector {
    void writeEvent(Message message);
    List<Message> get100LastMessages();
    long getNumberOfMessagesWithFailure();
}
