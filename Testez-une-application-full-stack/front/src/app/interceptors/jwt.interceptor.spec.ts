import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { JwtInterceptor } from './jwt.interceptor';
import { SessionService } from '../services/session.service';
import { HttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';
import { expect } from '@jest/globals';

describe('JwtInterceptor', () => {
    let httpClient: HttpClient;
    let httpTestingController: HttpTestingController;
    let sessionService: SessionService;
  
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule], // Utilisation du module de test HTTP
        providers: [
          JwtInterceptor, // Fournisseur de l'intercepteur
          { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }, // Assure que l'intercepteur est appliqué
          SessionService // Fournisseur du service de session
        ]
      });
  
      httpClient = TestBed.inject(HttpClient); // Injection du client HTTP
      httpTestingController = TestBed.inject(HttpTestingController); // Injection du contrôleur de test HTTP
      sessionService = TestBed.inject(SessionService); // Injection du service de session
    });
  
    it('should add Authorization header if user is logged in', () => {
      // Simuler l'utilisateur connecté avec un token
      sessionService.sessionInformation = {
        token: 'fake-jwt-token',
        type: 'Bearer',
        id: 1,
        username: 'user',
        firstName: 'John',
        lastName: 'Doe',
        admin: false,
      };
      sessionService.isLogged = true;
  
      // Effectuer une requête HTTP avec HttpClient
      httpClient.get('/test-endpoint').subscribe(response => {
        expect(response).toBeDefined(); // On s'assure que la réponse existe
      });
  
      // Vérifier que l'en-tête Authorization est ajouté à la requête
      const req = httpTestingController.expectOne('/test-endpoint');
      expect(req.request.headers.has('Authorization')).toBe(true); // Vérifier que l'en-tête Authorization est bien présent
      expect(req.request.headers.get('Authorization')).toBe('Bearer fake-jwt-token'); // Vérifier la valeur de l'en-tête
  
      // Vérifier que la requête a bien été envoyée
      req.flush({});
    });
  
    it('should not add Authorization header if user is not logged in', () => {
      // Simuler l'utilisateur non connecté
      sessionService.isLogged = false;
  
      // Effectuer une requête HTTP
      httpClient.get('/test-endpoint').subscribe(response => {
        expect(response).toBeDefined();
      });
  
      // Vérifier qu'aucun en-tête Authorization n'est présent
      const req = httpTestingController.expectOne('/test-endpoint');
      expect(req.request.headers.has('Authorization')).toBe(false); // Vérifier l'absence de l'en-tête Authorization
  
      // Vérifier que la requête a bien été envoyée
      req.flush({});
    });
  
    afterEach(() => {
      // Vérifier qu'il n'y a pas de requêtes HTTP en attente
      httpTestingController.verify();
    });
  });