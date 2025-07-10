Bank Account Backend API

Ce projet est une API REST simple pour gérer un compte bancaire : dépôts, retraits et impression de relevé. Il est développé avec Spring Boot, Java 21 et utilise MongoDB comme base de données.
# Démarrage rapide
## Prérequis

- Java 21
- Maven 3.9+
- Docker (pour MongoDB)
- Postman (ou tout autre client HTTP)

### cloner le projet : 
```bash 
git clone https://github.com/marcelinNkomo/kata-bank-account.git
```
### se positionner sur le répertoire du projet : 
```bash 
cd kata-bank-account
```
## Lancer MongoDB avec Docker
```bash 
docker-compose up -d
```
MongoDB sera disponible sur `mongodb://localhost:27017` (Vous n'avez plus rien à faire)

## Lancer l'application
```bash 
./mvnw spring-boot:run
```
L'API sera disponible sur : [http://localhost:8080](http://localhost:8080)


## Utilisation avec Postman
1. Faire un dépôt
- Méthode : POST
- URL : http://localhost:8080/account/deposit?amount=100

Remplace 100 par le montant souhaité.

2. Faire un retrait
- Méthode : POST
- URL : http://localhost:8080/account/withdraw?amount=50

Le montant doit être inférieur ou égal au solde disponible.

3. Obtenir le relevé de compte
- Méthode : GET
- URL : http://localhost:8080/account/statement

Retourne la liste des transactions avec date, montant et solde.