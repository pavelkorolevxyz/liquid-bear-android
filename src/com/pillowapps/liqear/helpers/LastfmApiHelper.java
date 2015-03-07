package com.pillowapps.liqear.helpers;

import com.pillowapps.liqear.network.ServiceHelper;

import java.util.Map;

public class LastfmApiHelper {
    public String generateApiSig(Map<String, String> params) {
        StringBuilder b = new StringBuilder();
        params.put("api_key", ServiceHelper.LASTFM_API_KEY);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            b.append(entry.getKey());
            b.append(entry.getValue());
        }
        b.append(ServiceHelper.LASTFM_API_SECRET);
        return StringUtils.md5(b.toString());
    }
}
