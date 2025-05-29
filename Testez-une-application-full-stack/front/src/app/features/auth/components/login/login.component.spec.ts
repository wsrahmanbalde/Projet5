import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';


import { LoginComponent } from './login.component';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceMock: any;
  let sessionServiceMock: any;
  let routerMock: any;

  beforeEach(async () => {
    authServiceMock = { login: jest.fn() };
    sessionServiceMock = { logIn: jest.fn() };
    routerMock = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should log in successfully', () => {
    const mockSession: SessionInformation = {
      token: 'token123',
      type: 'Bearer',
      id: 1,
      username: 'john',
      firstName: 'John',
      lastName: 'Doe',
      admin: false
    };

    component.form.setValue({
      email: 'john@example.com',
      password: '123456'
    });

    authServiceMock.login.mockReturnValue(of(mockSession));

    component.submit();

    expect(authServiceMock.login).toHaveBeenCalledWith({
      email: 'john@example.com',
      password: '123456'
    });
    expect(sessionServiceMock.logIn).toHaveBeenCalledWith(mockSession);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBe(false);
  });

  it('should set onError to true on login failure', () => {
    component.form.setValue({
      email: 'wrong@example.com',
      password: 'wrongpass'
    });

    authServiceMock.login.mockReturnValue(throwError(() => new Error('Invalid credentials')));

    component.submit();

    expect(component.onError).toBe(true);
  });
});

describe('LoginComponent', () => {
  let component: LoginComponent;
  let authService: AuthService;
  let sessionService: SessionService;
  let router: Router;

  beforeEach(() => {
    authService = { login: jest.fn() } as any;
    sessionService = { logIn: jest.fn() } as any;
    router = { navigate: jest.fn() } as any;

    component = new LoginComponent(authService, new FormBuilder(), router, sessionService);
  });

  it('should call login and navigate on success', () => {
    // Setup mock response for the login service
    const mockResponse = { token: 'fakeToken' };
    authService.login = jest.fn().mockReturnValue(of(mockResponse));

    // Spy on navigate and logIn methods
    const routerSpy = jest.spyOn(router, 'navigate');
    const sessionServiceSpy = jest.spyOn(sessionService, 'logIn');

    // Simulate form submission
    component.form.setValue({ email: 'user@example.com', password: 'password123' });
    component.submit();

    // Assertions
    expect(authService.login).toHaveBeenCalledWith({
      email: 'user@example.com',
      password: 'password123',
    });

    expect(sessionServiceSpy).toHaveBeenCalledWith(mockResponse);

    expect(routerSpy).toHaveBeenCalledWith(['/sessions']);
  });

  it('should set error flag on login failure', () => {
    // Setup mock failure for the login service
    authService.login = jest.fn().mockReturnValue(throwError('Login failed'));

    // Spy on navigate and logIn methods
    const routerSpy = jest.spyOn(router, 'navigate');
    const sessionServiceSpy = jest.spyOn(sessionService, 'logIn');

    // Simulate form submission
    component.form.setValue({ email: 'user@example.com', password: 'password123' });
    component.submit();

    // Assertions
    expect(authService.login).toHaveBeenCalledWith({
      email: 'user@example.com',
      password: 'password123',
    });

    // Check if sessionService and router are not called in case of error
    expect(sessionServiceSpy).not.toHaveBeenCalled();
    expect(routerSpy).not.toHaveBeenCalled();

    // Ensure the error flag is set
    expect(component.onError).toBe(true);
  });
});