import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';
import { Teacher } from '../interfaces/teacher.interface';
import { of } from 'rxjs';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpClientMock: jest.Mocked<HttpClient>;

  beforeEach(() => {
    httpClientMock = {
      get: jest.fn()
    } as unknown as jest.Mocked<HttpClient>;

    service = new TeacherService(httpClientMock);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all teachers using correct URL', () => {
    const mockTeachers: Teacher[] = [
      {
        id: 1,
        firstName: 'Alice',
        lastName: 'Smith',
        createdAt: new Date('2024-01-01'),
        updatedAt: new Date('2024-02-01')
      }
    ];

    httpClientMock.get.mockReturnValue(of(mockTeachers));

    service.all().subscribe(result => {
      expect(result).toEqual(mockTeachers);
    });

    expect(httpClientMock.get).toHaveBeenCalledWith('api/teacher');
  });

  it('should fetch teacher by ID using correct URL', () => {
    const mockTeacher: Teacher = {
      id: 1,
      firstName: 'Alice',
      lastName: 'Smith',
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-02-01')
    };

    httpClientMock.get.mockReturnValue(of(mockTeacher));

    service.detail('1').subscribe(result => {
      expect(result).toEqual(mockTeacher);
    });

    expect(httpClientMock.get).toHaveBeenCalledWith('api/teacher/1');
  });
});