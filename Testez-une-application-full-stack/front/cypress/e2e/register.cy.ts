describe('Register + Login spec - Manual login after registration', () => {
  it('should register and then log in manually via the login form', () => {
   
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200
    }).as('register');

    cy.visit('/register');

    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('john.doe@example.com');
    cy.get('input[formControlName=password]').type('strongpassword123');

    cy.get('button[type=submit]').click();

    cy.wait('@register');

    cy.url().should('include', '/login');

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: {
        id: 1,
        username: 'john.doe@example.com',
        firstName: 'John',
        lastName: 'Doe',
        admin: false,
        token: 'fake-jwt-token'
      }
    }).as('login');

    cy.intercept('GET', '/api/session', []).as('getSessions');

    cy.intercept('GET', '/api/user/1', {
      statusCode: 200,
      body: {
        id: 1,
        username: 'john.doe@example.com',
        firstName: 'John',
        lastName: 'Doe',
        admin: false,
        email: 'john.doe@example.com'
      }
    }).as('getUser');

    cy.get('input[formControlName=email]').type('john.doe@example.com');
    cy.get('input[formControlName=password]').type('strongpassword123');
    
    cy.get('button[type=submit]').click();

    cy.wait('@login');

    cy.url().should('include', '/sessions');
  });
});