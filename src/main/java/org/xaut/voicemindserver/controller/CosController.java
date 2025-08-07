package org.xaut.voicemindserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xaut.voicemindserver.Service.CosService;

import java.util.Map;

@RestController
@RequestMapping("/api/cos")
@RequiredArgsConstructor
public class CosController {

    private final CosService cosService;

    @GetMapping("/sts")
    public Map<String, Object> getCosSts() throws Exception {
        return cosService.getTemporaryCredentials();
    }
}
