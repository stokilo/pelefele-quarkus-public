package com.sstec.qpelefele.model.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sstec.qpelefele.config.AppConfigSource;
import com.sstec.qpelefele.model.vm.ErrorsVM;
import com.sstec.qpelefele.model.exceptions.BusinessException;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.jboss.resteasy.reactive.RestResponse;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;

public class ExceptionMapper {

    private static final Logger log = Logger.getLogger(ExceptionMapper.class);

    @ServerExceptionMapper
    public RestResponse<String> mapBusinessException(BusinessException x) {
        return RestResponse.status(Response.Status.BAD_REQUEST, x.details);
    }

    @ServerExceptionMapper
    public RestResponse<String> mapConstraintViolationException(ConstraintViolationException error) {
        ErrorsVM errorsVM = new ErrorsVM();

        String message = "";
        try {
            error.getConstraintViolations().forEach(v -> errorsVM.addError(v.getPropertyPath().toString(),
                    v.getMessage()));
            message = new ObjectMapper().writeValueAsString(errorsVM);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return RestResponse.status(Response.Status.BAD_REQUEST, message);
    }

    @ServerExceptionMapper
    public RestResponse<String> mapThrowable(Throwable x) {
        log.error("Exception mapper error", x);
        return RestResponse.status(Response.Status.BAD_REQUEST, "Unhandled exception raised.");
    }

    @ServerExceptionMapper
    public RestResponse<String> mapException(Exception x) {
        log.error("Exception mapper error", x);
        return RestResponse.status(Response.Status.BAD_REQUEST, "Unhandled exception raised.");
    }
}
