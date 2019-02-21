package com.hrrm.famoney.api.accounts.dto.mapper;

import com.hrrm.famoney.api.accounts.dto.AccountDTO;
import com.hrrm.famoney.domain.accounts.Account;
import com.hrrm.famoney.infrastructure.jaxrs.mapper.EntityToDTOMapper;

public interface AccountEntityToDTOMapper extends EntityToDTOMapper<Account, AccountDTO> {

}
