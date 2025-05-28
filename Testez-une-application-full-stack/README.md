# Yoga App

## 📦 Technologies utilisées

- **Frontend** : Angular 14
- **Backend** : Spring Boot (Java)
- **Base de données** : MySQL
- **Tests Front-end** : Jest (unitaires, intégration, end-to-end avec Cypress)
- **Tests Back-end** : JUnit (unitaires, intégration), Jacoco (couverture)

---

## 🛠️ Installation

### 📂 Cloner le projet

```bash
git clone https://github.com/wsrahmanbalde/Projet4TestApplication.git
cd Projet4TestApplication/Testez-une-application-full-stack
```

---

## 🗃️ Installation de la base de données

> La base de données MySQL est configurée automatiquement par Spring Boot via le fichier `application.properties`.

Assurez-vous que :
- MySQL est installé et fonctionne.
- Le fichier de configuration Spring contient les bonnes informations :

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test
    username: root
    password: root
```

⚠️ Crée la base de données vide `test` si elle n’est pas auto-générée :
```sql
CREATE DATABASE test;
```
Crée les table a partir du fichié `data.sql` qui contient l'ensemble des tables necessaires pour le projet et une insertion des données administrateur.

---

## ⚙️ Installation du backend (Spring Boot)

```bash
cd back
mvn clean install
```

---

## 💻 Installation du frontend (Angular)

```bash
cd front
npm install
npm start
```

---

## 🚀 Lancer l'application

### Backend

```bash
cd back
mvn spring-boot:run
```

### Frontend

```bash
cd front
npm start
```

L’application sera disponible sur :  
🔗 `http://localhost:4200`

---

## 🧪 Lancer les tests

### ✅ Tests unitaires et intégration (Front - Jest)

```bash
cd front
npm run test -- --coverage
```

### ✅ Tests end-to-end (Front - Cypress)

```bash
cd front
Pour lancer tout les tests e2e : npm run e2e:all
npm run e2e:coverage
npm run cypress:open
```

### ✅ Tests unitaires et intégration (Back - JUnit)

```bash
cd back
mvn test
ou  mvn clean test
```

---

## 📊 Générer les rapports de couverture

### 🧩 Couverture Frontend (Jest)

- Un rapport HTML est généré dans :
```bash
front/coverage/lcov-report/index.html
```

### 🧩 Couverture End-to-End (Cypress)

- Un rapport HTML est généré dans :
```bash
front/coverage/lcov-report/index.html
```

### 🧩 Couverture Backend (Jacoco)

- Un rapport HTML est généré dans :
```bash
back/target/site/jacoco/index.html

Pour ouvrir le rapport: 
lancer en ligne de commande : open target/site/jacoco/index.html

```

---

## 🖼️ Captures d’écran

Les captures d’écran des rapports de couverture sont disponibles dans le dossier :

```bash
/docs/screenshots
```

- ✅ Couverture Frontend (Jest)
- ✅ Couverture End-to-End (Cypress)
- ✅ Couverture Backend (Jacoco)

---

## 👨‍💻 Auteur

BALDE Abdourahamane  
Projet de session – Yoga App
