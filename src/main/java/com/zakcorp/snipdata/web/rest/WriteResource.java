package com.zakcorp.snipdata.web.rest;

import com.zakcorp.snipdata.domain.Snip;
import com.zakcorp.snipdata.service.SnipService;
import com.zakcorp.snipdata.web.rest.vm.ResponseVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class WriteResource {

  private final SnipService snipService;

  @Autowired
  public WriteResource(SnipService snipService) {
    this.snipService = snipService;
  }

  @GetMapping("/hello")
  String home() {
    return "Hello World! I am Zakir Hussain";
  }

  @PostMapping("/create")
  @ResponseStatus(HttpStatus.CREATED)
  public String writeContent(@Valid @RequestBody Snip snip, HttpServletRequest request) throws IOException, NoSuchAlgorithmException {
    return snipService.saveSnip(snip, request);
  }

  @GetMapping(value = "/snipLink", params = { "snip_link" }, produces = { "application/json" })
  @ResponseStatus(HttpStatus.ACCEPTED)
  public ResponseEntity<ResponseVM> readContent(@RequestParam(value = "snip_link") String snipLink) {
    ResponseVM response = new ResponseVM();
    response.setData(snipService.readSnip(snipLink));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}
