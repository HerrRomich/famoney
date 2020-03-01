import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SimpleNotificationsModule } from 'angular2-notifications';
import { AngularModule } from '@famoney-shared/angular.module';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DATE_LOCALE, MatNativeDateModule, DateAdapter, MAT_DATE_FORMATS } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MomentDateAdapter, MAT_MOMENT_DATE_ADAPTER_OPTIONS } from '@angular/material-moment-adapter';

export const MY_FORMATS = {
  parse: {
    dateInput: 'L'
  },
  display: {
    dateInput: 'L',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY'
  }
};

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    AngularModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatNativeDateModule,
    FlexLayoutModule,
    SimpleNotificationsModule.forRoot()
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'ru-RU' },
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE, MAT_MOMENT_DATE_ADAPTER_OPTIONS]
    },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS },
    { provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: { strict: true } }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
