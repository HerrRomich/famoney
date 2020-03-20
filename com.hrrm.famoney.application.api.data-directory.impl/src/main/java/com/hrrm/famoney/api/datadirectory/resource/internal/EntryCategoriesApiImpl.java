package com.hrrm.famoney.api.datadirectory.resource.internal;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
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
import com.hrrm.famoney.application.api.datadirectory.dto.EntryCategoryDTO;
import com.hrrm.famoney.application.api.datadirectory.dto.EntryCategoryDTOBuilder;
import com.hrrm.famoney.application.api.datadirectory.dto.EntryCategoryDataDTO;
import com.hrrm.famoney.application.api.datadirectory.dto.ExpenseCategoryDTO;
import com.hrrm.famoney.application.api.datadirectory.dto.IncomeCategoryDTO;
import com.hrrm.famoney.application.api.datadirectory.dto.impl.EntryCategoriesDTOImpl;
import com.hrrm.famoney.application.api.datadirectory.resources.EntryCategoriesApi;
import com.hrrm.famoney.domain.datadirectory.EntryCategory;
import com.hrrm.famoney.domain.datadirectory.ExpenseCategory;
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
        return EntryCategoriesDTOImpl.builder()
            .expenses(this.<ExpenseCategory, ExpenseCategoryDTO>mapEntryCategoriesToDTO(expenseCategoryRepository
                .getTopLevelCategories(),
                    ExpenseCategoryDTO.Builder::new))
            .incomes(mapEntryCategoriesToDTO(incomeCategoryRepository.getTopLevelCategories(),
                    IncomeCategoryDTO.Builder::new))
            .build();
    }

    private <T extends EntryCategory<T>, P extends EntryCategoryDTO<P>> Iterable<P> mapEntryCategoriesToDTO(
            final List<T> entryCategories, Supplier<? extends EntryCategoryDTOBuilder<P>> entityDTOBuilderSupplier) {
        return entryCategories.stream()
            .map(entryCategory -> entityDTOBuilderSupplier.get()
                .id(entryCategory.getId())
                .name(entryCategory.getName())
                .addAllChildren(mapEntryCategoriesToDTO(entryCategory.getChildren(),
                        entityDTOBuilderSupplier))
                .build())
            .sorted(Comparator.comparing(P::getName))
            .collect(Collectors.toList());
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
