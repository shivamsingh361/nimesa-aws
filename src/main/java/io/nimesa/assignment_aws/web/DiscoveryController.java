package io.nimesa.assignment_aws.web;

import io.nimesa.assignment_aws.service.DiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discovery")
public class DiscoveryController {

    @Autowired
    private DiscoveryService discoveryService;

    @PostMapping("/discoverServices")
    public ResponseEntity<String> discoverServices(@RequestBody List<String> services) {
        String jobId = discoveryService.discoverServices(services);
        return ResponseEntity.ok(jobId);
    }

    @GetMapping("/getJobResult/{jobId}")
    public ResponseEntity<String> getJobResult(@PathVariable String jobId) {
        String status = discoveryService.getJobResult(jobId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/getDiscoveryResult/{serviceName}")
    public ResponseEntity<List<String>> getDiscoveryResult(@PathVariable String serviceName) {
        return ResponseEntity.ok(discoveryService.getDiscoveryResult(serviceName));
    }
}

