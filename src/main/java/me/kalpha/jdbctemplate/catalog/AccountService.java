package me.kalpha.jdbctemplate.catalog;

import org.springframework.stereotype.Service;

@Service
public interface AccountService {
    public Account getAccount(String userId);

    public Account save(Account account);
}
