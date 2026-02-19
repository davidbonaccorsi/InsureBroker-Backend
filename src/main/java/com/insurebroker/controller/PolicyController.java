package com.insurebroker.controller;

import com.insurebroker.dto.request.PolicyRequest;
import com.insurebroker.dto.response.PolicyResponse;
import com.insurebroker.security.UserPrincipal;
import com.insurebroker.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;
    private final com.insurebroker.repository.PolicyRepository policyRepository;
    private final com.insurebroker.service.PdfGeneratorService pdfGeneratorService;


    @PostMapping
    public ResponseEntity<PolicyResponse> createPolicy(
            @Valid @RequestBody PolicyRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PolicyResponse response = policyService.createPolicy(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PolicyResponse>> getPolicies(
            @RequestParam(defaultValue = "false") boolean showAll,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<PolicyResponse> responses = policyService.getPolicies(currentUser, showAll);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PolicyResponse> cancelPolicy(
            @PathVariable Long id,
            @Valid @RequestBody com.insurebroker.dto.request.CancelPolicyRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PolicyResponse response = policyService.cancelPolicy(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PolicyResponse> updatePolicy(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> updates,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PolicyResponse response = policyService.updatePolicyStatus(id, updates, currentUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{id}/upload-proof", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PolicyResponse> uploadPaymentProof(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        PolicyResponse response = policyService.uploadPaymentProof(id, file, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/download-proof")
    public ResponseEntity<org.springframework.core.io.Resource> downloadProof(@PathVariable Long id) {
        return policyService.downloadPaymentProof(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<org.springframework.core.io.Resource> downloadPolicyPdf(@PathVariable Long id) {

        com.insurebroker.entity.InsurancePolicy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        ByteArrayInputStream pdfStream = pdfGeneratorService.generatePolicyPdf(policy);

        org.springframework.core.io.InputStreamResource resource = new org.springframework.core.io.InputStreamResource(pdfStream);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Policy_" + policy.getPolicyNumber() + ".pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(resource);
    }
}