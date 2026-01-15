package com.example.sql_admin_auth.service;

import com.example.sql_admin_auth.entity.Job;
import com.example.sql_admin_auth.entity.Internship;
import com.example.sql_admin_auth.repository.JobRepository;
import com.example.sql_admin_auth.repository.InternshipRepository;
import com.google.gson.Gson;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScraperService {

    private final JobRepository jobRepository;
    private final InternshipRepository internshipRepository;
    private final GeminiService geminiService;
    private final BatchNotificationService batchNotificationService; // Injected
    private final Gson gson;

    public ScraperService(JobRepository jobRepository,
                          InternshipRepository internshipRepository,
                          GeminiService geminiService,
                          BatchNotificationService batchNotificationService) {
        this.jobRepository = jobRepository;
        this.internshipRepository = internshipRepository;
        this.geminiService = geminiService;
        this.batchNotificationService = batchNotificationService;
        this.gson = new Gson();
    }

    @Scheduled(fixedRate = 14400000) // Runs every 4 hours
    public void runHunter() {
        System.out.println(">>> HUNTER ACTIVATED (Batch Mode): Starting Patrol...");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        // options.addArguments("--headless");

        WebDriver driver = new ChromeDriver(options);

        // --- 1. COLLECTORS ---
        List<Job> newJobs = new ArrayList<>();
        List<Internship> newInternships = new ArrayList<>();

        try {
            huntCategory(driver, "https://freshersrecruitment.co.in/category/jobs/", "JOB", newJobs, newInternships);
            huntCategory(driver, "https://freshersrecruitment.co.in/category/internship/", "INTERNSHIP", newJobs, newInternships);

            // --- 2. HANDOFF TO NOTIFICATION SERVICE ---
            if (!newJobs.isEmpty()) {
                batchNotificationService.sendJobAlerts(newJobs);
            }
            if (!newInternships.isEmpty()) {
                batchNotificationService.sendInternshipAlerts(newInternships);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
            System.out.println(">>> HUNTER SLEEPING.");
        }
    }

    private void huntCategory(WebDriver driver, String url, String type, List<Job> jobBatch, List<Internship> internshipBatch) {
        driver.get(url);
        try { Thread.sleep(3000); } catch (InterruptedException e) {}

        List<WebElement> articles = driver.findElements(By.cssSelector("h2.entry-title a, h1.entry-title a"));
        System.out.println("Found " + articles.size() + " articles in " + type);

        List<String> linksToVisit = articles.stream().limit(6).map(a -> a.getAttribute("href")).toList();

        for (String link : linksToVisit) {

            boolean alreadyExists = (type.equals("JOB"))
                    ? jobRepository.existsByPostUrl(link)
                    : internshipRepository.existsByPostUrl(link);

            if (alreadyExists) {
                System.out.println("⛔ Old Post Found. Stopping category.");
                break;
            }

            System.out.println("Inspecting New: " + link);
            Object savedItem = processSinglePage(driver, link, type);

            // Add to Batch List if saved successfully
            if (savedItem != null) {
                if (savedItem instanceof Job) jobBatch.add((Job) savedItem);
                if (savedItem instanceof Internship) internshipBatch.add((Internship) savedItem);
            }

            driver.navigate().back();
            System.out.println("⏳ Cooldown: Waiting 10s...");
            try { Thread.sleep(10000); } catch (InterruptedException e) {}
        }
    }

    // Updated to return the object (Job/Internship)
    private Object processSinglePage(WebDriver driver, String url, String type) {
        driver.navigate().to(url);
        try { Thread.sleep(3000); } catch (InterruptedException e) {}

        try {
            String messyText = driver.findElement(By.className("entry-content")).getText();
            String pageTitle = driver.getTitle();
            String applyLink = "N/A";

            try {
                WebElement linkElement = driver.findElement(By.linkText("Click Here"));
                applyLink = linkElement.getAttribute("href");
            } catch (Exception e) {
                try {
                    WebElement linkElement = driver.findElement(By.partialLinkText("Apply"));
                    applyLink = linkElement.getAttribute("href");
                } catch (Exception ex) {}
            }

            String jsonResponse = geminiService.extractJobDetails(messyText);

            if (type.equals("JOB")) {
                Job job = gson.fromJson(jsonResponse, Job.class);
                if (job == null) job = new Job();

                if (isBad(job.getCompanyName())) job.setCompanyName(extractCompanyFromTitle(pageTitle));
                if (isBad(job.getRole())) job.setRole("Fresher / Trainee");
                if (isBad(job.getEligibility())) job.setEligibility("B.Tech / BCA / MCA / Non-Tech");
                if (isBad(job.getSalary())) job.setSalary("Best in Industry");
                if (isBad(job.getLocation())) job.setLocation("Pan India / Remote");
                if (isBad(job.getLastDateToApply())) job.setLastDateToApply("ASAP");
                if (isBad(job.getDuration())) job.setDuration("Full Time");

                job.setApplyLink(applyLink);
                job.setPostUrl(url);

                jobRepository.save(job);
                System.out.println("✅ SAVED JOB: " + job.getRole());
                return job; // Return for batching

            } else {
                Internship internship = gson.fromJson(jsonResponse, Internship.class);
                if (internship == null) internship = new Internship();

                if (isBad(internship.getCompanyName())) internship.setCompanyName(extractCompanyFromTitle(pageTitle));
                if (isBad(internship.getRole())) internship.setRole("Intern Trainee");
                if (isBad(internship.getEligibility())) internship.setEligibility("B.Tech / BCA / MCA Students");
                if (isBad(internship.getStipend())) internship.setStipend("Best in Industry");
                if (isBad(internship.getLocation())) internship.setLocation("Remote");
                if (isBad(internship.getLastDateToApply())) internship.setLastDateToApply("ASAP");
                if (isBad(internship.getDuration())) internship.setDuration("6 Months");

                internship.setApplyLink(applyLink);
                internship.setPostUrl(url);

                internshipRepository.save(internship);
                System.out.println("✅ SAVED INTERNSHIP: " + internship.getRole());
                return internship; // Return for batching
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to scrape: " + url);
            return null;
        }
    }

    private boolean isBad(String value) {
        return value == null || value.isEmpty() || value.equalsIgnoreCase("N/A") || value.equalsIgnoreCase("null");
    }

    private String extractCompanyFromTitle(String title) {
        if (title == null) return "Top Company";
        String[] parts = title.split(" ");
        return parts.length > 0 ? parts[0] : "Top Company";
    }
}