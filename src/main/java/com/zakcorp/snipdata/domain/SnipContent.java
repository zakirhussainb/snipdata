/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class SnipContent implements Serializable {

  private static final Long serialVersionUID = 1L;

  private String Content;
}
