package com.pillowapps.liqear.connection;

/**
 *
 * Class definition for a callback to be invoked when the response for the data
 * submission is available.
 *
 */
public interface PostCallback{
    /**
     * Called when a POST success response is received. <br/>
     * This method is guaranteed to execute on the UI thread.
     */
    public void onPostSuccess();

}
