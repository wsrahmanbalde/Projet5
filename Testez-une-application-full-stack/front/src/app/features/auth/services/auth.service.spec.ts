import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { expect } from '@jest/globals';

describe('AuthService', () => {
    let service: AuthService;
    let httpMock: HttpTestingController;
  
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [AuthService],
      });
      service = TestBed.inject(AuthService);
      httpMock = TestBed.inject(HttpTestingController);
    });
  
    afterEach(() => {
      httpMock.verify();
    });
  
    it('should be created', () => {
      expect(service).toBeTruthy();
    });
  
    it('should send a POST request on register and handle void response', () => {
      const mockRegisterData: RegisterRequest = {
        email: 'test@example.com',
        firstName: 'John',
        lastName: 'Doe',
        password: 'password123'
      };
  
      service.register(mockRegisterData).subscribe(response => {
        expect(response).toBeUndefined(); // No response body, so we expect undefined
      });
  
      const req = httpMock.expectOne('api/auth/register');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockRegisterData);
      req.flush(null); // Simulate a void response with null (should match Observable<void>)
    });
  
    it('should send a POST request on login and return session information', () => {
      const mockLoginData: LoginRequest = {
        email: 'test@example.com',
        password: 'password123'
      };
  
      const mockResponse: SessionInformation = {
        token: 'fake-jwt-token',
        type: 'Bearer',
        id: 1,
        username: 'john.doe',
        firstName: 'John',
        lastName: 'Doe',
        admin: false
      };
  
      service.login(mockLoginData).subscribe(response => {
        expect(response).toEqual(mockResponse); // Check if the response matches the mock session information
      });
  
      const req = httpMock.expectOne('api/auth/login');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockLoginData);
      req.flush(mockResponse); // Simulate a successful response with the mock session data
    });
  });

  describe('AuthService Integration Test', () => {
  let authService: AuthService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule], // Remplace HttpClientModule par HttpClientTestingModule pour les tests
      providers: [AuthService]
    });

    authService = TestBed.inject(AuthService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    // Vérifie qu'aucune requête HTTP n'a échoué ou reste en attente
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(authService).toBeTruthy();
  });

  describe('login', () => {
    it('should make an HTTP POST request and return session information', () => {
      const mockSessionInformation: SessionInformation = {
        token: 'fake-jwt-token',
        type: 'Bearer',
        id: 1,
        username: 'johndoe@example.com',
        firstName: 'John',
        lastName: 'Doe',
        admin: true
      };

      const loginRequest: LoginRequest = {
        email: 'johndoe@example.com',
        password: 'password123'
      };

      // Appel à la méthode login du service
      authService.login(loginRequest).subscribe((sessionInformation) => {
        // Vérification que la réponse reçue est correcte
        expect(sessionInformation).toEqual(mockSessionInformation);
      });

      // On s'assure que l'appel HTTP a bien été fait avec la méthode POST vers l'URL correcte
      const req = httpTestingController.expectOne('api/auth/login');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(loginRequest);  // Vérifie que la requête contient bien les bonnes données

      // Simuler la réponse de l'API avec le mock de session
      req.flush(mockSessionInformation);  // Simule la réponse reçue de l'API

      // Vérifie qu'il n'y a pas d'autres appels HTTP en attente
      httpTestingController.verify();
    });
  });
});