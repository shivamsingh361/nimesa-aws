package io.nimesa.assignment_aws.client;

import io.nimesa.assignment_aws.entity.Job;
import io.nimesa.assignment_aws.entity.ServiceType;
import io.nimesa.assignment_aws.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class S3Service {
    private final S3Client s3Client;
    private final JobRepository jobRepository;

    @Autowired
    public S3Service(S3Client s3Client, JobRepository jobRepository) {
        this.s3Client = s3Client;
        this.jobRepository = jobRepository;
    }
    Map<String, List<String>> bucketObjectMap = new HashMap<>();

    public List<String> listFiles(String bucketName) {
        ListObjectsV2Request request = ListObjectsV2Request.builder().build();
        ListObjectsV2Response response = s3Client.listObjectsV2(request);

        return response.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    public List<String> discoverS3Buckets() {
        return s3Client.listBuckets().buckets().stream().map(buc -> buc.getValueForField("Name", String.class).get()).collect(Collectors.toList());
    }

    public String discoverS3BucketObjects(String bucketId) {
        String jobId = UUID.randomUUID().toString();
        Job job = jobRepository.save(new Job(jobId, "In Progress"));
        try {
            CompletableFuture<Void> s3Future = CompletableFuture.runAsync(() -> {
                if (!bucketId.isBlank()) {
                    ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucketId).build();
                    ListObjectsV2Response response = s3Client.listObjectsV2(request);

                    bucketObjectMap.put(bucketId, response.contents().stream()
                            .map(S3Object::key)
                            .collect(Collectors.toList()));
                }
            });
            s3Future.thenRun(()-> {
                job.setStatus("Success");
                jobRepository.save(job);
            }).exceptionally(exception -> {
                job.setStatus("Failed");
                jobRepository.save(job);
                return null;
            });
        } catch (Exception exception) {
            job.setStatus("Failed");
            jobRepository.save(job);
        }
        return jobId;
    }

    public int getS3BucketObjectCount(String bucketId) {
        return bucketObjectMap.getOrDefault(bucketId, new ArrayList<>()).size();
    }

    public List<String> getS3BucketObjectlike(String bucketId, String pattern) {
        return bucketObjectMap
                .getOrDefault(bucketId, new ArrayList<>())
                .stream()
                .filter(obj-> obj.matches(pattern))
                .collect(Collectors.toList());
    }
}
