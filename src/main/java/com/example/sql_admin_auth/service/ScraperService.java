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

import java.util.List;

@Service
public class ScraperService {

    private final JobRepository jobRepository;
    private final InternshipRepository internshipRepository;
    private final GeminiService geminiService;
    private final Gson gson;

    public ScraperService(JobRepository jobRepository, InternshipRepository internshipRepository, GeminiService geminiService) {
        this.jobRepository = jobRepository;
        this.internshipRepository = internshipRepository;
        this.geminiService = geminiService;
        this.gson = new Gson();
    }

    @Scheduled(fixedRate = 14400000)
    public void runHunter() {
        System.out.println(">>> HUNTER ACTIVATED (Slow Mode): Starting Patrol...");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        // options.addArguments("--headless"); // Uncomment to hide browser

        WebDriver driver = new ChromeDriver(options);

        try {
            huntCategory(driver, "https://freshersrecruitment.co.in/category/jobs/", "JOB");
            huntCategory(driver, "https://freshersrecruitment.co.in/category/internship/", "INTERNSHIP");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
            System.out.println(">>> HUNTER SLEEPING.");
        }
    }

    private void huntCategory(WebDriver driver, String url, String type) {
        driver.get(url);
        try { Thread.sleep(3000); } catch (InterruptedException e) {}

        List<WebElement> articles = driver.findElements(By.cssSelector("h2.entry-title a, h1.entry-title a"));
        System.out.println("Found " + articles.size() + " articles in " + type);

        // Limit to 6 to get a good batch
        List<String> linksToVisit = articles.stream().limit(6).map(a -> a.getAttribute("href")).toList();

        for (String link : linksToVisit) {
            System.out.println("Inspecting: " + link);
            processSinglePage(driver, link, type);

            driver.navigate().back();

            // --- PROACTIVE WAIT (YOUR IDEA) ---
            System.out.println("⏳ Cooldown: Waiting 7s to respect Gemini API limit...");
            try { Thread.sleep(7000); } catch (InterruptedException e) {}
        }
    }

    private void processSinglePage(WebDriver driver, String url, String type) {
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
                } catch (Exception ex) { System.out.println("No Apply link found."); }
            }

            String jsonResponse = geminiService.extractJobDetails(messyText);

            if (type.equals("JOB")) {
                Job job = gson.fromJson(jsonResponse, Job.class);
                if (job == null) job = new Job();

                if (isBad(job.getCompanyName())) job.setCompanyName(extractCompanyFromTitle(pageTitle));
                if (isBad(job.getRole())) job.setRole("Fresher / Trainee");
                if (isBad(job.getEligibility())) job.setEligibility("B.Tech / BCA / MCA / Non-Tech Allowed");
                if (isBad(job.getSalary())) job.setSalary("Best in Industry");
                if (isBad(job.getLocation())) job.setLocation("Pan India / Remote");
                if (isBad(job.getLastDateToApply())) job.setLastDateToApply("ASAP");
                if (isBad(job.getDuration())) job.setDuration("Full Time");

                job.setApplyLink(applyLink);
                jobRepository.save(job);
                System.out.println("✅ SAVED JOB: " + job.getRole());

            } else {
                Internship internship = gson.fromJson(jsonResponse, Internship.class);
                if (internship == null) internship = new Internship();

                if (isBad(internship.getCompanyName())) internship.setCompanyName(extractCompanyFromTitle(pageTitle));
                if (isBad(internship.getRole())) internship.setRole("Intern Trainee");
                if (isBad(internship.getEligibility())) internship.setEligibility("B.Tech / BCA / MCA Students (Non-Tech Allowed)");
                if (isBad(internship.getStipend())) internship.setStipend("Best in Industry / Performance Based");
                if (isBad(internship.getLocation())) internship.setLocation("Remote / Pan India");
                if (isBad(internship.getLastDateToApply())) internship.setLastDateToApply("ASAP");
                if (isBad(internship.getDuration())) internship.setDuration("3-6 Months (Performance Based)");

                internship.setApplyLink(applyLink);
                internshipRepository.save(internship);
                System.out.println("✅ SAVED INTERNSHIP: " + internship.getRole());
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to scrape: " + url + " | " + e.getMessage());
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