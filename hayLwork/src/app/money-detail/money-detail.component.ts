import { Component, OnInit, ViewChild } from '@angular/core';
import { MoneyDetailEntity } from '../MoneyDetailEntity';
import { MoneyDetailService } from '../money-detail.service';
import { NgForm } from '@angular/forms';
import { costType } from '../costType';
import { payMethod } from '../payMethod';
import { incomeType } from '../incomeType';

@Component({
  selector: 'app-money-detail',
  templateUrl: './money-detail.component.html',
  styleUrls: ['./money-detail.component.css']
})
export class MoneyDetailComponent implements OnInit {

  constructor(private moneyDetailService: MoneyDetailService) { }

  ngOnInit(): void {
    this.getCostTypes();
    this.getPayMethods();
    this.getIncomeTypes();
  }

  // 入账: true or 出帐: false?:boolean;
  selectedOptionInorOut: String = "Out";
  isOut = true;

  // costTypes
  costTypes : costType[] = [];
  // payMethods
  payMethods : payMethod[] = [];
  // incomeTypes
  incomeTypes : incomeType[] = [];

  // 初期
  moneyDetail = new MoneyDetailEntity(true, this.moneyDetailService.getToday(),
                                      '', 0, '', '', true, '', '' , 0, '');

  submitted = false;

  onSubmit() { this.submitted = true; }

  onClear(moneyDetailForm: NgForm) {
    moneyDetailForm.reset();
    this.moneyDetail = new MoneyDetailEntity(true, this.moneyDetailService.getToday(),
                                              '', 0, '', '', true, '', '' , 0, '');
    moneyDetailForm.controls['inOrOut'].patchValue('Out');
    // this.selectedOptionInorOut = 'Out';
    // this.isOut = true;
  }

  toggleElements() {
    if (this.selectedOptionInorOut === "Out"){
      this.isOut = true;
    }else{
      this.isOut = false;
    }
  }

  getCostTypes(): void {
    this.moneyDetailService.getCostTypes()
      .subscribe(costTypes => this.costTypes = costTypes);
  }

  getPayMethods(): void {
    this.moneyDetailService.getPayMethods()
      .subscribe(payMethods => this.payMethods = payMethods);
  }

  getIncomeTypes(): void {
    this.moneyDetailService.getIncomeTypes()
      .subscribe(incomeTypes => this.incomeTypes = incomeTypes);
  }
}
