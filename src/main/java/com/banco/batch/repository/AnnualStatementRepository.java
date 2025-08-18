package com.banco.batch.repository;

import com.banco.batch.model.AnnualStatement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnualStatementRepository extends JpaRepository<AnnualStatement, Long> { }
