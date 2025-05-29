import { FormComponent } from './form.component';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SessionService } from '../../../../services/session.service';
import { TeacherService } from '../../../../services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';
import { of } from 'rxjs';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { expect } from '@jest/globals'; 
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Session } from '../../interfaces/session.interface';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  let mockSessionService: any;
  let mockSessionApiService: any;
  let mockTeacherService: any;
  let mockSnackBar: any;
  let mockRouter: any;
  let mockRoute: any;

  beforeEach(async () => {
    mockSessionService = {
      sessionInformation: { admin: true }
    };
    mockSessionApiService = {
      create: jest.fn().mockReturnValue(of({})),
      update: jest.fn().mockReturnValue(of({})),
      detail: jest.fn().mockReturnValue(of({
        name: 'Test session',
        date: '2025-05-01',
        teacher_id: 1,
        description: 'A session description',
        users: []
      }))
    };
    mockTeacherService = {
      all: jest.fn().mockReturnValue(of([]))
    };
    mockSnackBar = {
      open: jest.fn()
    };
    mockRouter = {
      url: '/sessions/create',
      navigate: jest.fn()
    };
    mockRoute = {
      snapshot: {
        paramMap: {
          get: jest.fn().mockReturnValue('1')
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [FormComponent],
      providers: [
        FormBuilder,
        { provide: ActivatedRoute, useValue: mockRoute },
        { provide: Router, useValue: mockRouter },
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create form on init in create mode', () => {
    expect(component.sessionForm).toBeDefined();
    expect(component.onUpdate).toBe(false);
  });

  it('should load session and populate form in update mode', () => {
    mockRouter.url = '/sessions/update/1';
    component.ngOnInit();
    expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
  });

  it('should call create on submit in create mode', () => {
    component.onUpdate = false;
    component.sessionForm?.setValue({
      name: 'Test',
      date: '2025-05-01',
      teacher_id: 1,
      description: 'Description'
    });
    component.submit();
    expect(mockSessionApiService.create).toHaveBeenCalled();
    expect(mockSnackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should call update on submit in update mode', () => {
    component.onUpdate = true;
    component['id'] = '1';
    component.sessionForm?.setValue({
      name: 'Test',
      date: '2025-05-01',
      teacher_id: 1,
      description: 'Description'
    });
    component.submit();
    expect(mockSessionApiService.update).toHaveBeenCalledWith('1', expect.anything());
    expect(mockSnackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should redirect if not admin', () => {
    mockSessionService.sessionInformation.admin = false;
    component.ngOnInit();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });
});

describe('FormComponent (integration with real SessionApiService)', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let sessionApiService: SessionApiService;

  const mockRouter = {
    url: '/sessions/create',
    navigate: jest.fn()
  };

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('123')
      }
    }
  };

  const mockSessionService = {
    sessionInformation: { admin: true }
  };

  const mockTeacherService = {
    all: jest.fn().mockReturnValue(of([]))
  };

  const mockMatSnackBar = {
    open: jest.fn()
  };

  const mockSession: Session = {
    id: 123,
    name: 'Test Session',
    description: 'Test description',
    date: new Date('2025-06-01'),
    teacher_id: 1,
    users: [],
    createdAt: new Date(),
    updatedAt: new Date()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FormComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        FormBuilder,
        SessionApiService, // ✅ réel
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: SessionService, useValue: mockSessionService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: MatSnackBar, useValue: mockMatSnackBar }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    sessionApiService = TestBed.inject(SessionApiService);
  });

  it('should create the component', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should call create on SessionApiService and navigate', () => {
    jest.spyOn(sessionApiService, 'create').mockReturnValue(of(mockSession));
    fixture.detectChanges();

    component.sessionForm?.setValue({
      name: 'New Session',
      date: '2025-06-01',
      teacher_id: '1',
      description: 'A description'
    });

    component.submit();

    expect(sessionApiService.create).toHaveBeenCalledWith(expect.any(Object));
    expect(mockMatSnackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should call update on SessionApiService and navigate if onUpdate is true', () => {
    mockRouter.url = '/sessions/update/123';

    jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(mockSession));
    jest.spyOn(sessionApiService, 'update').mockReturnValue(of(mockSession));

    fixture.detectChanges(); // Appelle ngOnInit, remplit le formulaire

    component.submit();

    expect(sessionApiService.update).toHaveBeenCalledWith('123', expect.any(Object));
    expect(mockMatSnackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });
});