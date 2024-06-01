package io.nimesa.assignment_aws.service;

import io.nimesa.assignment_aws.entity.ServiceType;
import io.nimesa.assignment_aws.repository.JobRepository;
import io.nimesa.assignment_aws.client.EC2Service;
import io.nimesa.assignment_aws.client.S3Service;
import io.nimesa.assignment_aws.entity.Job;
import io.nimesa.assignment_aws.exception.JobNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class DiscoveryService {

    @Autowired
    private EC2Service ec2Service;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private JobRepository jobRepository;

    Map<ServiceType, List<String>> discoveryRes = new HashMap<>();


    public String discoverServices(List<String> services) {
        String jobId = UUID.randomUUID().toString();
        Job job = jobRepository.save(new Job(jobId, "In Progress"));

        try {
            CompletableFuture<Void> ec2Future = CompletableFuture.runAsync(() -> {
                if (services.contains("EC2")) {
                    discoveryRes.put(ServiceType.EC2, ec2Service.discoverEC2Instances());
                }
            });

            CompletableFuture<Void> s3Future = CompletableFuture.runAsync(() -> {
                if (services.contains("S3")) {
                    discoveryRes.put(ServiceType.S3, s3Service.discoverS3Buckets());
                }
            });

            CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(ec2Future, s3Future);

            combinedFuture.thenRun(() -> {
                job.setStatus("Success");
                jobRepository.save(job);
            }).exceptionally(exception -> {
                job.setStatus("Failed");
                jobRepository.save(job);
                return null;
            }).join(); // Ensures the main thread waits for the completion

        } catch (Exception exception) {
            job.setStatus("Failed");
            jobRepository.save(job);
        }

        return jobId;
    }

    public String getJobResult(String jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
        return job.getStatus();
    }

    public List<String> getDiscoveryResult(String serviceName) {
        if (serviceName.equalsIgnoreCase("EC2")) {
            return discoveryRes.get(ServiceType.EC2);
        } else if (serviceName.equalsIgnoreCase("S3")) {
            return discoveryRes.get(ServiceType.S3);
        } else {
            throw new IllegalArgumentException("Unsupported service: " + serviceName);
        }
    }
}

