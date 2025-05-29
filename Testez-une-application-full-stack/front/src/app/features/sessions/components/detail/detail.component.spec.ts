import { expect } from '@jest/globals'; 
import { DetailComponent } from './detail.component';
import { of } from 'rxjs';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let mockRoute: any;
  let mockRouter: any;
  let mockSessionService: any;
  let mockSessionApiService: any;
  let mockTeacherService: any;
  let mockMatSnackBar: any;

  const sessionMock = {
    id: 1,
    name: 'Test session',
    date: '2025-05-01',
    teacher_id: 1,
    description: 'A test session',
    users: [100]
  };

  const teacherMock = {
    id: 1,
    firstName: 'John',
    lastName: 'Doe',
    email: 'john@doe.com'
  };

  beforeEach(() => {
    mockRoute = {
      snapshot: {
        paramMap: {
          get: jest.fn().mockReturnValue('1')
        }
      }
    };

    mockRouter = {
      navigate: jest.fn()
    };

    mockSessionService = {
      sessionInformation: {
        id: 100,
        admin: true
      }
    };

    mockSessionApiService = {
      detail: jest.fn().mockReturnValue(of(sessionMock)),
      delete: jest.fn().mockReturnValue(of({})),
      participate: jest.fn().mockReturnValue(of({})),
      unParticipate: jest.fn().mockReturnValue(of({}))
    };

    mockTeacherService = {
      detail: jest.fn().mockReturnValue(of(teacherMock))
    };

    mockMatSnackBar = {
      open: jest.fn()
    };

    component = new DetailComponent(
      mockRoute,
      {} as any,
      mockSessionService,
      mockSessionApiService,
      mockTeacherService,
      mockMatSnackBar,
      mockRouter
    );
  });

  it('should initialize and fetch session and teacher', () => {
    component.ngOnInit();
    expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
    expect(mockTeacherService.detail).toHaveBeenCalledWith('1');
    expect(component.session).toEqual(sessionMock);
    expect(component.teacher).toEqual(teacherMock);
    expect(component.isParticipate).toBe(true);
  });

  it('should delete session and navigate', () => {
    component.delete();
    expect(mockSessionApiService.delete).toHaveBeenCalledWith('1');
    expect(mockMatSnackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should call participate and fetch session again', () => {
    const fetchSpy = jest.spyOn(component as any, 'fetchSession');
    component.participate();
    expect(mockSessionApiService.participate).toHaveBeenCalledWith('1', '100');
    expect(fetchSpy).toHaveBeenCalled();
  });

  it('should call unParticipate and fetch session again', () => {
    const fetchSpy = jest.spyOn(component as any, 'fetchSession');
    component.unParticipate();
    expect(mockSessionApiService.unParticipate).toHaveBeenCalledWith('1', '100');
    expect(fetchSpy).toHaveBeenCalled();
  });
});