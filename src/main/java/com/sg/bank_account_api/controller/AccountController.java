package com.sg.bank_account_api.controller;

import com.sg.bank_account_api.dto.AccountDto;
import com.sg.bank_account_api.dto.CreateClientDto;
import com.sg.bank_account_api.dto.CreateTransactionDto;
import com.sg.bank_account_api.dto.CreatedAccountDto;
import com.sg.bank_account_api.dto.StatementDto;
import com.sg.bank_account_api.model.Account;
import com.sg.bank_account_api.model.TransactionType;
import com.sg.bank_account_api.service.IAccountService;
import com.sg.bank_account_api.utils.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final IAccountService service;
    private final DtoMapper mapper;

    /**
     * Endpoint de creation du compte. il faut fournir les nom et/ou prenom du
     * client
     *
     * @param dto
     * @return
     */
    @PostMapping()
    public ResponseEntity<CreatedAccountDto> create(@RequestBody CreateClientDto dto) {
        CreatedAccountDto createdAccount = service.createAccount(dto);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    /**
     * Endpoint pour effectuer un depot. il faut fournir le numéro du compte,
     * l'identifiant du client et le montant
     *
     * @param dto
     * @return
     */
    @PostMapping("/deposit")
    public ResponseEntity<StatementDto> deposit(@RequestBody CreateTransactionDto dto) {
        StatementDto statement = service.performTransaction(dto, TransactionType.DEPOSIT);
        return new ResponseEntity<>(statement, HttpStatus.OK);
    }

    /**
     * Endpoint pour effectuer un retrait. il faut fournir le numéro du compte,
     * l'identifiant du client et le montant
     *
     * @param dto
     * @return
     */
    @PostMapping("/withdraw")
    public ResponseEntity<StatementDto> withdraw(@RequestBody CreateTransactionDto dto) {
        StatementDto statement = service.performTransaction(dto, TransactionType.WITHDRAW);
        return new ResponseEntity<>(statement, HttpStatus.OK);
    }

    /**
     * Endpoint qui permet de retourner l'état et les informations d'un compte à
     * partir de son numero de compte
     *
     * @param accountId
     * @return
     */
    @GetMapping("/statement/{accountId}")
    public ResponseEntity<AccountDto> printStatement(@PathVariable String accountId) {
        Account account = service.getAccountById(accountId);
        return ResponseEntity.ok(mapper.accountToDto(account));
    }

}
