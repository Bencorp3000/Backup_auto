package com.bencorp.backup.auto.repository;

import com.bencorp.backup.auto.entity.BackupExecution;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupExecutionRepository extends JpaRepository<BackupExecution, Long> {

    List<BackupExecution> findAllByOrderByStartedAtDesc();
}
