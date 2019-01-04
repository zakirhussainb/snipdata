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
import com.zakcorp.snipdata.service.FileResourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@Slf4j
@Qualifier("json")
public class JsonResource implements FileResourceType<String> {

  @Override
  public void storeToFile(String filePath, String content) throws IOException {
    Path path = new Path(filePath);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootNode = mapper.createObjectNode();
    ((ObjectNode) rootNode).put("content", content);
    try {
      mapper.writeValue(new File(filePath), rootNode);
    } catch (Exception e) {
      log.error("Exception here", e);
    }
  }

  @Override
  public String readFromFile(String filePath) throws IOException {
    return new ObjectMapper().readValue(new File(filePath), String.class);
  }
}
