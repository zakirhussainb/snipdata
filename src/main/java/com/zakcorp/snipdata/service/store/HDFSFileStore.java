/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.service.store;

import com.zakcorp.snipdata.config.HDFSConfiguration;
import com.zakcorp.snipdata.service.FileResourceType;
import com.zakcorp.snipdata.service.StoreType;
import com.zakcorp.snipdata.util.Constants;
import com.zakcorp.snipdata.util.WebUtility;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Qualifier("hdfs")
public class HDFSFileStore implements StoreType<String> {

  private final HDFSConfiguration config;
  private final WebUtility webUtility;
  @Value("${application.file.resource}")
  private String folderName;
  private final FileResourceType<String> fileResourceType;

  @Autowired
  public HDFSFileStore(HDFSConfiguration config, WebUtility webUtility,
    @Qualifier("${application.file.resource}") FileResourceType<String> fileResourceType) {
    this.config = config;
    this.webUtility = webUtility;
    this.fileResourceType = fileResourceType;
  }
  @Override
  public String saveToStorage(String content) throws IOException {
    FileSystem fs = FileSystem.get(config.getHadoopConfiguration());
    String dirPath = fs.getUri() + webUtility.getStorageLocationPrefix() + Constants.DELIMITER + folderName
      + webUtility.getDatePrefix();
    String fileName = webUtility.getRandomUUID() + Constants.DOT + folderName;
    return fileResourceType.storeToFile(dirPath, fileName, content);
  }
}
