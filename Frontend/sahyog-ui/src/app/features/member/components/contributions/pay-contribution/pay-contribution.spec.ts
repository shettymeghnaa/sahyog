import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PayContribution } from './pay-contribution';

describe('PayContribution', () => {
  let component: PayContribution;
  let fixture: ComponentFixture<PayContribution>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PayContribution]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PayContribution);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
