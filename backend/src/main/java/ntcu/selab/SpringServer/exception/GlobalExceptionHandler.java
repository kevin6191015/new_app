package ntcu.selab.SpringServer.exception;

import ntcu.selab.SpringServer.web.api.ApiEnvelope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiEnvelope<Void>> badCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiEnvelope.error(401, "帳號或密碼錯誤"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiEnvelope<Void>> illegalArg(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(ApiEnvelope.error(400, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiEnvelope<Void>> invalid(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest()
                .body(ApiEnvelope.error(400, "參數驗證失敗"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiEnvelope<Void>> others(Exception ex) {
        // TODO: 於此記錄詳細 log
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiEnvelope.error(500, "伺服器發生錯誤"));
    }
}
