/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.service.impl;

import com.zakcorp.snipdata.service.Store;

import java.io.IOException;

public class LocalFileStore implements Store<String> {

  @Override
  public String saveToStorage(String content) throws IOException {
    return null;
  }
}
