/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.service.file;

import com.zakcorp.snipdata.service.FileResourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetReader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class ParquetResource implements FileResourceType<String> {

  private static final File SCHEMA_LOC = new File(
    "//home//xedflix//zakir-local//mission//projects//snipdata//src//main//resources//snipSchema.avsc");

  @Override
  public String storeToFile(String filePath, String content) throws IOException {
    Schema schema = new Schema.Parser().parse(SCHEMA_LOC);
    GenericRecord data = new GenericData.Record(schema);
    data.put("content", content);
    Path path = new Path(filePath);
    try {
      AvroParquetWriter<GenericRecord> writer = new AvroParquetWriter<GenericRecord>(path, schema);
      writer.write(data);
      writer.close();
    } catch (Exception e) {
      log.error("Error in writing content to parquet file {} ", e);
    }
    return filePath;
  }

  @Override
  public String readFromFile(String filePath) {
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
}
