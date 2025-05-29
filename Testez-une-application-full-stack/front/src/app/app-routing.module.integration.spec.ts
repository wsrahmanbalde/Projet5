import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { AppRoutingModule } from './app-routing.module';
import { MeComponent } from './components/me/me.component';
import { NotFoundComponent } from './components/not-found/not-found.component';
import { AuthGuard } from './guards/auth.guard';
import { UnauthGuard } from './guards/unauth.guard';
import { expect } from '@jest/globals';

describe('AppRoutingModule', () => {
  let router: Router;
  let location: Location;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([]), AppRoutingModule],
      declarations: [MeComponent, NotFoundComponent],
      providers: [
        { provide: AuthGuard, useValue: { canActivate: () => true } },
        { provide: UnauthGuard, useValue: { canActivate: () => true } }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    location = TestBed.inject(Location);

    router.initialNavigation();
  });

  it('should navigate to /me and load MeComponent', fakeAsync(() => {
    router.navigate(['/me']);
    tick();
    expect(location.path()).toBe('/me');
  }));

  it('should redirect unknown routes to /404', fakeAsync(() => {
    router.navigate(['/some/unknown/path']);
    tick();
    expect(location.path()).toBe('/404');
  }));
});