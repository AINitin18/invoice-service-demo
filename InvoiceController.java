package com.example.invoice.api;

import com.example.invoice.api.dto.CreateInvoiceRequest;
import com.example.invoice.api.dto.UpdateStatusRequest;
import com.example.invoice.model.Invoice;
import com.example.invoice.model.InvoiceStatus;
import com.example.invoice.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {
    private final InvoiceService service;

    public InvoiceController(InvoiceService service) { this.service = service; }

    // REQ-INV-001: Create Invoice endpoint
    @PostMapping
    public ResponseEntity<?> create(@Validated @RequestBody CreateInvoiceRequest req) {
        try {
            Invoice inv = service.create(req.getCustomerName(), req.getAmount());
            return ResponseEntity.status(HttpStatus.CREATED).body(inv);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // REQ-INV-002: Get Invoice by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Invoice inv = service.get(id);
        return (inv == null) ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found") : ResponseEntity.ok(inv);
    }

    // REQ-INV-003: List & Filter Invoices
    @GetMapping
    public ResponseEntity<List<Invoice>> list(@RequestParam(required = false) InvoiceStatus status,
                                              @RequestParam(required = false, name = "customer") String customerName) {
        return ResponseEntity.ok(service.list(status, customerName));
    }

    // REQ-INV-004: Update status
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @Validated @RequestBody UpdateStatusRequest req) {
        InvoiceStatus status;
        try {
            status = InvoiceStatus.valueOf(req.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status");
        }
        Invoice inv = service.updateStatus(id, status);
        return (inv == null) ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found") : ResponseEntity.ok(inv);
    }
}
