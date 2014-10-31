package com.pillowapps.liqear.connection;

import com.pillowapps.liqear.connection.Params.ApiSource;
import org.json.JSONException;
import org.json.JSONObject;

public class Result {
    private Status status;
    private String errorMessage = null;
    private Long errorCode = null;
    private ApiSource apiSource;
    private String additionalParameter;
    private JSONObject resultDocument;
    private String resultDocumentString;
    private String method;

    public Result(String document, ApiSource apiSource, String method,
                  String additionalParameter) {
        this.status = Status.OK;
        try {
            this.resultDocument = new JSONObject(document);
        } catch (JSONException e) {
            this.resultDocument = null;
            this.resultDocumentString = document;
        }
        this.apiSource = apiSource;
        this.method = method;
        this.additionalParameter = additionalParameter;
    }

    public boolean isOk() {
        return this.status == Status.OK;
    }

    public JSONObject getResultDocument() {
        return resultDocument;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "Result [status=" + status + ", errorMessage=" + errorMessage
                + ", errorCode=" + getErrorCode() + ", apiSource=" + apiSource
                + ", additionalParameter=" + additionalParameter
                + ", resultDocument=" + resultDocument + ", method=" + method
                + "]";
    }

    public Long getErrorCode() {
        return errorCode;
    }

    public String getResultDocumentString() {
        return resultDocumentString;
    }

    public enum Status {
        OK, FAILED
    }

}
