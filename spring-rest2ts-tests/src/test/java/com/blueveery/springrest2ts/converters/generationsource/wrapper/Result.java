package com.blueveery.springrest2ts.converters.generationsource.wrapper;


public class Result {
    private static final Result SUCCESS = new Result("success");
    private static final Result ERROR = new Result("error");

    private final String info;

    public Result(final String info) {
        this.info = info;
    }

    public static Result success() {
        return SUCCESS;
    }

    public static Result error() {
        return ERROR;
    }

    public String getInfo() {
        return info;
    }
}