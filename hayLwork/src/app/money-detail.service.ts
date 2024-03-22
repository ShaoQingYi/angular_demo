import { Injectable } from '@angular/core';
import { formatDate } from '@angular/common';
import { Inject, LOCALE_ID } from '@angular/core';
import { Observable, of } from 'rxjs';
import { costType } from './costType';
import { costTypes } from './mock-costType';
import { payMethod } from './payMethod';
import { payMethods } from './mock-payMethod';
import { incomeType } from './incomeType';
import { incomeTypes } from './mock-incomeType';

@Injectable({
  providedIn: 'root'
})
export class MoneyDetailService {

  constructor(
    @Inject(LOCALE_ID) public locale: string,
  ) { }

  // TODO SpringBoot
  private serviceUrl = 'http://127.0.0.1:8080';

  getToday(): string {
    return formatDate(new Date(), 'yyyy-MM-dd',this.locale);
  }

  getCostTypes(): Observable <costType[]> {
    const costtypes = of(costTypes);
    return costtypes;
  }

  getPayMethods(): Observable <payMethod[]> {
    const paymethods = of(payMethods);
    return paymethods;
  }

  getIncomeTypes(): Observable <incomeType[]> {
    const incometypes = of(incomeTypes);
    return incometypes;
  } 
}
