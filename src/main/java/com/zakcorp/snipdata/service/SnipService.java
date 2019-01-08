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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class SnipService {
  private final SnipRepository snipRepository;
  private final WebUtility webUtility;
  private final StoreType<String> storeType;
  private final FileResourceType<String> fileResourceType;

  @Autowired
  public SnipService(
    SnipRepository snipRepository,
    WebUtility webUtility, StoreType<String> storeType,
    @Qualifier("${application.file.resource}") FileResourceType<String> fileResourceType) {
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

  public String readSnip(String shortLink) throws IOException {
    SnipInfo snipInfo = snipRepository.findOneByShortLinkAndIsArchived(shortLink, false);
    if (snipInfo == null) {
      return "The sniplink you requested has been expired";
    }
    return fileResourceType.readFromFile(snipInfo.getPastePath());
  }

  public String archiveExpiredSnips() {
    List<SnipInfo> list = snipRepository.findAll();
    List<Long> ids = new ArrayList<>();
    list.forEach(snip -> {
      log.info("snip..data...{}", snip);
      LocalDateTime createdTimeStamp = snip.getCreatedDate();
      LocalDateTime currentTimeStamp = webUtility.getCurrentTimeStamp();
      long minutes = createdTimeStamp.until(currentTimeStamp, ChronoUnit.MILLIS);
      log.info("minutes....{} ", minutes);
      if (minutes > snip.getExpirationLength() && (!snip.isArchived())) {
        snip.setArchived(true);
        snip.setLastModifiedDate(LocalDateTime.now(ZoneId.of("UTC")));
        snipRepository.save(snip);
        ids.add(snip.getId());
      }
    });
    return "Archived id list " + ids.stream().
      map(Objects::toString).
      collect(Collectors.joining(Constants.COMMA_SEPARATOR));
  }

  public String deleteSnipFromStorage() {
    List<SnipInfo> list = snipRepository.findAllByIsArchived(true);
    List<Long> expiredIds = new ArrayList<>();
    list.forEach(expiredSnip -> {
      LocalDateTime lastModifiedTs = expiredSnip.getLastModifiedDate();
      LocalDateTime currentTs = webUtility.getCurrentTimeStamp();
      long days = lastModifiedTs.until(currentTs, ChronoUnit.DAYS);
      if (days > 7) {
        String filePath = expiredSnip.getPastePath();
        File file = new File(filePath);
        if (file.delete()) {
          log.info("File is deleted successfully");
          expiredIds.add(expiredSnip.getId());
        } else {
          log.info("File doesn't exists");
        }
      }
    });
    return "Deleted Snips List " + expiredIds.stream().
      map(Objects::toString).
      collect(Collectors.joining(Constants.COMMA_SEPARATOR));
  }

}
