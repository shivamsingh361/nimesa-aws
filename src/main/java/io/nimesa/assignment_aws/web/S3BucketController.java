package io.nimesa.assignment_aws.web;

import io.nimesa.assignment_aws.client.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/s3")
public class S3BucketController {

    @Autowired
    private S3Service s3Service;

    @PostMapping("/getS3BucketObjects")
    public ResponseEntity<String> getS3BucketObjects(@RequestBody String bucketName) {
        String jobId = s3Service.discoverS3BucketObjects(bucketName);
        return ResponseEntity.ok(jobId);
    }

    @GetMapping("/getS3BucketObjectCount/{bucketName}")
    public ResponseEntity<Integer> getS3BucketObjectCount(@PathVariable String bucketName) {
        int count = s3Service.getS3BucketObjectCount(bucketName);
        return ResponseEntity.ok(count);
    }
    @GetMapping("/getS3BucketObjectlike")
    public ResponseEntity<List<String>> getS3BucketObjectlike(
            @RequestParam String bucketName,
            @RequestParam String pattern) {
        List<String> matchingObjects = s3Service.getS3BucketObjectlike(bucketName, pattern);
        return ResponseEntity.ok(matchingObjects);
    }
}
