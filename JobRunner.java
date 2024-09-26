package ooad;

import lombok.Getter;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public  class JobRunner {
    enum Priority {
        HIGH(1), MEDIUM(2), LOW(3);

        private final int key;

        Priority(int key) {
            this.key = key;
        }

        public int getKey() {
            return key;
        }
    }
    @Getter
    abstract static class Job {
        String name;
        Priority priority;
        public Job(String name, Priority priority) {
            this.name = name;
            this.priority = priority;
        }

        public abstract void execute();
    }

    interface Schedule {
        boolean shouldRun(LocalDateTime localDateTime);
    }




    static class EmailJob extends Job {


        public EmailJob(String name,Priority priority) {
            super(name,priority);
        }

        @Override
        public void execute() {
            System.out.println("Running Email Job");
        }
    }
    static class BackupJob extends Job {


        public BackupJob(String name,Priority priority) {
            super(name,priority);
        }

        @Override
        public void execute() {
            System.out.println("Running BackupJob Job");
        }
    }
    // Once Daily
    @Getter
    static class DailySchedule implements Schedule {
        // We can also add description and other metadata
        Integer hour;

        public DailySchedule(Integer hour) {
            this.hour = hour;
        }

        @Override
        public boolean shouldRun(LocalDateTime localDateTime) {

            return localDateTime.getHour() == this.hour;
        }
    }
    @Getter
    static class WeeklySchedule implements Schedule {
        List<DayOfWeek> dayOfWeeks;

        public WeeklySchedule() {
            this.dayOfWeeks = new ArrayList<>();
        }

        @Override
        public boolean shouldRun(LocalDateTime localDateTime) {

            return this.dayOfWeeks.contains(localDateTime.getDayOfWeek());

        }

        public void addDayToSchedule(DayOfWeek dayOfWeek) {
            this.dayOfWeeks.add(dayOfWeek);
        }
    }

    @Getter
    static class HourlySchedule implements Schedule {

        @Override
        public boolean shouldRun(LocalDateTime localDateTime) {

            return localDateTime.getMinute() == 0;

        }


    }
    @Getter
    static class CustomSchedule implements Schedule {
        List<LocalDateTime> localDateTimes;

        public CustomSchedule() {
            this.localDateTimes = new ArrayList<>();
        }

        @Override
        public boolean shouldRun(LocalDateTime localDateTime) {
            return localDateTimes.contains(localDateTime);
        }
        public void addCustomSchedule(LocalDateTime customTime) {
            localDateTimes.add(customTime);

        }
    }
    @Getter
    static class ScheduledJob {
        Job job;
        Schedule schedule;

        public ScheduledJob(Job job, Schedule schedule) {
            this.job = job;
            this.schedule = schedule;
        }
    }
    @Getter
    static class JobScheduler {
       // List<ScheduledJob> jobs;
          Queue<ScheduledJob> jobs;
        public JobScheduler() {
            //this.jobs = new ArrayList<>();
            this.jobs = new PriorityQueue<>((job1,job2) -> job2.getJob().getPriority().getKey() - job1.getJob().getPriority().getKey());
        }

        public void addScheduledJob(ScheduledJob job) {
            this.jobs.add(job);
        }
    }

    static class JobManager {
        public static JobManager INSTANCE = new JobManager();

        private JobManager() {
        }

        private void runJobs(JobScheduler jobScheduler) {
            LocalDateTime localDateTime = LocalDateTime.now();

            for(ScheduledJob scheduledJob : jobScheduler.getJobs()) {
                if(scheduledJob.getSchedule().shouldRun(localDateTime)) {
                    scheduledJob.getJob().execute();
                }
            }
        }

        public void start(JobScheduler jobScheduler) throws InterruptedException {
            while(true) {
                runJobs(jobScheduler);
                Thread.sleep(Duration.ofMinutes(1).toSeconds());
            }
        }


    }

}
