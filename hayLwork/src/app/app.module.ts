import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { MoneyDetailComponent } from './money-detail/money-detail.component';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    MoneyDetailComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule // 为了组件能用 NgModule，需要引入这个
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
