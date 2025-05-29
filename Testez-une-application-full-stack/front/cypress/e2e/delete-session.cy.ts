describe('Suppression de session avec un utilisateur admin simulé (version ralentie)', () => {
  it('Login, voir les détails de la session, puis la supprimer', () => {
    cy.visit('/login');
    cy.wait(1000); 

    let sessionDeleted = false;

    const session = {
      id: 456,
      name: 'Session à supprimer',
      date: '2025-07-20',
      teacher: {
        id: 2,
        firstName: 'Bob',
        lastName: 'Martin'
      },
      users: [],
      description: 'Session à tester pour suppression',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true,
        token: 'fake-jwt-token'
      }
    }).as('login');

    cy.intercept('GET', '/api/session', (req) => {
      req.reply(sessionDeleted ? [] : [session]);
    }).as('getSessions');

    cy.intercept('GET', `/api/session/${session.id}`, {
      statusCode: 200,
      body: session
    }).as('getSessionById');

    cy.intercept('DELETE', `/api/session/${session.id}`, (req) => {
      sessionDeleted = true;
      req.reply(204);
    }).as('deleteSession');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.wait(500);
    cy.get('button[type=submit]').click();

    cy.wait('@login');
    cy.wait(1000); 
    cy.url().should('include', '/sessions');
    cy.wait('@getSessions');
    cy.wait(1000); 

   
    cy.contains('mat-card.item', session.name).within(() => {
      cy.contains('button', 'Detail').click();
    });

    cy.wait('@getSessionById');
    cy.wait(1000); 
    cy.url().should('include', `/sessions/detail/${session.id}`);

   
    cy.contains('button', 'Delete').click();
    cy.wait('@deleteSession');
    cy.wait(1000); 

   
    cy.url().should('include', '/sessions');
    cy.wait('@getSessions');
    cy.wait(1000); 

    cy.contains(session.name).should('not.exist');
  });
});