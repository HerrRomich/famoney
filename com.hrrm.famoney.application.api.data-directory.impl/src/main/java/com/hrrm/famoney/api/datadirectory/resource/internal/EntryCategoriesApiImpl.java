package com.hrrm.famoney.api.datadirectory.resource.internal;

import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.hrrm.famoney.application.api.datadirectory.dto.EntryCategoriesDTO;
import com.hrrm.famoney.application.api.datadirectory.dto.EntryCategoryDataDTO;
import com.hrrm.famoney.application.api.datadirectory.dto.ExpenseCategoryDTO;
import com.hrrm.famoney.application.api.datadirectory.dto.IncomeCategoryDTO;
import com.hrrm.famoney.application.api.datadirectory.dto.impl.EntryCategoriesDTOImpl;
import com.hrrm.famoney.application.api.datadirectory.dto.impl.ExpenseCategoryDTOImpl;
import com.hrrm.famoney.application.api.datadirectory.dto.impl.IncomeCategoryDTOImpl;
import com.hrrm.famoney.application.api.datadirectory.resources.EntryCategoriesApi;
import com.hrrm.famoney.domain.datadirectory.ExpenseCategory;
import com.hrrm.famoney.domain.datadirectory.IncomeCategory;
import com.hrrm.famoney.domain.datadirectory.repository.ExpenseCategoryRepository;
import com.hrrm.famoney.domain.datadirectory.repository.IncomeCategoryRepository;

import io.swagger.v3.oas.annotations.Hidden;

@Component(service = EntryCategoriesApi.class, scope = ServiceScope.BUNDLE)
@JaxrsResource
@JaxrsApplicationSelect("(osgi.jaxrs.name=com.hrrm.famoney.application.api.data-dictionary)")
@Hidden
public class EntryCategoriesApiImpl implements EntryCategoriesApi {

    private final Logger logger;
    private final IncomeCategoryRepository incomeCategoryRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    @Activate
    public EntryCategoriesApiImpl(@Reference(service = LoggerFactory.class) final Logger logger,
            @Reference final IncomeCategoryRepository incomeCategoryRepository,
            @Reference final ExpenseCategoryRepository expenseCategoryRepository) {
        super();
        this.logger = logger;
        this.incomeCategoryRepository = incomeCategoryRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    @Override
    public EntryCategoriesDTO getEntryCategories() {
        final List<ExpenseCategoryDTO> expenses = expenseCategoryRepository.getTopLevelCategories()
            .stream()
            .map(this::mapExpenseCategoryToDTO)
            .collect(Collectors.toList());
        final List<IncomeCategoryDTO> incomes = incomeCategoryRepository.getTopLevelCategories()
            .stream()
            .map(this::mapIncomeCategoryToDTO)
            .collect(Collectors.toList());
        return EntryCategoriesDTOImpl.builder()
            .expenses(expenses)
            .incomes(incomes)
            .build();
    }

    private ExpenseCategoryDTO mapExpenseCategoryToDTO(final ExpenseCategory expenseCategory) {
        return ExpenseCategoryDTOImpl.builder()
            .id(expenseCategory.getId())
            .name(expenseCategory.getName())
            .addAllChildren(expenseCategory.getChildren()
                .stream()
                .map(this::mapExpenseCategoryToDTO)
                .collect(Collectors.toList()))
            .build();

    }

    private IncomeCategoryDTO mapIncomeCategoryToDTO(final IncomeCategory incomeCategory) {
        return IncomeCategoryDTOImpl.builder()
            .id(incomeCategory.getId())
            .name(incomeCategory.getName())
            .addAllChildren(incomeCategory.getChildren()
                .stream()
                .map(this::mapIncomeCategoryToDTO)
                .collect(Collectors.toList()))
            .build();

    }

    @Override
    public void addEntryCategory(final EntryCategoryDataDTO entryCategory) {
        // TODO Auto-generated method stub
        //
        throw new UnsupportedOperationException();
    }

    @Override
    public EntryCategoryDataDTO changeEntryCategory(final Integer id, final EntryCategoryDataDTO entryCategory) {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteEntryCategory(final Integer id) {
        // TODO Auto-generated method stub
        //
        throw new UnsupportedOperationException();
    }

}
