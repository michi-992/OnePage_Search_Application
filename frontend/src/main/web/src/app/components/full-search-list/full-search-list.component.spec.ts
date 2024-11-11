import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FullSearchListComponent } from './full-search-list.component';

describe('FullSearchListComponent', () => {
  let component: FullSearchListComponent;
  let fixture: ComponentFixture<FullSearchListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FullSearchListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FullSearchListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
