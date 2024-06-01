package io.nimesa.assignment_aws.exception;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


public class JobNotFoundException extends RuntimeException {
    String msg;
    public JobNotFoundException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
