/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.service.store;

import com.zakcorp.snipdata.service.FileResourceType;
import com.zakcorp.snipdata.service.StoreType;
import com.zakcorp.snipdata.util.Constants;
import com.zakcorp.snipdata.util.WebUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LocalFileStore implements StoreType<String> {

  @Autowired
  private final WebUtility webUtility;

  @Value("${application.file.resource}")
  private String fileResource;

  @Autowired
  @Qualifier("${application.file.resource}")
  private final FileResourceType<String> fileResourceType;

  public LocalFileStore(
    WebUtility webUtility,
    @Qualifier("${application.file.resource}") FileResourceType<String> fileResourceType) {
    this.webUtility = webUtility;
    this.fileResourceType = fileResourceType;
  }

  @Override
  public String saveToStorage(String content) throws IOException {
    String dirPath =
      webUtility.getStorageLocationPrefix() + Constants.DELIMITER + fileResource +
        webUtility.getDatePrefix();
    String fileName = webUtility.getRandomUUID() + Constants.DOT + fileResource;
    return fileResourceType.storeToFile(dirPath, fileName, content);
  }
}
