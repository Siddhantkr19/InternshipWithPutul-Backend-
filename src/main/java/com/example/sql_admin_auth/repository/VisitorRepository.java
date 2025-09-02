package com.example.sql_admin_auth.repository;


import com.example.sql_admin_auth.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    Optional<Visitor> findFirstByOrderByIdAsc();
}
