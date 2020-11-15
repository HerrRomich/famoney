package com.hrrm.famoney.api.accounts.internal;

import java.math.BigDecimal;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;

import com.hrrm.famoney.api.accounts.dto.EntryDataDTO;
import com.hrrm.famoney.domain.accounts.movement.Entry;
import com.hrrm.famoney.domain.accounts.movement.EntryItem;
import com.hrrm.famoney.domain.accounts.movement.Movement;

@Component(service = EntryApiService.class)
public class EntryApiService {

    public Movement updateMovement(Entry entry, EntryDataDTO entryDataDTO) {
        return fillEntryAttributes(entryDataDTO, () -> entry);
    }

    public Movement createMovement(EntryDataDTO entryDataDTO) {
        return fillEntryAttributes(entryDataDTO, Entry::new);
    }

    private Movement fillEntryAttributes(final EntryDataDTO entryDataDTO,
            final Supplier<Entry> entrySupplier) {
        final var entry = entrySupplier.get();
        return entry.setEntryItems(entryDataDTO.getEntryItems()
            .stream()
            .map(entryItemDataDTO -> new EntryItem().setCategoryId(entryItemDataDTO.getCategoryId())
                .setAmount(entryItemDataDTO.getAmount())
                .setComments(entryItemDataDTO.getComments()))
            .collect(Collectors.toList()))
            .setAmount(entryDataDTO.getAmount())
            .setTotal(BigDecimal.ZERO);
    }

}
