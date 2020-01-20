import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { AccountsService } from '../accounts.service';
import { Observable, combineLatest } from 'rxjs';
import { FormControl } from '@angular/forms';
import { MatChipInputEvent, MatAutocomplete, MatAutocompleteSelectedEvent } from '@angular/material';
import { map, startWith } from 'rxjs/operators';

@Component({
  selector: 'app-account-tags-popup',
  templateUrl: 'account-tags-popup.component.html',
  styleUrls: ['account-tags-popup.component.scss']
})
export class AccountTagsPopupComponent implements OnInit {
  separatorKeysCodes: number[] = [ENTER, COMMA];
  public accountTags: Observable<string[]>;
  @ViewChild('tagsInput', { static: true }) tagsInput: ElementRef<HTMLInputElement>;
  tagsCtrl = new FormControl();
  @ViewChild('tagAutoComplete', { static: true }) matAutocomplete: MatAutocomplete;

  constructor(public accountsService: AccountsService) {}

  ngOnInit() {
    this.accountTags = combineLatest([
      this.accountsService.getTags(),
      this.tagsCtrl.valueChanges.pipe(
        startWith(null as string),
        map(filterValue => (filterValue ? (filterValue as string).toLowerCase() : ''))
      )
    ]).pipe(map(([tagsList, filterValue]) => tagsList.filter(tag => tag.toLowerCase().includes(filterValue))));
  }

  selectTag(event: MatAutocompleteSelectedEvent) {
    this.accountsService.addTag(event.option.viewValue);
    this.tagsInput.nativeElement.value = '';
    this.tagsCtrl.setValue(null);
  }

  addTag(event: MatChipInputEvent) {
    if (this.matAutocomplete.isOpen) {
      return;
    }
    const input = event.input;
    const value = event.value;

    if (this.matAutocomplete.options.filter(option => option.value === value.trim()).length !== 1) {
      return;
    }

    this.accountsService.addTag(value.trim());
    if (input) {
      input.value = '';
    }
    this.tagsCtrl.setValue(null);
  }

  removeTag(tag: string) {
    this.accountsService.removeTag(tag);
  }

  clearTags() {
    this.accountsService.clearTags();
  }
}
