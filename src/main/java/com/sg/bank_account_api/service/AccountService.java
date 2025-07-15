package com.sg.bank_account_api.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.sg.bank_account_api.dto.CreatedAccountDto;
import com.sg.bank_account_api.dto.TransactionDto;
import com.sg.bank_account_api.exceptions.AccountNotFoundException;
import com.sg.bank_account_api.exceptions.AmountException;
import com.sg.bank_account_api.exceptions.ClientNotFoundException;
import com.sg.bank_account_api.model.Account;
import com.sg.bank_account_api.model.Client;
import com.sg.bank_account_api.model.Transaction;
import com.sg.bank_account_api.model.TransactionType;
import com.sg.bank_account_api.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

/**
 * Classe qui gèrer toute la logique associée à un compte
 */
@RequiredArgsConstructor
@Service
public final class AccountService implements IAccountService {

    // Dépendances
    private final AccountRepository accountRepository;

    private final ClientService clientService;

    @Override
    public CreatedAccountDto createAccount(Client client) {
        // création du client
        Client createdClient = clientService.createClient(client);

        // Création du compte pour le client
        Account newAccount = accountRepository.save(new Account(createdClient));

        return new CreatedAccountDto(newAccount.getId(), createdClient.getId());
    }

    @Override
    public Transaction performTransaction(TransactionDto dto, TransactionType type) {
        // récupération du compte à patir de l'indentifiant
        Account existingAccount = getAccountById(dto.accountId());

        // extraction du client
        Client client = existingAccount.getClient();

        // on vérifie qu'on peut effectuer la transaction avec le montant renseigné
        validateAmount(dto.amount(), type, existingAccount.getBalance());

        // on vérifie que le compte appartient bien au client
        validateClientForAccount(dto.clientId(), dto.accountId(), client);

        // On créé la transaction et on l'ajoute au compte
        Transaction transaction = createTransactionAndUpdateAccount(type, dto.amount(), existingAccount);

        return transaction;
    }

    @Override
    public Account getAccountById(String accountId) {
        // on renvoi le compte à partir de l'identifiant
        return accountRepository.findById(accountId)
                // on lève une erreur une erreur si le compte n'existe pas
                .orElseThrow(() -> new AccountNotFoundException("Account not found for ID : " + accountId));
    }

    @Override
    public void updateAcount(Account account) {
        // on met à jout le compte
        if (account != null) {
            accountRepository.save(account);
        }
    }

    /**
     * Methode qui permet de vérifier la validité du montant
     * 
     * @param amount
     * @param type
     * @param balance
     */
    private void validateAmount(BigDecimal amount, TransactionType type, BigDecimal balance) {
        /**
         * le montant doit ête positif
         * dans la cas où c'est un retrait, il doit être inférieur (ou égal) au solde
         * sinon une exception est levée
         */
        if (amount.compareTo(BigDecimal.ZERO) <= 0
                || (TransactionType.WITHDRAW.equals(type) && amount.compareTo(balance) >= 0)) {
            throw new AmountException("Amount must be > 0 and must be <= balance in the case of a withdrawal");
        }
    }

    /**
     * Methode qui permet de créer un transaction
     * 
     * @param type
     * @param amount
     * @param existingAccount
     * @return
     */
    private Transaction createTransactionAndUpdateAccount(TransactionType type, BigDecimal amount,
            Account existingAccount) {
        // si c'est un retrait, le montant devient gégatif
        BigDecimal transactionAmount = TransactionType.WITHDRAW.equals(type) ? amount.negate() : amount;
        // on met à jour solde
        BigDecimal newBalance = existingAccount.getBalance().add(transactionAmount);
        // création de la transaction
        Transaction transaction = new Transaction(transactionAmount, newBalance);

        // on met à jour le compte avec le nouveau solde et la nouvelle transaction
        existingAccount.setBalance(newBalance);
        existingAccount.getTransactions().add(transaction);
        updateAcount(existingAccount);

        return transaction;
    }

    /**
     * Methode qui permet de vérier que le le compte est bien celui du client
     * 
     * @param clientId
     * @param accountId
     * @param client
     */
    private void validateClientForAccount(String clientId, String accountId, Client client) {
        if (!client.getId().equals(clientId)) {
            final String msg = String.format("Client with id %s is not associated with account %s", clientId,
                    accountId);
            throw new ClientNotFoundException(msg);
        }
    }
}
