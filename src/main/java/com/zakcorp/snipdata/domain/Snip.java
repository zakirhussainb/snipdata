/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Data
public class Snip implements Serializable {

  private static final Long serialVersionUID = 1L;

  private String ipAddress;

  private String snipContent;

  private Instant timestamp = Instant.now(Clock.system(ZoneId.of("UTC")));

}
