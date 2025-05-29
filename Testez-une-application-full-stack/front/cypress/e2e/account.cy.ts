describe('Login spec', () => {
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

    cy.get('input[formControlName=email]').type("yoga@studio.com");
    cy.get('input[formControlName=password]').type("test!1234");

    cy.get('button[type=submit]').click();

    cy.wait('@login');

    cy.url().should('include', '/sessions');

    cy.contains('Account').click();

    cy.wait('@getUserById');

    cy.contains('firstName LASTNAME');
    cy.contains('yoga@studio.com');
  });
});