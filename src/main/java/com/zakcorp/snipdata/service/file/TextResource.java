/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.service.file;

import com.zakcorp.snipdata.service.FileResourceType;
import lombok.extern.slf4j.Slf4j;
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

  @Override
  public void storeToFile(String filePath, String content) throws IOException {
    File file = new File(filePath.trim());

    //    PrintWriter pw = new PrintWriter(filePath);
    //    pw.write(content);
    //    pw.close();
  }

  @Override
  public String readFromFile(String filePath) throws IOException {
    return String.valueOf(Files.readAllLines(Paths.get(filePath)));
  }
}
