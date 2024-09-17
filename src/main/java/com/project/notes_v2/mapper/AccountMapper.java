package com.project.notes_v2.mapper;

import com.project.notes_v2.dto.AccountRequestDTO;
import com.project.notes_v2.dto.AccountResponseDTO;
import com.project.notes_v2.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account toAccount(AccountRequestDTO accountRequestDTO);

    AccountResponseDTO toAccountResponseDTO(Account account);

    void updateAccountFromDTO(AccountRequestDTO accountRequestDTO, @MappingTarget Account account);
}
