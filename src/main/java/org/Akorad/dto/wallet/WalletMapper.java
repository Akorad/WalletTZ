package org.Akorad.dto.wallet;

import org.Akorad.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mapping(target = "ownerName", expression = "java(wallet.getUser().getFirstName() + \" \" + wallet.getUser().getLastName())")
    WalletResponseDTO toDto(Wallet wallet);

    WalletDto toWalletDto(Wallet wallet);
}
