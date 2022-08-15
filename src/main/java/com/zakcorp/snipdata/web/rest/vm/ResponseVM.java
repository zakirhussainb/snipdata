/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.web.rest.vm;

import lombok.Data;

@Data
public class ResponseVM {
  private String status;
  private int statusCode;
  private String data;
  private String message;

  public ResponseVM() {
    this.status = "success";
    this.statusCode = 200;
    this.data = null;
    this.message = "Success";
  }
}
