package com.technology.order.config;

import com.technology.user.registration.models.User;
import com.technology.user.registration.repositories.UserRepository;
import com.technology.user.shift.models.Shift;
import com.technology.user.shift.repositories.ShiftRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfiguration {
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;
    private ThreadPoolTaskExecutor executor;

    private List<User> users;
    private int userCounter = 0;

    @Autowired
    public AsyncConfiguration(ShiftRepository shiftRepository, UserRepository userRepository) {
        this.shiftRepository = shiftRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("OrderProcessing");
        executor.setThreadFactory(new CustomThreadFactory());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
    }

    private class CustomThread extends Thread {

        public CustomThread(Runnable runnable) {
            super(runnable);
        }

        private User user;
        private int taskNumber;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public int getTaskNumber() {
            return taskNumber;
        }

        public void setTaskNumber(int taskNumber) {
            this.taskNumber = taskNumber;
        }
    }

    private class CustomThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable runnable) {
            CustomThread thread = new CustomThread(runnable);
            thread.setUser(users.get(userCounter));
            thread.setTaskNumber(0);
            //Thread thread = new Thread(runnable);
            thread.setName("CustomThread-" + userCounter);
            userCounter++;
            return thread;
        }

    }

    @Scheduled(cron = "0 55 * * * ?")
    private void getAllUsersByShift() {
        LocalDateTime now = LocalDateTime.now();
        List<Shift> shifts = shiftRepository.findAll();
        shifts.stream()
                .filter(shift ->
                        Math.abs(ChronoUnit.MINUTES.between(now.plusMinutes(5), shift.getStartTime())) < 2)
                .findAny()
                .ifPresent(shift -> {
                    users = userRepository.findAllUsersByShiftAndRole("STAFF", shift.getStartTime());
                });
    }

    @Scheduled(cron = "0 55 * * * ?")
    public void adjustThreadPool() {
        LocalDateTime now = LocalDateTime.now();
        List<Shift> shifts = shiftRepository.findAll();
        shifts.stream()
                .filter(shift ->
                        Math.abs(ChronoUnit.MINUTES.between(now.plusMinutes(5), shift.getStartTime())) < 2)
                .findAny()
                .ifPresent(shift -> {
                    users = userRepository.findAllUsersByShiftAndRole("STAFF", shift.getStartTime());
                    int staffNumber = users.size();
                    executor.setCorePoolSize(staffNumber);
                    executor.setMaxPoolSize(staffNumber);
                });
    }

    @Bean(name = "threadPoolTaskExecutor")
    public Executor asyncExecutor() {
        return executor;
    }
}
