package com.kaltura.netkit.utils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.kaltura.netkit.utils.ErrorElement.ErrorCode.BadRequestErrorCode;
import static com.kaltura.netkit.utils.ErrorElement.ErrorCode.CanceledRequestCode;
import static com.kaltura.netkit.utils.ErrorElement.ErrorCode.ConnectionErrorCode;
import static com.kaltura.netkit.utils.ErrorElement.ErrorCode.GeneralErrorCode;
import static com.kaltura.netkit.utils.ErrorElement.ErrorCode.InternalServerErrorCode;
import static com.kaltura.netkit.utils.ErrorElement.ErrorCode.LoadErrorCode;
import static com.kaltura.netkit.utils.ErrorElement.ErrorCode.NotFoundCode;
import static com.kaltura.netkit.utils.ErrorElement.ErrorCode.ServiceUnavailableErrorCode;
import static com.kaltura.netkit.utils.ErrorElement.ErrorCode.SessionErrorCode;

/**
 */

public class ErrorElement {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({BadRequestErrorCode, NotFoundCode, ConnectionErrorCode, InternalServerErrorCode,
            ServiceUnavailableErrorCode, LoadErrorCode, CanceledRequestCode, SessionErrorCode, GeneralErrorCode})
    public @interface ErrorCode{
        int BadRequestErrorCode = 400;
        int NotFoundCode = 404;
        int ConnectionErrorCode = 408;

        int InternalServerErrorCode = 500;
        int ServiceUnavailableErrorCode = 503;
        int LoadErrorCode = 518;
        int CanceledRequestCode = 520;

        int SessionErrorCode = 601;
        int GeneralErrorCode = 666;
    }

    public static ErrorElement GeneralError = new ErrorElement("GeneralError", "Something went wrong", GeneralErrorCode);
    public static ErrorElement NotFound = new ErrorElement("NotFound", "Resource not found", NotFoundCode);
    public static ErrorElement LoadError = new ErrorElement("LoadError", "Failed to load data from source", LoadErrorCode);
    public static ErrorElement ServiceUnavailableError = new ErrorElement("ServiceUnavailableError", "Requested service is unavailable", ServiceUnavailableErrorCode);
    public static ErrorElement CanceledRequest = new ErrorElement("CanceledRequest", "Request was canceled", CanceledRequestCode);
    public static ErrorElement ConnectionError = new ErrorElement("ConnectionError", "Failed to connect to source", ConnectionErrorCode);
    public static ErrorElement BadRequestError = new ErrorElement("BadRequestError", "Invalid or missing request params", BadRequestErrorCode);
    public static ErrorElement SessionError = new ErrorElement("SessionError", "Failed to obtain session", SessionErrorCode);

    private String name;
    private String message;
    private String code;
    protected Object extra;

    public ErrorElement(String name, String message, int code) {
        this(message, code);
        this.name = name;
    }

    public ErrorElement(String message, String code, Object extra) {
        this.message = message;
        this.code = code;
        this.extra = extra;
    }

    public ErrorElement(String message, int code) {
        this(message, code + "");
    }

    public ErrorElement(String message, String code) {
        this(message, code, null);
    }

    public ErrorElement setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    /**
     * enable user to change default message with his own
     *
     * @param message
     * @return
     */
    public ErrorElement message(String message) {
        this.message = message;
        return this;
    }

    public ErrorElement addMessage(String message) {
        this.message += "; " + message;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Object getExtra() {
        return extra;
    }

    public static ErrorElement fromCode(int code, String message) {
        switch (code) {
            case 404:
                return ErrorElement.NotFound.message(message);
            case 400:
                return ErrorElement.BadRequestError.message(message);
            default:
                return new ErrorElement(message, code);
        }
    }

    public static ErrorElement fromException(Exception exception) {
        switch (exception.getClass().getSimpleName()) {
            case "SocketTimeoutException":
                return new ErrorElement(ErrorElement.ConnectionError.getName(), "SocketTimeoutException Exception: " + ConnectionErrorCode, exception);
            case "UnknownHostException":
                return new ErrorElement(ErrorElement.ConnectionError.getName(), "UnknownHostException Exception: " + ConnectionErrorCode, exception);
            case "SSLException":
                return new ErrorElement(ErrorElement.ConnectionError.getName(), "SSLException: android.os=" + android.os.Build.VERSION.SDK_INT + ": " + ConnectionErrorCode, exception);
            default:
                return new ErrorElement(ErrorElement.GeneralError.getName(), "Exception: " + GeneralErrorCode, exception);

        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(name != null){
            builder.append("Error: ").append(name).append("; ");
        }
        builder.append("code:").append(code).append(", Message:").append(message);
        return builder.toString();
    }
}
