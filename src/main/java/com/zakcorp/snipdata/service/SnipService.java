/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.service;

import com.zakcorp.snipdata.domain.Snip;
import com.zakcorp.snipdata.domain.SnipInfo;
import com.zakcorp.snipdata.repository.SnipRepository;
import com.zakcorp.snipdata.util.Constants;
import com.zakcorp.snipdata.util.WebUtility;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.io.InputFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class SnipService {

  private static Schema SCHEMA;
  private static final File SCHEMA_LOC = new File("//home//xedflix//zakir-local//mission//projects//snipdata//src//main//resources//snipSchema.avsc");
  private final SnipRepository snipRepository;
  private final WebUtility webUtility;
  //  private final HDFSConfiguration hdfsConfiguration;
  @Autowired
  public SnipService(
    SnipRepository snipRepository,
    WebUtility webUtility) {
    this.snipRepository = snipRepository;
    this.webUtility = webUtility;
  }

  public String saveSnip(
    Snip content,
    HttpServletRequest request) throws NoSuchAlgorithmException, IOException {
    webUtility.setRequest(request);
    content.setIpAddress(webUtility.getClientIp());
    log.info("ipAddress..." + webUtility.getClientIp());
    log.info("content..." + content);
    String ipAddress = content.getIpAddress();
    Long timestamp = content.getTimestamp().getEpochSecond();
    String pasteContent = content.getSnipContent();
    String shortLink = "http://" + ipAddress + "/" + webUtility.sha1Hash(ipAddress + timestamp);
    String fileLocation = saveToParquet(pasteContent);
    SnipInfo snipInfo = new SnipInfo();
    snipInfo.setShortLink(shortLink);
    snipInfo.setPastePath(fileLocation);
    snipRepository.save(snipInfo);
    log.info("shortLink...." + shortLink);
    return shortLink;
  }

  private String saveToParquet(String pasteContent) throws IOException {
    SCHEMA = new Schema.Parser().parse(SCHEMA_LOC);
    System.out.println("SCHEMA..." + SCHEMA);
    String fileLocation = webUtility.getStorageLocationPrefix() + Constants.DELIMITER +
      webUtility.getDatePrefix() +
      Constants.DELIMITER + webUtility.getRandomUUID() + ".parquet";
    GenericRecord record = new GenericData.Record(SCHEMA);
    record.put("Content", pasteContent);
    Path path = new Path(fileLocation);
    try {
      ParquetWriter writer = AvroParquetWriter.builder(path)
        .withSchema(SCHEMA)
        .build();
      writer.write(record);
    } catch (Exception e) {
      log.error("Exception in writing content to parquet file {}", e);
    }
    return fileLocation;
  }

  public Object readSnip(String shortLink) {
    String filePath = snipRepository.findPastePathByShortLink(shortLink);
    return readFromStorage(filePath);
  }

  public Object readFromStorage(String filePath) {
    Object response = null;
    Path path = new Path(filePath);
    try {
      ParquetReader reader = AvroParquetReader.builder(path).build();
      response = reader.read();
    } catch (Exception e) {
      log.error("Exception in locating the requested file {} ", e);
    }
    return response;
  }

}
