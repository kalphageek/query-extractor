package me.kalpha.jdbctemplate.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    public Account getAccount(String userId) {
        Optional<Account> optionalAccount = accountRepository.findById(userId);
        return optionalAccount.orElseThrow(() -> new NoSuchElementException(userId));
    }

    public Account save(Account account) {
        Account savedAccount = accountRepository.save(account);
        return savedAccount;
    }
}
