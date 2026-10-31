package com.bencorp.backup.auto.controller;

import com.bencorp.backup.auto.entity.BackupExecution;
import com.bencorp.backup.auto.repository.BackupExecutionRepository;
import com.bencorp.backup.auto.service.BackupService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class BackupController {

    private final BackupService backupService;
    private final BackupExecutionRepository repository;

    public BackupController(BackupService backupService, BackupExecutionRepository repository) {
        this.backupService = backupService;
        this.repository = repository;
    }

    @GetMapping
    public String index(Model model) {
        List<BackupExecution> executions = repository.findAllByOrderByStartedAtDesc();
        model.addAttribute("executions", executions);
        return "index";
    }

    @PostMapping("/backup/run")
    public String triggerManualBackup() {
        backupService.triggerManualBackup();
        return "redirect:/";
    }
}
