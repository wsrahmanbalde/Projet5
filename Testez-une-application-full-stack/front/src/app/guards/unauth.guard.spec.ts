import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { UnauthGuard } from './unauth.guard';
import { SessionService } from '../services/session.service';
import { expect } from '@jest/globals';

describe('UnauthGuard', () => {
  let guard: UnauthGuard;
  let sessionServiceMock: Partial<SessionService>;
  let routerMock: any;

  beforeEach(() => {
    sessionServiceMock = {
      isLogged: false
    };

    routerMock = {
      navigate: jest.fn()
    };

    TestBed.configureTestingModule({
      providers: [
        UnauthGuard,
        { provide: Router, useValue: routerMock },
        { provide: SessionService, useValue: sessionServiceMock }
      ]
    });

    guard = TestBed.inject(UnauthGuard);
  });

  it('should allow activation if user is NOT logged in', () => {
    sessionServiceMock.isLogged = false;

    const result = guard.canActivate();

    expect(result).toBe(true);
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });

  it('should block activation and redirect if user IS logged in', () => {
    sessionServiceMock.isLogged = true;

    const result = guard.canActivate();

    expect(result).toBe(false);
    expect(routerMock.navigate).toHaveBeenCalledWith(['rentals']);
  });
});