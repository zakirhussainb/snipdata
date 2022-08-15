/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */
package com.zakcorp.snipdata.config;

import com.zakcorp.snipdata.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HDFSConfiguration {

  @Value("${application.store.hadoop.default-fs}")
  private String defaultFs;

  public org.apache.hadoop.conf.Configuration getHadoopConfiguration() {
    org.apache.hadoop.conf.Configuration config =
      new org.apache.hadoop.conf.Configuration();
    config.set(Constants.FS_DEFAULTFS, defaultFs);
    return config;
  }
}
