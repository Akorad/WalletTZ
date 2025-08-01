package org.Akorad.reposetory;

import org.Akorad.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findWalletByWalletId (UUID walletId);
    Optional<Wallet> findByWalletIdAndUserId(UUID walletId, Long userId);
    List<Wallet> findAllByUserId(Long userId);
    Page<Wallet> findAllByUserId(Long userId, Pageable pageable);
}
