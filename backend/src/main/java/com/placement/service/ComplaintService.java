package com.placement.service;

import com.placement.dto.ComplaintDTO;
import com.placement.entity.Complaint;
import com.placement.entity.ComplaintStatus;
import com.placement.entity.User;
import com.placement.repository.ComplaintRepository;
import com.placement.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public ComplaintService(ComplaintRepository complaintRepository, UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ComplaintDTO.ComplaintResponse submitComplaint(Long studentUserId, ComplaintDTO.ComplaintRequest request) {
        User student = userRepository.findById(studentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Student user not found"));

        Complaint complaint = new Complaint();
        complaint.setStudent(student);
        complaint.setTitle(request.getTitle());
        complaint.setCategory(request.getCategory());
        complaint.setDescription(request.getDescription());
        complaint.setPriority(request.getPriority());
        complaint.setStatus(ComplaintStatus.SUBMITTED);

        Complaint saved = complaintRepository.save(complaint);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ComplaintDTO.ComplaintResponse> getStudentComplaints(Long studentUserId) {
        return complaintRepository.findByStudentIdOrderByCreatedAtDesc(studentUserId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ComplaintDTO.ComplaintResponse> getAllComplaints(ComplaintStatus status, Pageable pageable) {
        Page<Complaint> page;
        if (status != null) {
            page = complaintRepository.findByStatus(status, pageable);
        } else {
            page = complaintRepository.findAll(pageable);
        }
        return page.map(this::toDto);
    }

    @Transactional
    public ComplaintDTO.ComplaintResponse updateComplaintStatus(Long complaintId, ComplaintDTO.StatusUpdateRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found with ID: " + complaintId));

        complaint.setStatus(request.getStatus());
        if (request.getAdminResponse() != null) {
            complaint.setAdminResponse(request.getAdminResponse());
        }

        Complaint updated = complaintRepository.save(complaint);
        return toDto(updated);
    }

    private ComplaintDTO.ComplaintResponse toDto(Complaint c) {
        ComplaintDTO.ComplaintResponse dto = new ComplaintDTO.ComplaintResponse();
        dto.setId(c.getId());
        dto.setStudentId(c.getStudent().getId());
        dto.setStudentName(c.getStudent().getFullName());
        dto.setEmail(c.getStudent().getEmail());
        dto.setTitle(c.getTitle());
        dto.setCategory(c.getCategory());
        dto.setDescription(c.getDescription());
        dto.setPriority(c.getPriority());
        dto.setStatus(c.getStatus());
        dto.setAdminResponse(c.getAdminResponse());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        return dto;
    }
}
