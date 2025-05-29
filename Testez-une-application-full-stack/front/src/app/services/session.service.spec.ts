import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    service = new SessionService();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should set session information and isLogged to true on login', () => {
    const mockSession: SessionInformation = {
      token: 'fake-jwt-token',
      type: 'Bearer',
      id: 1,
      username: 'john.doe',
      firstName: 'John',
      lastName: 'Doe',
      admin: false
    };

    service.logIn(mockSession);

    expect(service.sessionInformation).toEqual(mockSession);
    expect(service.isLogged).toBe(true);
  });

  it('should clear session information and set isLogged to false on logout', () => {
    const mockSession: SessionInformation = {
      token: 'fake-jwt-token',
      type: 'Bearer',
      id: 1,
      username: 'john.doe',
      firstName: 'John',
      lastName: 'Doe',
      admin: false
    };

    service.logIn(mockSession); // se loguer avant pour tester le logout
    service.logOut();

    expect(service.sessionInformation).toBeUndefined();
    expect(service.isLogged).toBe(false);
  });

  it('should emit true when logged in and false when logged out', (done) => {
    const mockSession: SessionInformation = {
      token: 'fake-jwt-token',
      type: 'Bearer',
      id: 1,
      username: 'john.doe',
      firstName: 'John',
      lastName: 'Doe',
      admin: false
    };

    const emittedValues: boolean[] = [];

    service.$isLogged().subscribe((value) => {
      emittedValues.push(value);
      // attendre deux valeurs : false initial, puis true
      if (emittedValues.length === 2) {
        expect(emittedValues).toEqual([false, true]);
        done();
      }
    });

    service.logIn(mockSession);
  });
});
