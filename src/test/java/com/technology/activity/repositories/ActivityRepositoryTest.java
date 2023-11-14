package com.technology.activity.repositories;

import com.technology.activity.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityRepositoryTest extends JpaRepository<Activity,Integer> {
    Optional<Activity> findActivityById(Integer id);
}
