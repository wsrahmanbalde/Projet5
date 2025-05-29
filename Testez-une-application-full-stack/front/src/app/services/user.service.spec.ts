import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { UserService } from './user.service';
import { User } from '../interfaces/user.interface';
import { of } from 'rxjs';

describe('UserService', () => {
  let service: UserService;
  let httpClientMock: jest.Mocked<HttpClient>;

  beforeEach(() => {
    // Configurer le mock pour HttpClient
    httpClientMock = {
      get: jest.fn(),
      delete: jest.fn()
    } as unknown as jest.Mocked<HttpClient>;

    // Initialiser le service avec HttpClient mocké
    service = new UserService(httpClientMock);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call httpClient.get with correct URL when getById is called', () => {
    const userMock: User = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@example.com',
      password: 'password123',
      admin: true,
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-02-01')
    };

    // Mocker la réponse de get() pour qu'elle renvoie l'utilisateur mocké
    httpClientMock.get.mockReturnValue(of(userMock));

    // Tester la méthode getById() avec un id sous forme de string
    service.getById('1').subscribe((result) => {
      // Vérifier que le résultat est correct
      expect(result).toEqual(userMock);
    });

    // Vérifier que l'URL correcte a été appelée
    expect(httpClientMock.get).toHaveBeenCalledWith('api/user/1');
  });

  it('should call httpClient.delete with correct URL when delete is called', () => {
    // Mocker la réponse de delete() pour qu'elle renvoie un objet vide
    httpClientMock.delete.mockReturnValue(of({}));

    // Tester la méthode delete() avec un id sous forme de string
    service.delete('1').subscribe((result) => {
      // Vérifier que le résultat est un objet vide
      expect(result).toEqual({});
    });

    // Vérifier que l'URL correcte a été appelée
    expect(httpClientMock.delete).toHaveBeenCalledWith('api/user/1');
  });
});