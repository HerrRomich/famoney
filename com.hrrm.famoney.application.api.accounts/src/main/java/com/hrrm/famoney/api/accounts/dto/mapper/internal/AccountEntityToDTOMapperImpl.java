package com.hrrm.famoney.api.accounts.dto.mapper.internal;

import java.util.function.Supplier;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.hrrm.famoney.api.accounts.dto.AccountDTO;
import com.hrrm.famoney.api.accounts.dto.mapper.AccountEntityToDTOMapper;
import com.hrrm.famoney.domain.accounts.Account;

@Component(service = AccountEntityToDTOMapper.class, scope = ServiceScope.SINGLETON)
public class AccountEntityToDTOMapperImpl implements AccountEntityToDTOMapper {

    @Override
    public AccountDTO toDTO(Account entity) {
        return AccountDTO.builder()
            .id(entity.getId())
            .name(entity.getName())
            .tags(entity.getTags())
            .build();
    }

    @Override
    public AccountDTO toDTO(Account entity, Supplier<AccountDTO> dtoSupplier) {
        return AccountDTO.builderFrom(dtoSupplier.get())
        .id(entity.getId())
        .name(entity.getName())
        .tags(entity.getTags())
        .build();
    }

    @Override
    public Account fromDTO(AccountDTO dto) {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public Account fromDTO(AccountDTO dto, Supplier<Account> entitySupplier) {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

}
