describe('Création de session avec un utilisateur admin simulé', () => {
  it('Login successful and load user info on Account click', () => {
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', (req) => {
      req.reply({
        statusCode: 200,
        headers: {
          'Content-Type': 'application/json',
        },
        delay: 200,
        body: {
          id: 1,
          username: 'userName',
          firstName: 'firstName',
          lastName: 'lastName',
          admin: true,
          token: 'fake-jwt-token'
        },
      });
    }).as('login');

    cy.intercept('GET', '/api/session', []).as('session');

    cy.intercept('GET', '/api/user/1', {
      statusCode: 200,
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true,
        email: 'yoga@studio.com'
      }
    }).as('getUserById');

    cy.intercept('GET', '/api/teacher', {
      statusCode: 200,
      body: [
        {
          id: 1,
          firstName: 'Alice',
          lastName: 'Dupont',
          createdAt: '2024-01-01T00:00:00Z',
          updatedAt: '2024-01-01T00:00:00Z'
        },
        {
          id: 2,
          firstName: 'Bob',
          lastName: 'Martin',
          createdAt: '2024-01-02T00:00:00Z',
          updatedAt: '2024-01-02T00:00:00Z'
        }
      ]
    }).as('getTeachers');

    const createdSession = {
      id: 123,
      name: 'Session de test',
      date: '2025-06-15',
      teacher: {
        id: 1,
        firstName: 'Alice',
        lastName: 'Dupont'
      },
      description: 'Ceci est une description de test pour la session.',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    cy.intercept('POST', '/api/session', {
      statusCode: 201,
      body: createdSession
    }).as('createSession');

    cy.intercept('GET', '/api/session', {
      statusCode: 200,
      body: [createdSession]
    }).as('getSessions');

    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type("test!1234");

    cy.get('button[type=submit]').click();

    cy.wait('@login');

    cy.url().should('include', '/sessions');

    cy.contains('button', 'Create').click();

    cy.get('input[formControlName=name]').type('Session de test');

    cy.get('input[formControlName=date]').type('2025-06-15');

    cy.wait('@getTeachers');

    cy.get('mat-select[formControlName=teacher_id]').click();
    cy.get('mat-option').should('have.length', 2);
    cy.get('mat-option').eq(0).should('contain.text', 'Alice Dupont');
    cy.get('mat-option').eq(1).should('contain.text', 'Bob Martin');

    cy.get('mat-option').contains('Alice Dupont').click();

    cy.get('mat-select[formControlName=teacher_id]').should('contain.text', 'Alice Dupont');

    cy.get('textarea[formControlName=description]').type('Ceci est une description de test pour la session.');

    cy.get('button[type="submit"]').contains('Save').click();

    cy.wait('@createSession');

    cy.wait('@getSessions');

    cy.contains('Session de test').should('be.visible');
  });
});