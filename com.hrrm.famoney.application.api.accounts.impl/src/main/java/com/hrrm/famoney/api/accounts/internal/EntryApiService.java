package com.hrrm.famoney.api.accounts.internal;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Component;

import com.hrrm.famoney.api.accounts.dto.EntryDataDTO;
import com.hrrm.famoney.api.accounts.dto.EntryItemDataDTO;
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

    private Movement fillEntryAttributes(final EntryDataDTO entryDataDTO, final Supplier<Entry> entrySupplier) {
        final var entry = entrySupplier.get();
        final var entryItems = entry.getEntryItems();
        var i = 0;
        List<EntryItemDataDTO> entryItemDTOs = entryDataDTO.getEntryItems();
        while(i < entryItemDTOs.size()) {
            EntryItem entryItem;
            if (i < entryItems.size()) {
                entryItem = entryItems.get(i);
            } else {
                entryItem = new EntryItem().setPosition(i);
                entryItems.add(entryItem);
            }
            final var entryItemDTO = entryItemDTOs.get(i);
            entryItem.setCategoryId(entryItemDTO.getCategoryId())
            .setAmount(entryItemDTO.getAmount())
            .setComments(entryItemDTO.getComments().orElse(null));
            i++;
        }
        while(i < entryItems.size()) {
            entryItems.remove(entryItems.size() - 1);
        }
        return entry.setAmount(entryDataDTO.getAmount())
            .setTotal(BigDecimal.ZERO);
    }

}
