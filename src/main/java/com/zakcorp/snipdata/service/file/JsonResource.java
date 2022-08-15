/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.service.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zakcorp.snipdata.domain.SnipContent;
import com.zakcorp.snipdata.service.FileResourceType;
import com.zakcorp.snipdata.util.Constants;
import com.zakcorp.snipdata.util.WebUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@Slf4j
@Qualifier("json")
public class JsonResource implements FileResourceType<String> {

  private final WebUtility webUtility;

  @Autowired
  public JsonResource(WebUtility webUtility) {
    this.webUtility = webUtility;
  }

  @Override
  public String storeToFile(String dirPath, String fileName, String content) throws IOException {
    File dir = webUtility.createMultipleDirs(dirPath);
    File file = webUtility.createFile(dir, fileName);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootNode = mapper.createObjectNode();
    ((ObjectNode) rootNode).put(Constants.CONTENT, content);
    try {
      mapper.writeValue(file, rootNode);
    } catch (Exception e) {
      log.error("Exception in writing content to json file ", e);
    }
    return file.toString();
  }

  @Override
  public String readFromFile(String filePath) throws IOException {
    return new ObjectMapper().readValue(new File(filePath), SnipContent.class).getContent();
  }
}
