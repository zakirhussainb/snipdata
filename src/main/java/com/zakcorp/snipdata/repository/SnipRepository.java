/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.repository;

import com.zakcorp.snipdata.domain.SnipInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnipRepository extends JpaRepository<SnipInfo, Long> {
  //  public String findOne
  //  select paste_path from snip_info where shortlink='';
  SnipInfo findOneByShortLinkAndIsArchived(String shortLink, boolean isArchived);

}
