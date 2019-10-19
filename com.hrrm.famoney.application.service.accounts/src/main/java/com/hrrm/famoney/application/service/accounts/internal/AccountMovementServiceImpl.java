package com.hrrm.famoney.application.service.accounts.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.hrrm.famoney.application.service.accounts.AccountMovementService;

@Component(service = AccountMovementService.class, scope = ServiceScope.SINGLETON)
public class AccountMovementServiceImpl implements AccountMovementService {

}
