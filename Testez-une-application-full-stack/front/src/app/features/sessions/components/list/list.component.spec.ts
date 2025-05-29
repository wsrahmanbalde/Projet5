import { expect } from '@jest/globals';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListComponent } from './list.component';
import { SessionService } from '../../../../services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { of } from 'rxjs';
import { Session } from '../../interfaces/session.interface';
import { SessionInformation } from '../../../../interfaces/sessionInformation.interface';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  const mockSessions: Session[] = [
    {
      id: 1,
      name: 'Session 1',
      date: new Date('2025-05-01'),
      teacher_id: 1,
      description: 'Desc',
      users: [],
      createdAt: new Date(),
      updatedAt: new Date()
    },
    {
      id: 2,
      name: 'Session 2',
      date: new Date('2025-06-01'),
      teacher_id: 2,
      description: 'Desc 2',
      users: [10],
      createdAt: new Date(),
      updatedAt: new Date()
    }
  ];

  const mockUser: SessionInformation = {
    token: 'abc123',
    type: 'Bearer',
    id: 10,
    username: 'jdoe',
    firstName: 'John',
    lastName: 'Doe',
    admin: true
  };

  const sessionApiServiceStub = {
    all: jest.fn().mockReturnValue(of(mockSessions))
  };

  const sessionServiceStub = {
    sessionInformation: mockUser
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      providers: [
        { provide: SessionApiService, useValue: sessionApiServiceStub },
        { provide: SessionService, useValue: sessionServiceStub }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should expose session data from service', (done) => {
    component.sessions$.subscribe((sessions) => {
      expect(sessions.length).toBe(2);
      expect(sessions[0].name).toBe('Session 1');
      expect(sessions[1].users).toContain(10);
      done();
    });
  });

  it('should return the current session user', () => {
    expect(component.user).toEqual(mockUser);
  });
});