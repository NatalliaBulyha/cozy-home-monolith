package com.cozyhome.onlineshop.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;

public class FeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message;
        try (InputStream responseBody = response.body().asInputStream()) {
            message = IOUtils.toString(responseBody);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        switch(HttpStatus.valueOf(response.status())) {
            case BAD_REQUEST:
                return new BadRequestException(message);
            case NOT_FOUND:
                return new ResourceNotFoundException(message);
            case NOT_ACCEPTABLE:
                return new NotAcceptableException(message);
            default:
                return errorDecoder.decode(methodKey, response);
        }
    }
}
