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
import com.zakcorp.snipdata.util.WebUtility;
import com.zakcorp.snipdata.web.rest.SnipResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class SnipService {

  private static final File SCHEMA_LOC = new File(
    "//home//xedflix//zakir-local//mission//projects//snipdata//src//main//resources//snipSchema.avsc");
  private static final Logger deleteLog = LoggerFactory.getLogger(SnipResource.class.getName());
  private final SnipRepository snipRepository;
  private final WebUtility webUtility;
  //  private final HDFSConfiguration hdfsConfiguration;
  private final StoreType<String> storeType;
  private final FileResourceType<String> fileResourceType;

  @Autowired
  public SnipService(
    SnipRepository snipRepository,
    WebUtility webUtility, StoreType<String> storeType,
    FileResourceType<String> fileResourceType) {
    this.snipRepository = snipRepository;
    this.webUtility = webUtility;
    this.storeType = storeType;
    this.fileResourceType = fileResourceType;
  }

  public String saveSnip(
    Snip snip,
    HttpServletRequest request) throws NoSuchAlgorithmException, IOException {
    webUtility.setRequest(request);
    snip.setIpAddress(webUtility.getClientIp());
    String ipAddress = snip.getIpAddress();
    Long timestamp = snip.getTimestamp().getEpochSecond();
    String pasteContent = snip.getSnipContent();
    String fileLocation = storeType.saveToStorage(pasteContent);
    SnipInfo snipInfo = new SnipInfo();
    String shortLink = webUtility.sha1Hash(ipAddress + timestamp);
    log.info("shortLink...." + shortLink);
    snipInfo.setShortLink(shortLink);
    snipInfo.setPastePath(fileLocation);
    snipInfo.setIpAddress(ipAddress);
    snipRepository.save(snipInfo);
    return shortLink;
  }

  /*private String saveToParquet(String pasteContent) throws IOException {
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
  }*/

  public String readSnip(String shortLink) {
    SnipInfo snipInfo = snipRepository.findOneByShortLinkAndIsArchived(shortLink, false);
    if (snipInfo == null) {
      return "The sniplink you requested has been expired";
    }
    return fileResourceType.readFromFile(snipInfo.getPastePath());
  }

  public String readFromStorage(String filePath) {
    String response = "";
    Path path = new Path(filePath);
    try {
      ParquetReader<GenericRecord> reader = AvroParquetReader.<GenericRecord>builder(path).build();
      GenericRecord result = reader.read();
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
      if (minutes > snip.getExpirationLength() && (!snip.isArchived())) {
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
