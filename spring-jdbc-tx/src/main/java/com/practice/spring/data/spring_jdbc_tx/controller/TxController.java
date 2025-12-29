package com.practice.spring.data.spring_jdbc_tx.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.spring.data.spring_jdbc_tx.service.TxService;

@RestController
public class TxController {

    private final TxService txService;

    public TxController(TxService txService) {
        this.txService = txService;
    }

    // ---------- PROPAGATION ----------

    @GetMapping("/tx/required")
    public String required() {
        txService.requiredExample();
        return "REQUIRED done";
    }

    @GetMapping("/tx/requires-new")
    public String requiresNew() {
        txService.requiresNewExample();
        return "REQUIRES_NEW done";
    }

    @GetMapping("/tx/nested")
    public String nested() {
        txService.nestedExample();
        return "NESTED done";
    }

    @GetMapping("/tx/supports")
    public String supports() {
        txService.supportsExample();
        return "SUPPORTS done";
    }

    @GetMapping("/tx/not-supported")
    public String notSupported() {
        txService.notSupportedExample();
        return "NOT_SUPPORTED done";
    }

    @GetMapping("/tx/mandatory")
    public String mandatory() {
        txService.mandatoryExample();
        return "MANDATORY done";
    }

    @GetMapping("/tx/never")
    public String never() {
        txService.neverExample();
        return "NEVER done";
    }

    // ---------- ISOLATION ----------

    @GetMapping("/tx/isolation/read-committed")
    public String readCommitted() {
        txService.readCommitted();
        return "READ_COMMITTED";
    }

    @GetMapping("/tx/isolation/repeatable-read")
    public String repeatableRead() {
        txService.repeatableRead();
        return "REPEATABLE_READ";
    }

    @GetMapping("/tx/isolation/serializable")
    public String serializable() {
        txService.serializable();
        return "SERIALIZABLE";
    }
}

