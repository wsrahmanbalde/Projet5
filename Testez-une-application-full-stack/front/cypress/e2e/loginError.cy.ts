describe('Login failure', () => {
  it('should show error on invalid credentials', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Invalid credentials' }
    }).as('loginFail')

    cy.visit('/login')

    cy.get('input[formControlName=email]').type("wrong@user.com")
    cy.get('input[formControlName=password]').type("wrongpass")
    cy.get('button[type=submit]').click()

    cy.wait('@loginFail')

    cy.contains('An error occurred').should('be.visible')
  })
})