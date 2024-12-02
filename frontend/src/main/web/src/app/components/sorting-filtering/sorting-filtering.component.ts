import { Component, OnDestroy, OnInit } from '@angular/core';
import { SearchDataService } from '../../services/search/search-data.service';
import { AggregationResults } from '../../models/aggregation-results/aggregation-results.model';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-sorting-filtering',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sorting-filtering.component.html',
  styleUrl: './sorting-filtering.component.css'
})
export class SortingFilteringComponent implements OnInit, OnDestroy {
  aggregationResults: AggregationResults | null = null;
  private aggregationResultsSubscription: Subscription = new Subscription();
  private debounceTimer: any;

  size: number = 10;

  sorting: { [key: string]: any } = {
    sortField: null,
    sortOrder: null
  }

  filters: { [key: string]: any } = {
    calories: {
      isApplied: false,
      minValue: 0,
      maxValue: 0,
      minPercent: 0,
      maxPercent: 100,
      isDraggingMin: false,
      isDraggingMax: false
    },
    sodium: {
      isApplied: false,
      minValue: 0,
      maxValue: 0,
      minPercent: 0,
      maxPercent: 100,
      isDraggingMin: false,
      isDraggingMax: false
    },
    fat: {
      isApplied: false,
      minValue: 0,
      maxValue: 0,
      minPercent: 0,
      maxPercent: 100,
      isDraggingMin: false,
      isDraggingMax: false
    },
    protein: {
      isApplied: false,
      minValue: 0,
      maxValue: 0,
      minPercent: 0,
      maxPercent: 100,
      isDraggingMin: false,
      isDraggingMax: false
    },
    rating: {
      isApplied: false,
      minValue: 0,
      maxValue: 0,
      minPercent: 0,
      maxPercent: 100,
      isDraggingMin: false,
      isDraggingMax: false
    }
  };

  aggregationKeyMap = {
    calories: 'maxCalories',
    sodium: 'maxSodium',
    fat: 'maxFat',
    protein: 'maxProtein',
    rating: 'maxRating'
  };

  constructor(private searchDataService: SearchDataService) {}

  ngOnInit(): void {
    this.aggregationResultsSubscription = this.searchDataService.getAggregationResults()
      .subscribe((results: AggregationResults | null) => {
        this.aggregationResults = results;
        if (this.aggregationResults) {
          Object.keys(this.filters).forEach((filterKey) => {
            const filter = this.filters[filterKey];
            if (this.aggregationResults != null) {
              filter.minValue = Math.round(this.aggregationResults[`min${this.capitalizeFirstLetter(filterKey)}`]);
              filter.maxValue = Math.round(this.aggregationResults[`max${this.capitalizeFirstLetter(filterKey)}`]);
            }
            this.updateSlider(filterKey);
          });
        }
      });
  }

  ngOnDestroy(): void {
    if (this.aggregationResultsSubscription) {
      this.aggregationResultsSubscription.unsubscribe();
    }
  }

  startDraggingMin(event: MouseEvent, filterKey: string): void {
    const filter = this.filters[filterKey];
    filter.isDraggingMin = true;
    const handleMouseMove = (e: MouseEvent) => this.handleDrag(e, filterKey, true);
    const handleMouseUp = () => this.stopDragging(filterKey, handleMouseMove, handleMouseUp);

    document.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('mouseup', handleMouseUp);
  }

  startDraggingMax(event: MouseEvent, filterKey: string): void {
    const filter = this.filters[filterKey];
    filter.isDraggingMax = true;
    const handleMouseMove = (e: MouseEvent) => this.handleDrag(e, filterKey, false);
    const handleMouseUp = () => this.stopDragging(filterKey, handleMouseMove, handleMouseUp);

    document.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('mouseup', handleMouseUp);
  }

  handleDrag(event: MouseEvent, filterKey: string, isMin: boolean): void {
    if (!this.aggregationResults) {
      return;
    }

    const filter = this.filters[filterKey];
    const sliderContainer = document.querySelector(`.slider-container-${filterKey}`) as HTMLElement;
    const rect = sliderContainer.getBoundingClientRect();
    let offsetX = event.clientX - rect.left;
    offsetX = Math.max(0, Math.min(rect.width, offsetX));

    const maxValue = this.aggregationResults[`max${this.capitalizeFirstLetter(filterKey)}`];
    const value = (offsetX / rect.width) * maxValue;

    if (isMin) {
      filter.minValue = Math.min(Math.round(value), filter.maxValue);
    } else {
      filter.maxValue = Math.max(Math.round(value), filter.minValue);
    }

    this.updateSlider(filterKey);
    this.onInputChange();
  }

