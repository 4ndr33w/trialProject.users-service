package ru.authorization.auth.configurations;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import jakarta.servlet.http.HttpServletRequest;
import ru.authorization.auth.utils.StaticResources;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

//@ExtendWith(MockitoExtension.class)
public class AuthenticationFilterTests extends ConfigurationTestsUtils {

    @Mock
    private HttpServletRequest request;
/*
    @Test
    public void testGetUsernamePasswordFromRequest_ValidCredentials_ReturnsUsernamePassword() {

        when(request.getHeader(validAuthorizationHeader)).thenReturn(validAuthorizationHeaderValue);

        String[] result = filter.getUsernamePasswordFromRequest(request);

        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals(username, result[0]);
        assertEquals(password, result[1]);
    }*/
/*
    @Test
    public void testGetUsernamePasswordFromRequest_InvalidCredentials_ThrowsIllegalArgumentException() {

        String invalidCredentials = "invalidCredentials"; // Некорректные данные (без разделителя ":")
        String base64Credentials = java.util.Base64.getEncoder().encodeToString(invalidCredentials.getBytes());
        String invalidAuthorizationHeaderValue = "Basic " + base64Credentials;

        when(request.getHeader("Authorization")).thenReturn(invalidAuthorizationHeaderValue);


        //AuthenticationFilter filter = new AuthenticationFilter();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            filter.getUsernamePasswordFromRequest(request);
        });

        assertEquals(StaticResources.USERNAME_OR_PASSWORD_IS_NULL_EXCEPTION_MESSAGE, exception.getMessage());
    }*/
/*
    @Test
    public void testGetUsernamePasswordFromRequest_InvalidCredentials_ThrowsIllegalArgumentException() {

        when(request.getHeader(validAuthorizationHeader)).thenReturn(invalidAuthorizationHeaderValue);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            filter.getUsernamePasswordFromRequest(request);
        });
        assertEquals(StaticResources.USERNAME_OR_PASSWORD_IS_NULL_EXCEPTION_MESSAGE, exception.getMessage());
    }*/
/*
    @Test
    public void testGetUsernamePasswordFromRequest_MissingAuthorizationHeader_ThrowsIllegalArgumentException() {

        when(request.getHeader("Authorization")).thenReturn(null);

        //AuthenticationFilter filter = new AuthenticationFilter();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            filter.getUsernamePasswordFromRequest(request);
        });

        assertEquals(StaticResources.USERNAME_OR_PASSWORD_IS_NULL_EXCEPTION_MESSAGE, exception.getMessage());
    }*/
}
