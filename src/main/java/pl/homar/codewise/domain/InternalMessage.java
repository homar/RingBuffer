package pl.homar.codewise.domain;

public class InternalMessage {

    public static final InternalMessage EMPTY = new InternalMessage("", -1, -1L);
    private final String userAgent;
    private final int responseCode;
    private final long timestamp;

    public InternalMessage(String userAgent, int responseCode, long timestamp) {
        this.userAgent = userAgent;
        this.responseCode = responseCode;
        this.timestamp = timestamp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Message toRawMessage() {
        return new Message(userAgent, responseCode);
    }
}