  stopDragging(filterKey: string, handleMouseMove: (e: MouseEvent) => void, handleMouseUp: () => void): void {
    const filter = this.filters[filterKey];
    filter.isDraggingMin = false;
    filter.isDraggingMax = false;

    document.removeEventListener('mousemove', handleMouseMove);
    document.removeEventListener('mouseup', handleMouseUp);
  }

  updateSlider(filterKey: string): void {
    const filter = this.filters[filterKey];
    const maxValue = this.aggregationResults ? this.aggregationResults[`max${this.capitalizeFirstLetter(filterKey)}`] : 1;
    const minPercent = (filter.minValue / maxValue) * 100;
    const maxPercent = (filter.maxValue / maxValue) * 100;

    filter.minPercent = Math.max(Math.min(minPercent, 100), 0);
    filter.maxPercent = Math.max(Math.min(maxPercent, 100), 0);
  }

  validateMinValue(filterKey: string): void {
    const filter = this.filters[filterKey];
    if (filter.minValue === null || filter.minValue === undefined || filter.minValue < 0) {
      filter.minValue = 0;
    } else {
      filter.minValue = Math.round(filter.minValue);
    }
    this.updateSlider(filterKey);
  }

  validateMaxValue(filterKey: string): void {
    const filter = this.filters[filterKey];
    if (this.aggregationResults) {
      const maxValue = this.aggregationResults[`max${this.capitalizeFirstLetter(filterKey)}`];
      if (filter.maxValue > maxValue) {
        filter.maxValue = maxValue;
      }
    }

    if (filter.maxValue === null || filter.maxValue === undefined || filter.maxValue <= 0) {
      filter.maxValue = this.aggregationResults ? Math.round(this.aggregationResults[`max${this.capitalizeFirstLetter(filterKey)}`]) : 0;
    } else {
      filter.maxValue = Math.round(filter.maxValue);
    }

    this.updateSlider(filterKey);
  }

  onKeyDown(event: KeyboardEvent, filterKey: string, isMin: boolean): void {
    if (event.key === 'Enter') {
      if (isMin) {
        this.validateMinValue(filterKey);
      } else {
        this.validateMaxValue(filterKey);
      }
      this.onInputChange();
    }
  }

  toggleFilter(filterKey: string): void {
    this.filters[filterKey].isApplied = this.filters[filterKey].isApplied;
  }


  onSortChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    const sortOption = target?.value || 'default';
  
    if (sortOption === 'default') {
      this.sorting["sortField"] = null;
      this.sorting["sortOrder"] = null;
    } else {
      const [sortField, sortOrder] = sortOption.split(' ');
      this.sorting["sortField"] = sortField;
      this.sorting["sortOrder"] = sortOrder;
    }
    this.onInputChange();
  }

  onSizeChange(newSize: number): void {
    this.size = newSize;
    this.onInputChange();
  }

  objectKeys(obj: any): string[] {
    return Object.keys(obj);
  }

  capitalizeFirstLetter(string: string): string {
    return string.charAt(0).toUpperCase() + string.slice(1);
  }


  onInputChange(): void {
    if (this.debounceTimer) {
      clearTimeout(this.debounceTimer);
    }

    this.debounceTimer = setTimeout(() => {
      this.searchDataService.setPageBoolean(true);
      this.searchDataService.setSize(this.size);

      if(this.filters["calories"].isApplied) {
        this.searchDataService.setMinCalories(this.filters["calories"].minValue);
        this.searchDataService.setMaxCalories(this.filters["calories"].maxValue);
      }
      if(this.filters["sodium"].isApplied) {
        this.searchDataService.setMinSodium(this.filters["sodium"].minValue);
        this.searchDataService.setMaxSodium(this.filters["sodium"].maxValue);
      }
      if(this.filters["fat"].isApplied) {
        this.searchDataService.setMinFat(this.filters["fat"].minValue);
        this.searchDataService.setMaxFat(this.filters["fat"].maxValue);
      }
      if(this.filters["protein"].isApplied) {
        this.searchDataService.setMinProtein(this.filters["protein"].minValue);
        this.searchDataService.setMaxProtein(this.filters["protein"].maxValue);
      }
      if(this.filters["rating"].isApplied) {
        this.searchDataService.setMinRating(this.filters["protein"].minValue);
        this.searchDataService.setMaxRating(this.filters["rating"].maxValue);
      }
      if(this.sorting["sortField"] && this.sorting["sortOrder"]) {
        this.searchDataService.setSortField(this.sorting["sortField"]);
        this.searchDataService.setSortOrder(this.sorting["sortOrder"]);
        console.log(this.sorting["sortOrder"]);
      }

      if(this.searchDataService.getSearchTerm()) {
        console.log(this.searchDataService.getSearchTerm());
        this.searchDataService.onSearch(this.searchDataService.getSearchTerm());
      }
      console.log('Search triggered:', { size: this.size, sorting: this.sorting, filters: this.filters });
    }, 2000);
  }
}