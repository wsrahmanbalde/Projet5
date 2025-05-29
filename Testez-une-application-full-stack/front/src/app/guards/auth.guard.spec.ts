import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { SessionService } from '../services/session.service';
import { expect } from '@jest/globals';

describe('AuthGuard', () => {
  let guard: AuthGuard;
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
        AuthGuard,
        { provide: Router, useValue: routerMock },
        { provide: SessionService, useValue: sessionServiceMock }
      ]
    });

    guard = TestBed.inject(AuthGuard);
  });

  it('should allow activation if user is logged in', () => {
    sessionServiceMock.isLogged = true;

    const result = guard.canActivate();

    expect(result).toBe(true);
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });

  it('should block activation and redirect if user is not logged in', () => {
    sessionServiceMock.isLogged = false;

    const result = guard.canActivate();

    expect(result).toBe(false);
    expect(routerMock.navigate).toHaveBeenCalledWith(['login']);
  });
});