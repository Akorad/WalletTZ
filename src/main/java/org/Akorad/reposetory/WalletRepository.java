package org.Akorad.reposetory;

import jakarta.persistence.LockModeType;
import org.Akorad.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findWalletByWalletId (UUID walletId);
    Optional<Wallet> findByWalletIdAndUserId(UUID walletId, Long userId);
    List<Wallet> findAllByUserId(Long userId);
    Page<Wallet> findAllByUserId(Long userId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.walletId = :walletId")
    Optional<Wallet> findWalletByWalletIdWithLock(@Param("walletId") UUID walletId);

    @Query("SELECT w FROM Wallet w WHERE w.walletId = :walletId")
    Optional<Wallet> findByWalletId(@Param("walletId") UUID walletId);
}
