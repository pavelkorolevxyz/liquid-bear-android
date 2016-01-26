package com.pillowapps.liqear.network;

import com.pillowapps.liqear.helpers.StringUtils;
import com.pillowapps.liqear.network.LastfmApiModule;

import java.util.Map;

public class LastfmApiHelper {
    public String generateApiSig(Map<String, String> params) {
        StringBuilder b = new StringBuilder();
        params.put("api_key", LastfmApiModule.LASTFM_API_KEY);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            b.append(entry.getKey());
            b.append(entry.getValue());
        }
        b.append(LastfmApiModule.LASTFM_API_SECRET);
        return StringUtils.md5(b.toString());
    }
}
