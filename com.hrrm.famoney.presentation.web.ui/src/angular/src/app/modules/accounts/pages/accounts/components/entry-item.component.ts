import { Component } from '@angular/core';

@Component({
  selector: 'app-entry-item',
  templateUrl: 'entry-item.component.html',
  styleUrls: ['entry-item.component.scss']
})
export class EntryItemComponent {
  entryCategories = ['Категория1', 'Категория2'];
}
