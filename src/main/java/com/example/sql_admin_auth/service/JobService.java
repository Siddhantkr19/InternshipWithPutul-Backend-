package com.example.sql_admin_auth.service;

import com.example.sql_admin_auth.dto.JobDTO;
import com.example.sql_admin_auth.entity.Job;
import com.example.sql_admin_auth.repository.JobRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final ModelMapper modelMapper;

    public JobService(JobRepository jobRepository, ModelMapper modelMapper) {
        this.jobRepository = jobRepository;
        this.modelMapper = modelMapper;
    }

    // Convert Entity -> DTO
    private JobDTO convertToDTO(Job job) {
        return modelMapper.map(job, JobDTO.class);
    }

    // Convert DTO -> Entity
    private Job convertToEntity(JobDTO jobDTO) {
        return modelMapper.map(jobDTO, Job.class);
    }

    public List<JobDTO> getAllJobs() {
        return jobRepository.findAllByOrderByIdDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public JobDTO createJob(JobDTO jobDTO) {
        Job job = convertToEntity(jobDTO);
        Job savedJob = jobRepository.save(job);
        return convertToDTO(savedJob);
    }

    public Optional<JobDTO> updateJob(Long id, JobDTO jobDTO) {
        return jobRepository.findById(id).map(existingJob -> {
            // Map new values from DTO to the existing Entity
            // We skip mapping ID to ensure we don't overwrite it accidentally
            modelMapper.map(jobDTO, existingJob);
            existingJob.setId(id); // Ensure ID remains correct

            Job savedJob = jobRepository.save(existingJob);
            return convertToDTO(savedJob);
        });
    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
}