package com.example.sql_admin_auth.service;

import com.example.sql_admin_auth.entity.Internship;
import com.example.sql_admin_auth.repository.InternshipRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InternshipService {

    private final InternshipRepository internshipRepository;

    public InternshipService(InternshipRepository internshipRepository) {
        this.internshipRepository = internshipRepository;
    }

    public List<Internship> getAllInternships() {
        return internshipRepository.findAllByOrderByIdDesc();
    }

    public Internship createInternship(Internship internship) {
        return internshipRepository.save(internship);
    }

    public Optional<Internship> updateInternship(Long id, Internship internshipDetails) {
        return internshipRepository.findById(id).map(internship -> {
            internship.setCompanyName(internshipDetails.getCompanyName());
            internship.setRole(internshipDetails.getRole());
            internship.setStipend(internshipDetails.getStipend());
            internship.setLocation(internshipDetails.getLocation());
            internship.setDuration(internshipDetails.getDuration());
            internship.setEligibility(internshipDetails.getEligibility());
            internship.setApplyLink(internshipDetails.getApplyLink());
            internship.setLastDateToApply(internshipDetails.getLastDateToApply());
            return internshipRepository.save(internship);
        });
    }

    public void deleteInternship(Long id) {
        internshipRepository.deleteById(id);
    }
}