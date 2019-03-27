package com.epam.lab.accounts.accounts.facade;

import com.epam.lab.accounts.accounts.dto.AccountDTO;
import com.epam.lab.accounts.accounts.model.requests.CreateUpdateAccountRequest;
import com.epam.lab.accounts.accounts.service.AccountService;
import com.epam.lab.accounts.accounts.service.ErrorsService;
import com.epam.lab.accounts.accounts.service.SessionService;
import com.epam.lab.accounts.accounts.validator.CreateAccountRequestRequestValidator;
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
        final CreateUpdateAccountRequest createAccountRequest = getDefaultCreateAccountRequest();
        final AccountDTO accountDTO = getNewAccount();
//        when(accountService.isAccountAssignedToUser(createAccountRequest.getAccountCode(),
//                "tommy.johnson@gmail.com")).thenReturn(true);
//        when(accountService.isAccountExistsForAccountCode(createAccountRequest.getAccountCode())).thenReturn(false);
        when(accountService.isAccountExistsForAccountCode(createAccountRequest.getAccountCode()))
                .thenReturn(false);
        //todo add userFacade or userService
        //WHEN
        accountFacade.handleCreateOrUpdateAccountRequest(createAccountRequest);
        //THEN
        verify(accountService).createAccountForCurrentUser(accountDTO);
//        verify(accountService).isAccountAssignedToUser(accountDTO.getCode(), "tommy.johnson@gmail.com");
    }

//    @Test
//    public void testUpdateAccountForCurrentUser() {
//        //GIVEN
//        final CreateUpdateAccountRequest createAccountRequest = getDefaultCreateAccountRequest();
//        final AccountDTO accountDTO = getNewAccount();
////        when(accountService.isAccountAssignedToUser(createAccountRequest.getAccountCode(),
////                "tommy.johnson@gmail.com")).thenReturn(true);
////        when(accountService.isAccountExistsForAccountCode(createAccountRequest.getAccountCode())).thenReturn(false);
//        when(accountService.isAccountExistsForAccountCode(createAccountRequest.getAccountCode()))
//                .thenReturn(true);
//        //todo add userFacade or userService
//        //WHEN
//        accountFacade.handleCreateOrUpdateAccountRequest(createAccountRequest);
//        //THEN
//        verify(accountService).createAccountForCurrentUser(accountDTO);
////        verify(accountService).isAccountAssignedToUser(accountDTO.getCode(), "tommy.johnson@gmail.com");
//    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountForCurrentUserNegativeTest() {
        final CreateUpdateAccountRequest createUpdateAccountRequest = getIllegalCreateAccountRequest();
        when(accountService.isAccountExistsForAccountCode(createUpdateAccountRequest.getAccountCode()))
                .thenReturn(false);
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

    private CreateUpdateAccountRequest getDefaultCreateAccountRequest() {
        final AccountDTO accountDTO = getNewAccount();
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
}
