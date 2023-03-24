package me.kalpha.jdbctemplate.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {
    @Autowired
    AccountService accountService;

    @Test
    public void findById() {
        Account account = Account.builder()
                .email("yu@email.com")
                .role("USER")
                .userId("yu")
                .password("yu")
                .build();
        Account savedAccount = accountService.save(account);

        Account findAccount = accountService.getAccount(account.getUserId());

        assertTrue(savedAccount.getUserId().equals(findAccount.getUserId()));
    }

    @DisplayName("NoSuchElementException.class 발생 테스트")
    @Test
    public void findById_notfound() {
        String userId = "hong1";

        assertThrows(NoSuchElementException.class, () -> {
            accountService.getAccount(userId);
        });
    }
}