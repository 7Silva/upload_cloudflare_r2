package github.com._silva.upload_cloudflare_r2.cdn_r2.infrastructure.handler;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import com.mongodb.MongoException;

import github.com._silva.upload_cloudflare_r2.cdn_r2.domain.exceptions.DatabaseConnectionException;
import github.com._silva.upload_cloudflare_r2.cdn_r2.domain.exceptions.FileUploadException;
import github.com._silva.upload_cloudflare_r2.cdn_r2.domain.exceptions.InvalidFileException;
import github.com._silva.upload_cloudflare_r2.cdn_r2.infrastructure.response.ApiResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<String>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error("O arquivo excede o tamanho máximo permitido de 30MB"));
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidFileException(InvalidFileException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<ApiResponse<String>> handleS3Exception(S3Exception e) {
        String message = "Erro ao processar upload para R2: " + e.getMessage();
        // if (e.statusCode() == 403) {
        //     message = "Erro de autenticação com R2. Verifique as credenciais.";
        // }
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(message));
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ApiResponse<String>> handleFileUploadException(FileUploadException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erro ao fazer upload do arquivo: " + e.getMessage()));
    }

    @ExceptionHandler(DatabaseConnectionException.class)
    public ResponseEntity<ApiResponse<String>> handleDatabaseConnectionException(DatabaseConnectionException e) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error("Erro de conexão com o banco de dados: " + e.getMessage()));
    }

    @ExceptionHandler(MongoException.class)
    public ResponseEntity<ApiResponse<String>> handleMongoException(MongoException e) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error("Erro no serviço de banco de dados. Por favor, tente novamente mais tarde."));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<String>> handleDataAccessException(DataAccessException e) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error("Erro ao acessar o banco de dados. Por favor, tente novamente mais tarde."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Acesso negado. Você não tem permissão para realizar esta operação."));
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiResponse<String>> handleMultipartException(MultipartException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Erro no upload do arquivo. Verifique se o arquivo foi enviado corretamente."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Ocorreu um erro interno no servidor. Por favor, tente novamente mais tarde."));
    }
}
