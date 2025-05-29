describe('Modification de session avec un utilisateur admin simulé', () => {
  it('Login successful, edit a session and save changes', () => {
   
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true,
        token: 'fake-jwt-token'
      },
    }).as('login');

    let callCount = 0;
    cy.intercept('GET', '/api/session', (req) => {
      callCount++;
      if (callCount === 1) {
        req.reply({
          statusCode: 200,
          body: [
            {
              id: 456,
              name: 'Session existante',
              date: '2025-07-20',
              teacher: { id: 2, firstName: 'Bob', lastName: 'Martin' },
              description: 'Description initiale',
              createdAt: new Date().toISOString(),
              updatedAt: new Date().toISOString()
            }
          ]
        });
      } else {
        req.reply({
          statusCode: 200,
          body: [
            {
              id: 456,
              name: 'Session modifiée',
              date: '2025-06-15',
              teacher: { id: 1, firstName: 'Alice', lastName: 'Dupont' },
              description: 'Description modifiée de la session.',
              createdAt: new Date().toISOString(),
              updatedAt: new Date().toISOString()
            }
          ]
        });
      }
    }).as('getSessions');

    cy.intercept('GET', '/api/session/456', {
      statusCode: 200,
      body: {
        id: 456,
        name: 'Session existante',
        date: '2025-07-20',
        teacher: { id: 2, firstName: 'Bob', lastName: 'Martin' },
        description: 'Description initiale',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
    }).as('getSessionById');

    cy.intercept('GET', '/api/teacher', {
      statusCode: 200,
      body: [
        { id: 1, firstName: 'Alice', lastName: 'Dupont' },
        { id: 2, firstName: 'Bob', lastName: 'Martin' }
      ]
    }).as('getTeachers');

    cy.intercept('PUT', '/api/session/456', (req) => {
      req.reply({
        statusCode: 200,
        body: {
          id: 456,
          name: req.body.name,
          date: req.body.date,
          teacher: req.body.teacher,
          description: req.body.description,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        }
      });
    }).as('updateSession');

    cy.visit('/login');

    cy.wait(500); 

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').click();

    cy.wait('@login');
    cy.url().should('include', '/sessions');
    cy.wait('@getSessions');

    cy.wait(1000); 

    cy.contains('Session existante').should('exist');
    cy.contains('Description initiale').should('exist');

    cy.wait(1000); 

    cy.contains('mat-card.item', 'Session existante').within(() => {
      cy.contains('button', 'Edit').click();
    });

    cy.wait('@getSessionById');
    cy.url().should('include', '/sessions/update/456');
    cy.wait('@getTeachers');

    cy.get('input[formControlName=name]').clear().type('Session modifiée');
    cy.get('input[formControlName=date]').clear().type('2025-06-15');
    cy.get('mat-select[formControlName=teacher_id]').click();
    cy.get('mat-option').contains('Alice Dupont').click();
    cy.get('textarea[formControlName=description]').clear().type('Description modifiée de la session.');

    cy.get('button[type=submit]').contains('Save').click();
    cy.wait('@updateSession');

    cy.url().should('include', '/sessions');
    cy.wait('@getSessions');

    cy.contains('Session modifiée').should('exist');
    cy.contains('Description modifiée de la session.').should('exist');
  });
});