package me.kalpha.jdbctemplate.catalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;

    public Account getAccount(String userId) {
        Optional<Account> optionalAccount = accountRepository.findById(userId);
        return optionalAccount.orElseThrow(() -> new NoSuchElementException(userId));
    }

    public Account save(Account account) {
        Account savedAccount = accountRepository.save(account);
        return savedAccount;
    }
}
