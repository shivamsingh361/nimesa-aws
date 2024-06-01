package io.nimesa.assignment_aws.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EC2Service {
    private final Ec2Client ec2Client;

    @Autowired
    public EC2Service(Ec2Client ec2Client) {
        this.ec2Client = ec2Client;
    }

    public List<String> discoverEC2Instances() {
        DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();
        DescribeInstancesResponse response = ec2Client.describeInstances(request);

        return response.reservations().stream()
                .flatMap(reservation -> reservation.instances().stream())
                .map(Instance::instanceId)
                .collect(Collectors.toList());
    }

}
