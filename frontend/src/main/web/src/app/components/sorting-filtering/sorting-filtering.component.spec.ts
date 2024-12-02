import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SortingFilteringComponent } from './sorting-filtering.component';

describe('SortingFilteringComponent', () => {
  let component: SortingFilteringComponent;
  let fixture: ComponentFixture<SortingFilteringComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SortingFilteringComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SortingFilteringComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
