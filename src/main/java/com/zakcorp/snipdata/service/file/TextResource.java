/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.service.file;

import com.zakcorp.snipdata.service.FileResourceType;
import com.zakcorp.snipdata.util.WebUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@Qualifier("txt")
@Slf4j
public class TextResource implements FileResourceType<String> {

  private final WebUtility webUtility;

  @Autowired
  public TextResource(WebUtility webUtility) {
    this.webUtility = webUtility;
  }

  @Override
  public String storeToFile(String dirPath, String fileName, String content) throws IOException {
    File dir = webUtility.createMultipleDirs(dirPath);
    File file = webUtility.createFile(dir, fileName);
    webUtility.writeToFile(file, content);
    return file.toString();
  }

  @Override
  public String readFromFile(String filePath) throws IOException {
    return String.valueOf(Files.readAllLines(Paths.get(filePath)));
  }
}
