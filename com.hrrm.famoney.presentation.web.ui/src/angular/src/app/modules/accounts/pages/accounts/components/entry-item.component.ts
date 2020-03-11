import { Component } from '@angular/core';
import { EntryCategoryService } from '@famoney-shared/services/entry-category.service';

@Component({
  selector: 'app-entry-item',
  templateUrl: 'entry-item.component.html',
  styleUrls: ['entry-item.component.scss'],
})
export class EntryItemComponent {
  constructor(public entryCategoriesService: EntryCategoryService) {}
}
