package com.pillsolo.api.repository;

import com.pillsolo.api.domain.Pill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PillRepository extends JpaRepository<Pill, Long> {
    List<Pill> findByNameContainingIgnoreCase(String name);
}