package io.nimesa.assignment_aws.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Job {
    @Id
    private String jobId;
    private String status;

    public Job(String jobId, String status) {
        this.jobId = jobId;
        this.status = status;
    }
}
