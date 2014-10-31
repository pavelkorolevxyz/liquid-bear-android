package com.pillowapps.liqear.connection;

import com.pillowapps.liqear.helpers.AuthorizationInfoManager;
import com.pillowapps.liqear.helpers.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Params {
    private String methodString;
    private ApiMethod methodEnum;
    private Map<String, String> params = new HashMap<String, String>();
    private ApiSource apiSource;
    private String additionalParameter;
    private String url;

    public Params(String methodString, ApiMethod methodEnum) {
        this.methodString = methodString;
        this.methodEnum = methodEnum;
    }

    public Params(String methodString, ApiMethod methodEnum, String url) {
        this.methodString = methodString;
        this.methodEnum = methodEnum;
        this.url = url;
    }

    public Params(String methodString, ApiMethod methodEnum, Map<String, String> params) {
        this.methodString = methodString;
        this.methodEnum = methodEnum;
        this.params = params;
    }

    public String buildParameterQueue() {
        Map<String, String> localParams = new HashMap<String, String>(params);
        StringBuilder builder = new StringBuilder(100);
        switch (apiSource) {
            case VK:
                for (Iterator<Entry<String,
                        String>> it = localParams.entrySet().iterator(); it.hasNext(); ) {
                    Entry<String, String> entry = it.next();
                    builder.append(entry.getKey());
                    builder.append('=');
                    builder.append(StringUtils.encode(entry.getValue()));
                    if (it.hasNext()) {
                        builder.append('&');
                    }
                }
                builder.append("&access_token=")
                        .append(AuthorizationInfoManager.getVkAccessToken());
                break;
            case LASTFM:
                builder.append("method=");
                builder.append(methodString);
                builder.append('&');
                builder.append("format=json&");
                for (Iterator<Entry<String, String>> it = localParams.entrySet()
                        .iterator(); it.hasNext(); ) {
                    Entry<String, String> entry = it.next();
                    if (entry != null && entry.getValue() != null) {
                        builder.append(entry.getKey());
                        builder.append('=');
                        builder.append(StringUtils.encode(entry.getValue()));
                        if (it.hasNext()) {
                            builder.append('&');
                        }
                    }
                }
                break;
            default:
                break;
        }
        return builder.toString();
    }

    public void putParameter(String parameterName, String parameterValue) {
        params.put(parameterName, parameterValue);
    }

    public ApiSource getApiSource() {
        return apiSource;
    }

    public void setApiSource(ApiSource apiSource) {
        this.apiSource = apiSource;
    }

    public String getUrl() {
        switch (apiSource) {
            case LASTFM:
                return "http://ws.audioscrobbler.com/2.0/";
            case VK:
                return "https://api.vk.com/method/"
                        + methodString;
            default:
                return url;
        }
    }

    public ApiMethod getMethodEnum() {
        return methodEnum;
    }

    public void setMethodEnum(ApiMethod methodEnum) {
        this.methodEnum = methodEnum;
    }

    public String getMethodString() {
        return methodString;
    }

    public void setMethodString(String methodString) {
        this.methodString = methodString;
    }

    public String getAdditionalParameter() {
        return additionalParameter;
    }

    public void setAdditionalParameter(String additionalParameter) {
        this.additionalParameter = additionalParameter;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public enum ApiSource {
        LASTFM, VK, FUNKY, STRAIGHT, SETLISTFM
    }
}