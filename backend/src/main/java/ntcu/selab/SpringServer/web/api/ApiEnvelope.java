package ntcu.selab.SpringServer.web.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ApiEnvelope<T> {
    private int code;       // 200: success
    private String message; // "OK" or error text
    private T data;

    public static <T> ApiEnvelope<T> ok(T data) {
        return new ApiEnvelope<>(200, "OK", data);
    }

    public static <T> ApiEnvelope<T> error(int code, String message) {
        return new ApiEnvelope<>(code, message, null);
    }
}
