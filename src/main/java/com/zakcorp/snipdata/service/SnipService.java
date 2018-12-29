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
import org.apache.avro.io.DatumWriter;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.column.ParquetProperties;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.io.InputFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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
    Snip snip,
    HttpServletRequest request) throws NoSuchAlgorithmException, IOException {
    webUtility.setRequest(request);
    snip.setIpAddress(webUtility.getClientIp());
    log.info("ipAddress..." + webUtility.getClientIp());
    log.info("snip..." + snip);
    String ipAddress = snip.getIpAddress();
    Long timestamp = snip.getTimestamp().getEpochSecond();
    String pasteContent = snip.getSnipContent();
    String shortLink = webUtility.sha1Hash(ipAddress + timestamp);
    String fileLocation = saveToParquet(pasteContent);
    SnipInfo snipInfo = new SnipInfo();
    snipInfo.setShortLink(shortLink);
    snipInfo.setPastePath(fileLocation);
    snipRepository.save(snipInfo);
    log.info("shortLink...." + shortLink);
    return shortLink;
  }

  private String saveToParquet(String pasteContent) throws IOException {
    Schema schema = new Schema.Parser().parse(SCHEMA_LOC);
    GenericRecord data = new GenericData.Record(schema);
    data.put("content", pasteContent);
    String fileLocation = webUtility.getStorageLocationPrefix() + Constants.DELIMITER +
      webUtility.getDatePrefix() +
      Constants.DELIMITER + webUtility.getRandomUUID() + ".parquet";
    Path path = new Path(fileLocation);
    try {
      AvroParquetWriter<GenericRecord> writer = new AvroParquetWriter<GenericRecord>(path, schema);
      writer.write(data);
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return fileLocation;
  }

  public Object readSnip(String shortLink) {
    SnipInfo snipInfo = snipRepository.findOneByShortLink(shortLink);
    log.info("snipInfo....{}", snipInfo);
    String filePath = snipInfo.getPastePath();
    log.info("filePath....{} ", filePath);
    return readFromStorage(filePath);
  }

  public Object readFromStorage(String filePath) {
    Object response = null;
    Path path = new Path(filePath);
    try {
      AvroParquetReader<GenericRecord> reader = new AvroParquetReader<GenericRecord>(path);
      GenericRecord result = reader.read();
      response = result.get("content");
    } catch (Exception e) {
      log.error("Exception in locating the requested file {} ", e);
    }
    return response;
  }

}
