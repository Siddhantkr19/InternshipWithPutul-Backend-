package com.example.sql_admin_auth.service;

import com.example.sql_admin_auth.entity.Internship;
import com.example.sql_admin_auth.entity.Job;
import com.example.sql_admin_auth.entity.User;
import com.example.sql_admin_auth.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchNotificationService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public BatchNotificationService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public void sendJobAlerts(List<Job> newJobs) {
        if (newJobs.isEmpty()) return;

        List<User> subscribers = userRepository.findByNotifyForJobsTrue();
        if (subscribers.isEmpty()) {
            System.out.println("No job subscribers found.");
            return;
        }

        String subject = "🔔 " + newJobs.size() + " New Jobs Found!";
        String body = buildEmailBody(newJobs, "JOB");

        System.out.println("📨 Sending Job Batch Emails to " + subscribers.size() + " users...");
        sendToAll(subscribers, subject, body);
    }

    public void sendInternshipAlerts(List<Internship> newInternships) {
        if (newInternships.isEmpty()) return;

        List<User> subscribers = userRepository.findByNotifyForInternshipsTrue();
        if (subscribers.isEmpty()) {
            System.out.println("No internship subscribers found.");
            return;
        }

        String subject = "✨ " + newInternships.size() + " New Internships Found!";
        String body = buildEmailBody(newInternships, "INTERNSHIP");

        System.out.println("📨 Sending Internship Batch Emails to " + subscribers.size() + " users...");
        sendToAll(subscribers, subject, body);
    }

    private String buildEmailBody(List<?> items, String type) {
        StringBuilder body = new StringBuilder();
        body.append("Hello Future Engineer,\n\n");
        body.append("The Hunter has returned with ").append(items.size()).append(" new ").append(type.toLowerCase()).append("(s):\n\n");

        int count = 1;
        for (Object item : items) {
            if (item instanceof Job) {
                Job j = (Job) item;
                body.append(count++).append(". ").append(j.getRole())
                        .append(" at ").append(j.getCompanyName())
                        .append(" (").append(j.getLocation()).append(")\n");
            } else if (item instanceof Internship) {
                Internship i = (Internship) item;
                body.append(count++).append(". ").append(i.getRole())
                        .append(" at ").append(i.getCompanyName())
                        .append(" (").append(i.getLocation()).append(")\n");
            }
        }

        body.append("\nLog in to your dashboard to apply now!\n");
        body.append("https://internshipwithputul.netlify.app"); // Replace with your frontend URL
        return body.toString();
    }

    private void sendToAll(List<User> subscribers, String subject, String body) {
        for (User user : subscribers) {
            try {
                emailService.sendEmail(user.getEmail(), subject, body);
            } catch (Exception e) {
                System.err.println("Failed to email " + user.getEmail());
            }
        }
    }
}