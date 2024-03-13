import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';// 为了组件能用 NgModule，需要引入这个

import { AppComponent } from './app.component';
import { HerosComponent } from './heros/heros.component';
import { HeroDetailComponent } from './hero-detail/hero-detail.component';
import { MessagesComponent } from './messages/messages.component';
import { AppRoutingModule } from './app-routing.module';
import { DashboardComponent } from './dashboard/dashboard.component';

import { HttpClientModule } from '@angular/common/http';
import { HttpClientInMemoryWebApiModule } from 'angular-in-memory-web-api';
import { InMemoryDataService } from './in-memory-data.service';
import { HeroformComponent } from './heroform/heroform.component';

@NgModule({
  declarations: [
    AppComponent,
    HerosComponent,
    HeroDetailComponent,
    MessagesComponent,
    DashboardComponent,
    HeroformComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,// 为了组件能用 NgModule，需要引入这个
    AppRoutingModule,
    HttpClientModule

    // // The HttpClientInMemoryWebApiModule module intercepts HTTP requests
    // // and returns simulated server responses.
    // // Remove it when a real server is ready to receive requests.
    // // "angular-in-memory-web-api": "^0.17.0",
    // HttpClientInMemoryWebApiModule.forRoot(
    //   InMemoryDataService, { dataEncapsulation: false }
    // )
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
