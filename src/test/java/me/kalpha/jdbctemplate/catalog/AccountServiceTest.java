package me.kalpha.jdbctemplate.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AccountServiceTest {
    @Autowired
    AccountService accountService;

    @Test
    public void findById() {
        String userId = "hong";
        Account account = accountService.getAccount(userId);

        assertTrue(account.getUserId().equals(userId));
    }

    @Test
    public void findById_notfound() {
        String userId = "hong1";
        Account account = accountService.getAccount(userId);

        assertTrue(account.getUserId() == null);
    }
}