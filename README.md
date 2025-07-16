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
git clone https://github.com/mnkomo/kata-bank-account.git
```
### se positionner sur le répertoire du projet : 
```bash 
cd kata-bank-account
```
## Lancer MongoDB avec Docker (en fonction de la version)
```bash 
docker-compose up -d
```
ou
```bash 
docker compose up -d
```

MongoDB sera disponible sur `mongodb://localhost:27017` (Vous n'avez plus rien à faire)

## Lancer l'application
```bash 
./mvnw spring-boot:run
```
L'API sera disponible sur : [http://localhost:8080](http://localhost:8080)


## Utilisation avec Postman
Le chemin de base de l'API est /account.

Créer un Compte

Crée un nouveau compte bancaire pour un client.


    Point de terminaison : POST /account

    Corps de la requête :
    JSON

    {
      "lastName": "Doe",
      "firstName": "John"
    }

Réponse (201 Created) :
JSON


    {
      "accountId": "someAccountId",
      "clientId": "someClientId"
    }

    Erreurs possibles :

        400 Bad Request : IllegalArgumentException (par exemple, si lastname ou firstname est manquant ou vide dans le corps de la requête), HttpMessageNotReadableException (par exemple, si le corps de la requête est malformé).

Déposer des Fonds

Effectue un dépôt sur un compte existant.


    Point de terminaison : POST /account/deposit

    Corps de la requête :
    JSON

    {
      "clientId": "someClientId",
      "accountId": "someAccountId",
      "amount": 100.00
    }

Réponse (201 Created) :
JSON


    {
      "date": "2023-07-15",
      "amount": 100.00,
      "balance": 100.00
    }

    Erreurs possibles :

        404 Not Found : AccountNotFoundException (si l'accountId n'existe pas), ClientNotFoundException (si le clientId n'est pas associé à l'accountId).

        400 Bad Request : AmountException (si le amount n'est pas positif), HttpMessageNotReadableException (par exemple, si le corps de la requête est malformé).

Retirer des Fonds

Effectue un retrait d'un compte existant.


    Point de terminaison : POST /account/withdraw

    Corps de la requête :
    JSON

    {
      "clientId": "someClientId",
      "accountId": "someAccountId",
      "amount": 50.00
    }

Réponse (201 Created) :
JSON


    {
      "date": "2023-07-15",
      "amount": -50.00,
      "balance": 50.00
    }

    Erreurs possibles :

        404 Not Found : AccountNotFoundException (si l'accountId n'existe pas), ClientNotFoundException (si le clientId n'est pas associé à l'accountId).

        400 Bad Request : AmountException (si le amount n'est pas positif ou dépasse le balance du compte), HttpMessageNotReadableException (par exemple, si le corps de la requête est malformé).

Obtenir le Relevé de Compte

Récupère les détails et l'historique des transactions pour un compte spécifique.


    Point de terminaison : GET /account/statement/{accountId}

    Variable de chemin : accountId (String) - L'identifiant du compte.

    Réponse (200 OK) :
    JSON

    {
      "client": {
        "lastName": "Doe",
        "firstName": "John"
      },
      "balance": 50.00,
      "date": "2023-07-15",
      "statements": [
        {
          "date": "2023-07-15",
          "amount": 100.00,
          "balance": 100.00
        },
        {
          "date": "2023-07-15",
          "amount": -50.00,
          "balance": 50.00
        }
      ]
    }

    Erreurs possibles :

        404 Not Found : AccountNotFoundException (si l'accountId n'existe pas).

Gestion des Erreurs

L'API offre une gestion centralisée des exceptions grâce à @ControllerAdvice.

    404 Not Found :

        AccountNotFoundException : Lorsqu'un compte avec l'identifiant donné n'existe pas.

        ClientNotFoundException : Lorsqu'un client avec l'identifiant donné n'est pas associé au compte.

    400 Bad Request :

        TransactionTypeException : (Gérée par l'@ExceptionHandler mais non explicitement lancée par l'AccountService dans le code fourni).

        AmountException : Lorsque le montant de la transaction est invalide (par exemple, non positif ou un retrait dépassant le solde).

        IllegalArgumentException : Lorsque les arguments d'entrée sont invalides (par exemple, informations client manquantes).

        HttpMessageNotReadableException : Lorsque le corps de la requête est malformé.

    500 Internal Server Error :

        Exception : Capture toutes les exceptions non gérées, retournant une erreur générique du serveur interne.

Modèles de Données

Client

Représente un client bancaire.


    id : Identifiant unique du client (String).
    lastName : Nom de famille du client (String).
    firstName : Prénom du client (String).
    date : Date de création du client (LocalDate).

Account

Représente un compte bancaire.


    id : Identifiant unique du compte (String).
    balance : Solde actuel du compte (BigDecimal).
    client : Référence au client associé (Client).
    date : Date de création du compte (LocalDate).
    transactions : Liste des transactions effectuées sur le compte (List<Statement>).

Transaction

Représente une transaction financière (dépôt ou retrait).


    amount : Montant de la transaction (BigDecimal, positif pour un dépôt, négatif pour un retrait).
    balance : Solde du compte après la transaction (BigDecimal).
    date : Date de la transaction (LocalDate).

Statement

Représente un enregistrement d'une transaction dans le relevé de compte.


    date : Date de la transaction (LocalDate).
    amount : Montant de la transaction (BigDecimal).
    balance : Solde après la transaction (BigDecimal).
