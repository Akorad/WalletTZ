package org.Akorad.dto.transaction;

import org.Akorad.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "walletId", expression = "java(transaction.getWallet().getWalletId())")
    TransactionDto toDto(Transaction transaction);
}
