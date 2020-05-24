import { FocusHighlightDirective } from './../directives/focus-highlight.directive';
import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
  declarations: [FocusHighlightDirective],
  exports: [TranslateModule, FocusHighlightDirective],
})
export class SharedModule {}
