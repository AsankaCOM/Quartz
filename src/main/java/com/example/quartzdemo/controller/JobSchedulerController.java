package com.example.quartzdemo.controller;

import com.example.quartzdemo.job.FileUploader;
import com.example.quartzdemo.payload.ScheduleFileSendRequest;
import com.example.quartzdemo.payload.ScheduleFileSendResponse;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@RestController
public class JobSchedulerController {
    private static final Logger logger = LoggerFactory.getLogger(JobSchedulerController.class);

    @Autowired
    private Scheduler scheduler;

    @GetMapping("")
    public String home() {
        return "Hello";
    }

    @PostMapping("/scheduleFileUpload")
    public ResponseEntity<ScheduleFileSendResponse> scheduleFileUpload(@Valid @RequestBody ScheduleFileSendRequest scheduleFileSendRequest) {
        try {
            ZonedDateTime dateTime = ZonedDateTime.of(scheduleFileSendRequest.getDateTime(), scheduleFileSendRequest.getTimeZone());
            if(dateTime.isBefore(ZonedDateTime.now())) {
                ScheduleFileSendResponse scheduleFileSendResponse = new ScheduleFileSendResponse(false,
                        "dateTime must be after current time");
                return ResponseEntity.badRequest().body(scheduleFileSendResponse);
            }

            JobDetail jobDetail = buildJobDetail(scheduleFileSendRequest);
            Trigger trigger = buildJobTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail, trigger);

            ScheduleFileSendResponse scheduleFileSendResponse = new ScheduleFileSendResponse(true,
                    jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "File Upload Scheduled Successfully!");
            return ResponseEntity.ok(scheduleFileSendResponse);
        } catch (SchedulerException ex) {
            logger.error("Error scheduling file upload", ex);

            ScheduleFileSendResponse scheduleFileSendResponse = new ScheduleFileSendResponse(false,
                    "Error scheduling file upload. Please try later!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(scheduleFileSendResponse);
        }
    }

    private JobDetail buildJobDetail(ScheduleFileSendRequest scheduleFileSendRequest) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("email", scheduleFileSendRequest.getEmail());
        jobDataMap.put("subject", scheduleFileSendRequest.getSubject());
        jobDataMap.put("body", scheduleFileSendRequest.getBody());

        return JobBuilder.newJob(FileUploader.class)
                .withIdentity(UUID.randomUUID().toString(), "file-send-jobs")
                .withDescription("Send File Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withDescription("Send File Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}