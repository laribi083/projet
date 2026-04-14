package com.votredomaine.modelememoire.repository;

import com.votredomaine.modelememoire.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    List<Activity> findTop5ByOrderByCreatedAtDesc();
    
    List<Activity> findTop10ByOrderByCreatedAtDesc();
}