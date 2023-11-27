package com.technology.activity.services;

import com.technology.activity.models.Activity;
import com.technology.activity.models.ActivityStatus;
import com.technology.activity.repositories.ActivityRepository;
import com.technology.cart.exceptions.UserNotFoundException;
import com.technology.cart.helpers.CartServiceHelper;
import com.technology.employee.models.Employee;
import com.technology.employee.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public void changActivityStatus(ActivityStatus activityStatus) {
        String employeeEmail = CartServiceHelper.getSecurityUserFromContext()
                .getUser().getEmail();
        activityRepository.findActivityByEmployeeEmail(employeeEmail)
                .ifPresentOrElse(activity -> {
                            activity.setActivityStatus(activityStatus);
                            activityRepository.save(activity);
                        },
                        () -> {
                            Employee employee = employeeRepository.findEmployeeByEmail(employeeEmail)
                                    .orElseThrow(() -> new UserNotFoundException("Employee not found"));
                            Activity activity =
                                    activityRepository.saveAndFlush(
                                            Activity.builder()
                                                    .employee(employee)
                                                    .activityStatus(activityStatus)
                                                    .actualPoints(0)
                                                    .potentialPoints(0)
                                                    .build()
                                    );
                            employee.setEmployeeActivity(activity);
                            employeeRepository.save(employee);
                        });
    }
}
