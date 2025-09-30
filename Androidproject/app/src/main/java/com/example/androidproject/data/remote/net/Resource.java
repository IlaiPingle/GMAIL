package com.example.androidproject.data.remote.net;

public class Resource<T> {
    public enum Status {SUCCESS, ERROR, LOADING}

    private final Status status;
    private final T data;
    private final String message;
    private final int errorCode;
    private final Throwable cause;

    private Resource(Status status, T data, String message, int errorCode, Throwable cause) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.errorCode = errorCode;
        this.cause = cause;
    }

    // Factories
    public static <T> Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null, 0, null);
    }

    public static <T> Resource<T> loading(T data) {
        return new Resource<>(Status.LOADING, data, null, 0, null);
    }

    public static <T> Resource<T> error(String message, int errorCode) {
        return new Resource<>(Status.ERROR, null, message, errorCode, null);
    }

    public static <T> Resource<T> error(String message, int errorCode, T fallbackData, Throwable cause) {
        return new Resource<>(Status.ERROR, fallbackData, message, errorCode, cause);
    }

    //Helpers
    public Resource<T> withData(T newData) {
        return new Resource<>(status, newData, message, errorCode, cause);
    }

    public Resource<T> withMessage(String msg) {
        return new Resource<>(status, data, msg, errorCode, cause);
    }

    public Resource<T> withCause(Throwable c) {
        return new Resource<>(status, data, message, errorCode, c);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isLoading() {
        return status == Status.LOADING;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

    // Getters
    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public Throwable getCause() {
        return cause;
    }
}
