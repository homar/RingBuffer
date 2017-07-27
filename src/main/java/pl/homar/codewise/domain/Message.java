package pl.homar.codewise.domain;

import java.util.Objects;

public class Message {

    private final String userAgent;
    private final int responseCode;

    public Message(String userAgent, int responseCode) {
        this.userAgent = userAgent;
        this.responseCode = responseCode;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message that = (Message) o;
        return responseCode == that.responseCode &&
                Objects.equals(userAgent, that.userAgent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userAgent, responseCode);
    }
}
