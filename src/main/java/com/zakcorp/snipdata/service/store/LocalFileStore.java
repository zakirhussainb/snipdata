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
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LocalFileStore implements StoreType<String> {

  private final WebUtility webUtility;
  private final FileResourceType<String> fileResourceType;

  @Autowired
  public LocalFileStore(WebUtility webUtility, FileResourceType<String> fileResourceType) {
    this.webUtility = webUtility;
    this.fileResourceType = fileResourceType;
  }

  @Override
  public String saveToStorage(String content) throws IOException {
    String fileLocation = webUtility.getStorageLocationPrefix() + Constants.DELIMITER + "parquet" +
      Constants.DELIMITER +
      webUtility.getDatePrefix() +
      Constants.DELIMITER + webUtility.getRandomUUID() + ".parquet";
    fileResourceType.storeToFile(fileLocation, content);
    return fileLocation;
  }
}
