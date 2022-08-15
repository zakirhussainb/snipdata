/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */
package com.zakcorp.snipdata.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface StoreType<T> {
  T saveToStorage(T content) throws IOException;
}
