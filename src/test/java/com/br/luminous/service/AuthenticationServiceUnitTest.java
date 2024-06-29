package com.br.luminous.service;

import com.br.luminous.entity.User;
import com.br.luminous.exceptions.EmailAlreadyExistsException;
import com.br.luminous.models.UserRequest;
import com.br.luminous.repository.TokenRepository;
import com.br.luminous.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationServiceUnitTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private UserRequest createAValidUserRequest() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("João Silva");
        userRequest.setEmail("joao_silva@hotmail.com");
        userRequest.setPhone("988552233");
        userRequest.setUserName("joao_silva321");
        userRequest.setPassword("teste");
        userRequest.setBirthdate(LocalDate.of(2000, 1, 10));

        return userRequest;
    }

    public User createMockUser(UserRequest userRequest) {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName(userRequest.getName());
        mockUser.setEmail(userRequest.getEmail());
        mockUser.setPhone(userRequest.getPhone());
        mockUser.setUserName(userRequest.getUserName());
        mockUser.setPassword(userRequest.getPassword());
        mockUser.setBirthdate(userRequest.getBirthdate());
        return mockUser;
    }

    /*
     * CT001 - Validar criação de usuário (todas as entradas válidas)
     */
    @Test
    public void shouldCreateAUserGivenAValidUserRequest() {
        // Arrange
        UserRequest userRequest = createAValidUserRequest();
        User mockUser = createMockUser(userRequest);

        when(userRepository.save((any(User.class)))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            userToSave.setId(1L);
            return userToSave;
        });

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        Long userId = authenticationService.register(userRequest);
        User userCreated = userRepository.findById(userId).orElse(null);

        // Assert
        assertNotNull(userCreated, "O usuário não foi cadastrado no sistema!");
        assertEquals(userRequest.getName(), userCreated.getName());
        assertEquals(userRequest.getEmail(), userCreated.getEmail());
        assertEquals(userRequest.getPhone(), userCreated.getPhone());
        assertEquals(userRequest.getUserName(), userCreated.getUserName());
        assertEquals(userRequest.getPassword(), userCreated.getPassword());
        assertEquals(userRequest.getBirthdate(), userCreated.getBirthdate());
    }

    /*
     * CT002 - Validar e-mail informado já existente
     */
    @Test
    public void shouldNotCreateAUserGivenADuplicatedEmail() {
        // Arrange
        UserRequest userRequest = createAValidUserRequest();
        userRequest.setEmail("joao321@hotmail.com");
        userRequest.setPassword("password");

        doThrow(new EmailAlreadyExistsException())
                .when(userService)
                .checkEmailAlreadyExists(userRequest.getEmail());

        // Act
        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> authenticationService.register(userRequest)
        );

        // Assert
        assertEquals("Email already exists.", exception.getMessage());
        verify(userService, times(1)).checkEmailAlreadyExists(userRequest.getEmail());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(tokenRepository);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(authenticationManager);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(emailService);
    }

    /*
     * CT003 - Validar nome informado (nome inválido)
     */
    @Test
    public void shouldNotCreateAUserGivenAnInvalidName() {
        // Arrange
        UserRequest userRequest = createAValidUserRequest();
        userRequest.setName(" ");

        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.register(userRequest);
        });

        // Arrange
        assertEquals("Invalid name.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(userService, never()).checkEmailAlreadyExists(anyString());
    }

    /*
     * CT004 - E-mail inválido (sem “@”)
     */
    @Test
    public void shouldNotCreateAUserGivenAnInvalidEmail() {
        // Arrange
        UserRequest userRequest = createAValidUserRequest();
        userRequest.setEmail("joao_silva");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.register(userRequest);
        });

        // Assert
        assertEquals("Invalid e-mail.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    /*
    * CT005 - Nome de usuário inválido (contento espaços)
    */
    @Test
    public void shouldNotCreateAUserGivenAnInvalidUsername() {
        UserRequest userRequest = createAValidUserRequest();
        userRequest.setUserName("joao silva");

        Exception exception = assertThrows(Exception.class, () -> {
            authenticationService.register(userRequest);
        });

        assertEquals("Invalid username.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
