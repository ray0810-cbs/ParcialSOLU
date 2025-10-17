package com.example.parcialdbp.repositorios;

import com.example.parcialdbp.clases.Loan;
import com.example.parcialdbp.clases.Status;
import com.example.parcialdbp.clases.UserClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan,Long> {
    boolean existsByUserAndStatus(UserClass user, Status status);
}
