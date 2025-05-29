describe('Register spec - Form invalid', () => {
  it('should not allow submit with invalid data', () => {
    cy.visit('/register')

    cy.get('input[formControlName=firstName]').type('J') // trop court
    cy.get('input[formControlName=lastName]').type('D')  // trop court
    cy.get('input[formControlName=email]').type('invalid-email') // pas d'@
    cy.get('input[formControlName=password]').type('pw') // trop court

    cy.get('button[type=submit]').should('be.disabled')

    cy.intercept('POST', '/api/auth/register').as('register')
  })
})