package com.pillowapps.liqear.connection;

/**
 * Class definition for a callback to be invoked when the response data for the
 * GET call is available.
 */
public interface GetResponseCallback {

    public void onDataReceived(ReadyResult result);

}
