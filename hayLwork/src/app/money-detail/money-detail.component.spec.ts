import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MoneyDetailComponent } from './money-detail.component';

describe('MoneyDetailComponent', () => {
  let component: MoneyDetailComponent;
  let fixture: ComponentFixture<MoneyDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MoneyDetailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MoneyDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
