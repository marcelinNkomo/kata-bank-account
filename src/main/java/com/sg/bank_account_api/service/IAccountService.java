package com.sg.bank_account_api.service;

import com.sg.bank_account_api.dto.CreatedAccountDto;
import com.sg.bank_account_api.dto.TransactionDto;
import com.sg.bank_account_api.model.Account;
import com.sg.bank_account_api.model.Client;
import com.sg.bank_account_api.model.Transaction;
import com.sg.bank_account_api.model.TransactionType;

public sealed interface IAccountService permits AccountService {

    /**
     *  permet de creer un compte
     * @param client
     * @return CreatedAccountDto (objet contenant l'id du compte et l'id du client)
     */
    CreatedAccountDto createAccount(Client client);

    /**
     * Permet d'effectuer une transaction (Retrait ou Dépôt)
     * @param dto (transaction)
     * @param type
     * @return
     */
    Transaction performTransaction(TransactionDto dto, TransactionType type);

    /**
     * permet de récuperer un compte à partir de son identifiant
     * @param accountId
     * @return Account
     */
    Account getAccountById(String accountId);

    /**
     * permet de mettre à jour le compte, notamment après une transaction
     * @param account
     */
    void updateAcount(Account account);

}
