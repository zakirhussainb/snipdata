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
import com.zakcorp.snipdata.web.rest.SnipResource;
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
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.SeekableInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class SnipService {

  private static Schema SCHEMA;
  private static final File SCHEMA_LOC = new File("//home//xedflix//zakir-local//mission//projects//snipdata//src//main//resources//snipSchema.avsc");
  private final SnipRepository snipRepository;
  private final WebUtility webUtility;
  private static final Logger deleteLog = LoggerFactory.getLogger(SnipResource.class.getName());
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
//    log.info("snip..." + snip);
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

  public String readSnip(String shortLink) {
    SnipInfo snipInfo = snipRepository.findOneByShortLink(shortLink);
//    log.info("snipInfo....{}", snipInfo);
    String filePath = snipInfo.getPastePath();
//    log.info("filePath....{} ", filePath);
    return readFromStorage(filePath);
  }

  public String readFromStorage(String filePath) {
    String response = "";
    Path path = new Path(filePath);
    try {
      ParquetReader<GenericRecord> reader = AvroParquetReader.<GenericRecord>builder(path).build();
      GenericRecord result = reader.read();
//      log.info("result data.....{} ", result.get("content"));
      response = result.get("content").toString();
    } catch (Exception e) {
      log.error("Exception in locating the requested file {} ", e);
    }
    return response;
  }

  public String deleteSnip() {
    //delete from snip_info where id=1;
    List<SnipInfo> list = snipRepository.findAll();
    List<Long> ids = new ArrayList<>();
    list.forEach(snip -> {
      log.info("snip..data...{}", snip);
      LocalDateTime createdTimeStamp = snip.getCreatedDate();
      LocalDateTime currentTimeStamp = webUtility.getCurrentTimeStamp();
      long minutes = createdTimeStamp.until(currentTimeStamp, ChronoUnit.MINUTES);
      log.info("minutes....{} ", minutes);
      if ( minutes > snip.getExpirationLength() && (!snip.isArchived()) ) {
          snip.setArchived(true);
          snipRepository.save(snip);
          ids.add(snip.getId());
      }
    });
    return "Deleted " + ids.stream().
      map(Objects::toString).
      collect(Collectors.joining(","));
  }

}
