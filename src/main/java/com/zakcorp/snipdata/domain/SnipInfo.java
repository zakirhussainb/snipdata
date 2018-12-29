package com.zakcorp.snipdata.domain;

import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "snip_info")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
public class SnipInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(name = "shortlink", nullable = false)
  private String shortLink;

  @Column(name = "expiration_length")
  private Long expirationLength = (long) 1440;

  @CreatedDate
  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate = LocalDateTime.now(ZoneId.of("UTC"));

  @LastModifiedDate
  @Column(name = "last_modified_date")
  private LocalDateTime lastModifiedDate;

  @NotNull
  @Column(name = "paste_path", nullable = false)
  private String pastePath;

  @Column(name = "is_archived")
  private boolean isArchived = false;

}
