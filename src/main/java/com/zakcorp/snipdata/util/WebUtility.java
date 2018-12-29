/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;

@Component
public class WebUtility {

  private HttpServletRequest request;

  @Value("${application.store.path}")
  private String path;

  @Autowired
  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }

  public String getClientIp() {
    String remoteAddr = "";

    if (request != null) {
      remoteAddr = request.getHeader("X-FORWARDED-FOR");
      if (remoteAddr == null || remoteAddr.isEmpty()) {
        remoteAddr = request.getRemoteAddr();
      }
    }
    return remoteAddr;
  }

  public String sha1Hash(String str) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA1");
    md.update(str.getBytes());
    return Base62.encode(md.digest());
  }

  public String getDatePrefix() {
    LocalDate currentDate = LocalDate.now();
    return currentDate.getYear() + Constants.DELIMITER + currentDate.getMonthValue() +
      Constants.DELIMITER + currentDate.getDayOfMonth();
  }

  public String getStorageLocationPrefix() {
    return path;
  }

  public String getRandomUUID() {
    return UUID.randomUUID().toString();
  }

  public LocalDateTime getCurrentTimeStamp() {
    return LocalDateTime.now(ZoneId.of("UTC"));
  }

}
