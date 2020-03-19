package com.neueda.shortenedurl.controller;

import java.net.URI;
import java.util.regex.Pattern;

import com.neueda.shortenedurl.model.StatisticEntity;
import com.neueda.shortenedurl.model.UrlEntity;
import com.neueda.shortenedurl.services.statistic.StatisticService;
import com.neueda.shortenedurl.services.url.UrlService;
import com.neueda.shortenedurl.utils.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/url")
public class UrlController {
    Logger logger = LoggerFactory.getLogger(UrlController.class);

    private static Pattern URL = Pattern.compile("https?://[^\\s]+");
    @Autowired
    private UrlService service;

    @Autowired
    private StatisticService statisticService;

    @PostMapping(value = "/shortener")
    public ResponseEntity<String> create(@RequestParam("longurl") String longurl) throws Exception {
        logger.info("Received url to shorten: " + longurl);
        if (!URL.matcher(longurl).matches()) {
            ResponseEntity.badRequest().body("invalid url");
        }
        return ResponseEntity.ok(GsonUtils.DEFAULT_GSON.toJson(service.save(longurl)));
    }

    @GetMapping(path = "/{code}")
    public ResponseEntity<UrlEntity> findAndRedirect(@PathVariable String code, @CookieValue(value = "userId", required = true) String userId) {
        logger.info("Redirecting code to URL ", code);
        UrlEntity url = service.find(code);
        StatisticEntity urlstats = statisticService.buildUrlStatistics(userId, url);
        statisticService.create(urlstats);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url.getLongUrl()));

        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

}