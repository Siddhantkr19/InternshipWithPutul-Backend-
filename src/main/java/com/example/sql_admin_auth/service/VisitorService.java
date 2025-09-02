package com.example.sql_admin_auth.service;

import com.example.sql_admin_auth.entity.Visitor;
import com.example.sql_admin_auth.repository.VisitorRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VisitorService {

    private final VisitorRepository visitorRepository;

    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    // A helper method to find or create the counter
    private Visitor findOrCreateVisitor() {
        return visitorRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    Visitor newVisitor = new Visitor();
                    newVisitor.setCount(0L);
                    return visitorRepository.save(newVisitor);
                });
    }

    @Transactional
    public Visitor getVisitorCount() {
        return findOrCreateVisitor();
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE) // This is the key to preventing race conditions
    public Visitor incrementAndGetCount() {
        Visitor visitor = findOrCreateVisitor();
        visitor.setCount(visitor.getCount() + 1);
        return visitorRepository.save(visitor);
    }
}