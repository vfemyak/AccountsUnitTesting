package com.epam.lab.accounts.accounts.facade;

import com.epam.lab.accounts.accounts.dto.AccountDTO;
import com.epam.lab.accounts.accounts.model.requests.CreateUpdateAccountRequest;
import com.epam.lab.accounts.accounts.service.AccountService;
import com.epam.lab.accounts.accounts.service.ErrorsService;
import com.epam.lab.accounts.accounts.service.SessionService;
import com.epam.lab.accounts.accounts.validator.CreateAccountRequestRequestValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountFacadeTest {

    @Mock
    private AccountService accountService;

    private AccountFacade accountFacade = new AccountFacade();
    private CreateAccountRequestRequestValidator requestValidator = new CreateAccountRequestRequestValidator();
    private ErrorsService errorsService = new ErrorsService();
    private MockHttpSession httpSession = new MockHttpSession();

    @Mock
    private SessionService sessionService;

    @Before
    public void resetSession() {
        setField(accountFacade, "sessionService", sessionService);
        setField(accountFacade, "accountService", accountService);
        setField(requestValidator, "errorsService", errorsService);
        setField(requestValidator, "accountService", accountService);
        setField(errorsService, "sessionService", sessionService);
        setField(accountFacade, "createAccountRequestValidator", requestValidator);
        setField(sessionService, "session", httpSession);
//        setField(accountService, "userService", userService);
    }

    @Test
    public void testCreateAccountForCurrentUser() {
        //GIVEN
        final CreateUpdateAccountRequest createAccountRequest = getCreateAccountRequest();
        final AccountDTO accountDTO = getNewAccount();
        when(accountService.isAccountExistsForAccountCode(createAccountRequest.getAccountCode()))
                .thenReturn(false);
        //WHEN
        accountFacade.handleCreateOrUpdateAccountRequest(createAccountRequest);
        //THEN
        verify(accountService).createAccountForCurrentUser(accountDTO);
    }

    @Test
    public void testUpdateAccountForCurrentUser() {
        //GIVEN
        final CreateUpdateAccountRequest updateAccountRequest = getUpdateAccountRequest();
        final AccountDTO accountDTO = getExistingAccount();
        when(accountService.isAccountExistsForAccountCode(updateAccountRequest.getAccountCode()))
                .thenReturn(true);
        when(accountService.getAccountForAccountCode(updateAccountRequest.getAccountCode())).thenReturn(accountDTO);
        //WHEN
        accountFacade.handleCreateOrUpdateAccountRequest(updateAccountRequest);
        //THEN
        verify(accountService).updateAccount(accountDTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountForCurrentUserNegativeTest() {
        final CreateUpdateAccountRequest createUpdateAccountRequest = getIllegalCreateAccountRequest();
        when(accountService.isAccountExistsForAccountCode(createUpdateAccountRequest.getAccountCode()))
                .thenReturn(false);
        accountFacade.handleCreateOrUpdateAccountRequest(createUpdateAccountRequest);
    }

    @Test
    public void testUpdateAccountForCurrentUserNegativeTest() {
        final CreateUpdateAccountRequest createUpdateAccountRequest = getIllegalUpdateAccountRequest();
        when(accountService.isAccountExistsForAccountCode(createUpdateAccountRequest.getAccountCode()))
                .thenReturn(true);
//        when(!UrlValidator.getInstance().isValid(createUpdateAccountRequest.getAccountImage())).thenReturn(false);
        accountFacade.handleCreateOrUpdateAccountRequest(createUpdateAccountRequest);
    }

    private AccountDTO getNewAccount() {
        final AccountDTO accountDTO = new AccountDTO();
        accountDTO.setCode("default-code");
        accountDTO.setName("default-name");
        accountDTO.setBalance(BigDecimal.ZERO);
        accountDTO.setImg("https://cdn1us.denofgeek.com/sites/denofgeekus/files/styles/main_wide/public/2019/03/apple-streaming-service.jpg?itok=nz4ojFvf");
        return accountDTO;
    }

    private AccountDTO getExistingAccount() {
        final AccountDTO accountDTO = new AccountDTO();
        BigDecimal balance = new BigDecimal(2500000.0);
        accountDTO.setCode("nokia-lumia");
        accountDTO.setName("Nokia Lumia Department");
        accountDTO.setBalance(balance);
        accountDTO.setImg("https://upload.wikimedia.org/wikipedia/commons/thumb/f/f1/Nokia_Lumia_logo.svg/1280px-Nokia_Lumia_logo.svg.png");
        return accountDTO;
    }

    private CreateUpdateAccountRequest getCreateAccountRequest() {
        final AccountDTO accountDTO = getNewAccount();
        return getCreateUpdateAccountRequest(accountDTO);
    }

    private CreateUpdateAccountRequest getUpdateAccountRequest() {
        final AccountDTO accountDTO = getExistingAccount();
        return getCreateUpdateAccountRequest(accountDTO);
    }

    private CreateUpdateAccountRequest getCreateUpdateAccountRequest(AccountDTO accountDTO) {
        final CreateUpdateAccountRequest createAccountRequest = new CreateUpdateAccountRequest();
        createAccountRequest.setAccountCode(accountDTO.getCode());
        createAccountRequest.setAccountName(accountDTO.getName());
        createAccountRequest.setAccountBalance(accountDTO.getBalance());
        createAccountRequest.setAccountImage(accountDTO.getImg());
        return createAccountRequest;
    }

    private CreateUpdateAccountRequest getIllegalCreateAccountRequest() {
        final AccountDTO accountDTO = getNewAccount();
        final CreateUpdateAccountRequest createAccountRequest = new CreateUpdateAccountRequest();
        createAccountRequest.setAccountCode(accountDTO.getCode());
        return createAccountRequest;
    }

    private CreateUpdateAccountRequest getIllegalUpdateAccountRequest() {
        final AccountDTO accountDTO = getExistingAccount();
        final CreateUpdateAccountRequest updateAccountRequest = new CreateUpdateAccountRequest();
        updateAccountRequest.setAccountCode(accountDTO.getCode());
        updateAccountRequest.setAccountBalance(accountDTO.getBalance());
        updateAccountRequest.setAccountName(accountDTO.getName());
        updateAccountRequest.setAccountImage("flowers.png");    //wrong format
        return updateAccountRequest;
    }
}
