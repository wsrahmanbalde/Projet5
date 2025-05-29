import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { SessionsRoutingModule } from './sessions-routing.module';
import { ListComponent } from './components/list/list.component';
import { DetailComponent } from './components/detail/detail.component';
import { FormComponent } from './components/form/form.component';
import { expect } from '@jest/globals';

describe('SessionsRoutingModule', () => {
  let router: Router;
  let location: Location;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([]), SessionsRoutingModule],
      declarations: [ListComponent, DetailComponent, FormComponent]
    }).compileComponents();

    router = TestBed.inject(Router);
    location = TestBed.inject(Location);

    router.initialNavigation();
  });

  it('should navigate to / and load ListComponent', fakeAsync(() => {
    router.navigate(['/']);
    tick();
    expect(location.path()).toBe('/');
  }));

  it('should navigate to /detail/:id and load DetailComponent', fakeAsync(() => {
    router.navigate(['/detail/1']);
    tick();
    expect(location.path()).toBe('/detail/1');
  }));

  it('should navigate to /create and load FormComponent', fakeAsync(() => {
    router.navigate(['/create']);
    tick();
    expect(location.path()).toBe('/create');
  }));

  it('should navigate to /update/:id and load FormComponent', fakeAsync(() => {
    router.navigate(['/update/1']);
    tick();
    expect(location.path()).toBe('/update/1');
  }));
});