package com.finance;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TransactionController {
    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping("/transactions")
    public List<Transaction> transactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) String category) {
        return service.list(start, end, category);
    }

    @PostMapping("/transactions")
    public ResponseEntity<?> add(@RequestBody Transaction transaction) throws IOException {
        try {
            return ResponseEntity.ok(service.add(transaction));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/summary")
    public Map<String, Object> summary(@RequestParam(required = false) String month) {
        YearMonth selected = month == null || month.isBlank() ? YearMonth.now() : YearMonth.parse(month);
        return service.summary(selected);
    }

    @GetMapping("/export/transactions")
    public ResponseEntity<byte[]> exportTransactions() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions_export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(service.exportTransactions());
    }

    @GetMapping("/export/summary")
    public ResponseEntity<byte[]> exportSummary(@RequestParam(required = false) String month) {
        YearMonth selected = month == null || month.isBlank() ? YearMonth.now() : YearMonth.parse(month);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monthly_summary.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(service.exportSummary(selected));
    }
}
