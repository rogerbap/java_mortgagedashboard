package com.lender.mortgage.service.impl;

import com.lender.mortgage.dto.request.CreateConditionRequest;
import com.lender.mortgage.dto.request.UpdateConditionRequest;
import com.lender.mortgage.dto.response.ConditionResponse;
import com.lender.mortgage.entity.Loan;
import com.lender.mortgage.entity.LoanCondition;
import com.lender.mortgage.entity.User;
import com.lender.mortgage.entity.enums.ConditionStatus;
import com.lender.mortgage.entity.enums.ConditionType;
import com.lender.mortgage.entity.enums.Priority;
import com.lender.mortgage.exception.BadRequestException;
import com.lender.mortgage.exception.ResourceNotFoundException;
import com.lender.mortgage.repository.LoanConditionRepository;
import com.lender.mortgage.service.ConditionService;
import com.lender.mortgage.service.LoanService;
import com.lender.mortgage.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConditionServiceImpl implements ConditionService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConditionServiceImpl.class);
    
    @Autowired
    private LoanConditionRepository conditionRepository;
    
    @Autowired
    private LoanService loanService;
    
    @Autowired
    private UserService userService;
    
    @Override
    public ConditionResponse createCondition(CreateConditionRequest request, String createdByEmail) {
        Loan loan = loanService.getLoanEntity(request.getLoanId());
        
        LoanCondition condition = new LoanCondition();
        condition.setLoan(loan);
        condition.setType(request.getType());
        condition.setTitle(request.getTitle());
        condition.setDescription(request.getDescription());
        condition.setPriority(request.getPriority());
        condition.setStatus(ConditionStatus.PENDING);
       if (request.getDueDate() != null) {
    condition.setDueDate(request.getDueDate().atStartOfDay());
}
        condition.setComments(request.getComments());
        condition.setCreatedBy(createdByEmail);
        
        if (request.getAssignedToId() != null) {
            User assignedTo = userService.getUserEntity(request.getAssignedToId());
            condition.setAssignedTo(assignedTo);
        }
        
        LoanCondition savedCondition = conditionRepository.save(condition);
        
        logger.info("Created condition {} for loan {}", savedCondition.getTitle(), loan.getLoanNumber());
        
        return new ConditionResponse(savedCondition);
    }
    
    @Override
    public ConditionResponse updateCondition(Long conditionId, UpdateConditionRequest request, String updatedByEmail) {
        LoanCondition condition = getConditionEntity(conditionId);
        
        if (request.getTitle() != null) {
            condition.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            condition.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            condition.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            condition.setDueDate(request.getDueDate());
        }
        if (request.getComments() != null) {
            condition.setComments(request.getComments());
        }
        if (request.getInternalNotes() != null) {
            condition.setInternalNotes(request.getInternalNotes());
        }
        if (request.getAssignedToId() != null) {
            User assignedTo = userService.getUserEntity(request.getAssignedToId());
            condition.setAssignedTo(assignedTo);
        }
        
        condition.setLastModifiedBy(updatedByEmail);
        
        LoanCondition savedCondition = conditionRepository.save(condition);
        
        logger.info("Updated condition {}", savedCondition.getTitle());
        
        return new ConditionResponse(savedCondition);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ConditionResponse getConditionById(Long conditionId) {
        LoanCondition condition = getConditionEntity(conditionId);
        return new ConditionResponse(condition);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ConditionResponse> getConditionsByLoan(Long loanId) {
        Loan loan = loanService.getLoanEntity(loanId);
        return conditionRepository.findByLoan(loan)
                .stream()
                .map(ConditionResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ConditionResponse> getConditionsByUser(Long userId, Pageable pageable) {
        User user = userService.getUserEntity(userId);
        return conditionRepository.findByAssignedTo(user, pageable)
                .map(ConditionResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ConditionResponse> getActiveConditionsByUser(Long userId, Pageable pageable) {
        User user = userService.getUserEntity(userId);
        return conditionRepository.findActiveConditionsByUser(user, pageable)
                .map(ConditionResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ConditionResponse> getConditionsByStatus(ConditionStatus status, Pageable pageable) {
        return conditionRepository.findByStatus(status, pageable)
                .map(ConditionResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ConditionResponse> getActiveConditions(Pageable pageable) {
        return conditionRepository.findActiveConditions(pageable)
                .map(ConditionResponse::new);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ConditionResponse> getOverdueConditions() {
        return conditionRepository.findOverdueConditions(LocalDateTime.now())
                .stream()
                .map(ConditionResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ConditionResponse> getConditionsDueSoon(int daysAhead) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(daysAhead);
        
        return conditionRepository.findConditionsDueSoon(startDate, endDate)
                .stream()
                .map(ConditionResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ConditionResponse> getHighPriorityActiveConditions() {
        return conditionRepository.findHighPriorityActiveConditions()
                .stream()
                .map(ConditionResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public ConditionResponse completeCondition(Long conditionId, String completedByEmail, String notes) {
        LoanCondition condition = getConditionEntity(conditionId);
        
        if (!condition.canBeCompleted()) {
            throw new BadRequestException("Condition cannot be completed in current status: " + condition.getStatus());
        }
        
        condition.markCompleted(completedByEmail);
        if (notes != null && !notes.trim().isEmpty()) {
            String existingComments = condition.getComments();
            String newComments = existingComments != null ? 
                existingComments + "\n" + notes : notes;
            condition.setComments(newComments);
        }
        
        LoanCondition savedCondition = conditionRepository.save(condition);
        
        logger.info("Completed condition {} for loan {}", 
                   savedCondition.getTitle(), savedCondition.getLoan().getLoanNumber());
        
        return new ConditionResponse(savedCondition);
    }
    
    @Override
    public ConditionResponse waiveCondition(Long conditionId, String waivedByEmail, String reason) {
        LoanCondition condition = getConditionEntity(conditionId);
        
        if (!condition.canBeCompleted()) {
            throw new BadRequestException("Condition cannot be waived in current status: " + condition.getStatus());
        }
        
        condition.markWaived(waivedByEmail, reason);
        
        LoanCondition savedCondition = conditionRepository.save(condition);
        
        logger.info("Waived condition {} for loan {} - Reason: {}", 
                   savedCondition.getTitle(), savedCondition.getLoan().getLoanNumber(), reason);
        
        return new ConditionResponse(savedCondition);
    }
    
    @Override
    public ConditionResponse assignCondition(Long conditionId, Long userId, String assignedByEmail) {
        LoanCondition condition = getConditionEntity(conditionId);
        User assignedTo = userService.getUserEntity(userId);
        
        condition.setAssignedTo(assignedTo);
        condition.setLastModifiedBy(assignedByEmail);
        
        LoanCondition savedCondition = conditionRepository.save(condition);
        
        logger.info("Assigned condition {} to user {}", 
                   savedCondition.getTitle(), assignedTo.getFullName());
        
        return new ConditionResponse(savedCondition);
    }
    
    @Override
    public ConditionResponse updateConditionPriority(Long conditionId, Priority priority, String updatedByEmail) {
        LoanCondition condition = getConditionEntity(conditionId);
        
        condition.setPriority(priority);
        condition.setLastModifiedBy(updatedByEmail);
        
        LoanCondition savedCondition = conditionRepository.save(condition);
        
        logger.info("Updated condition {} priority to {}", 
                   savedCondition.getTitle(), priority);
        
        return new ConditionResponse(savedCondition);
    }
    
    @Override
    public void deleteCondition(Long conditionId, String deletedByEmail) {
        LoanCondition condition = getConditionEntity(conditionId);
        
        if (condition.getStatus() == ConditionStatus.COMPLETED) {
            throw new BadRequestException("Cannot delete completed condition");
        }
        
        conditionRepository.delete(condition);
        
        logger.info("Deleted condition {} by user {}", condition.getTitle(), deletedByEmail);
    }
    
    @Override
    @Transactional(readOnly = true)
    public LoanCondition getConditionEntity(Long conditionId) {
        return conditionRepository.findById(conditionId)
                .orElseThrow(() -> new ResourceNotFoundException("Condition not found with id: " + conditionId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean areAllConditionsSatisfied(Long loanId) {
        Loan loan = loanService.getLoanEntity(loanId);
        long activeConditions = conditionRepository.countActiveConditionsByLoan(loan);
        return activeConditions == 0;
    }
}
