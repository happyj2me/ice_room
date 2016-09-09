package com.alipay.mp2p.signal.server;

/**
 * Http requests callbacks.
 */
public interface AsyncHttpEvents {
  public void onHttpError(String errorMessage);
  public void onHttpComplete(String response);
}
