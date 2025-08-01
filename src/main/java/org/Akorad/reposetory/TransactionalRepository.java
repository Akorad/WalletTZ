package org.Akorad.reposetory;

import org.Akorad.entity.Transaction;
import org.Akorad.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionalRepository extends JpaRepository<Transaction,Long> {
    Page<Transaction> findAllByWallet(Wallet wallet, Pageable pageable);
}
