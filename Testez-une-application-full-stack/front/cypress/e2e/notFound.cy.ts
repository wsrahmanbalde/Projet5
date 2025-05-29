describe('Page Not Found', () => {
  it('Affiche la page Not Found quand l’URL est inconnue', () => {

    cy.visit('/truc-inexistant', { failOnStatusCode: false });

    cy.wait(1000);

    cy.contains('Page not found !').should('exist');
    cy.contains(/not found/i).should('exist');

    cy.url().should('include', '/404');

    cy.wait(1000);
  });
});