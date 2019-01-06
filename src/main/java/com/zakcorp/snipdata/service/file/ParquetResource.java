/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.service.file;

import com.zakcorp.snipdata.service.FileResourceType;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@Slf4j
@Qualifier("parquet")
public class ParquetResource implements FileResourceType<String> {

  private static final File SCHEMA_LOC = new File(
    "//home//xedflix//zakir-local//mission//projects//snipdata//src//main//resources//snipSchema.avsc");

  private final WebUtility webUtility;

  @Autowired
  public ParquetResource(WebUtility webUtility) {
    this.webUtility = webUtility;
  }

  @Override
  public String storeToFile(String dirPath, String fileName, String content) throws IOException {
    Schema schema = new Schema.Parser().parse(SCHEMA_LOC);
    GenericRecord data = new GenericData.Record(schema);
    data.put(Constants.CONTENT, content);
    String filePath = dirPath + Constants.DELIMITER + fileName;
    Path path = new Path(filePath);
    try {
      AvroParquetWriter<GenericRecord> writer = new AvroParquetWriter<GenericRecord>(path, schema);
      writer.write(data);
      writer.close();
    } catch (Exception e) {
      log.error("Error in writing content to parquet file {} ", e);
    }
    return path.toString();
  }

  @Override
  public String readFromFile(String filePath) {
    String response = "";
    Path path = new Path(filePath);
    try {
      ParquetReader<GenericRecord> reader = AvroParquetReader.<GenericRecord>builder(path).build();
      GenericRecord result = reader.read();
      response = result.get(Constants.CONTENT).toString();
    } catch (Exception e) {
      log.error("Exception in locating the requested file {} ", e);
    }
    return response;
  }
}
