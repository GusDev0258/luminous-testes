package com.br.luminous.service;

import com.br.luminous.entity.User;
import com.br.luminous.models.UserRequest;
import com.br.luminous.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationServiceUnitTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /*
     * CT001 - Validar criação de usuário (todas as entradas válidas)
     */
    @Test
    public void shouldCreateAUserGivenAValidUserRequest() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("João Silva");
        userRequest.setEmail("joao321@hotmail.com");
        userRequest.setPhone("988552233");
        userRequest.setUserName("joao_silva321");
        userRequest.setPassword("ct001teste");
        userRequest.setBirthdate(LocalDate.of(2000, 1, 10));

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName(userRequest.getName());
        mockUser.setEmail(userRequest.getEmail());
        mockUser.setPhone(userRequest.getPhone());
        mockUser.setUserName(userRequest.getUserName());
        mockUser.setPassword(userRequest.getPassword());
        mockUser.setBirthdate(userRequest.getBirthdate());

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            userToSave.setId(1L);
            return userToSave;
        });

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        Long userId = authenticationService.register(userRequest);
        User userCreated = userRepository.findById(userId).orElse(null);

        assertNotNull(userCreated, "O usuário não foi cadastrado no sistema!");
        assertEquals(userRequest.getName(), userCreated.getName());
        assertEquals(userRequest.getEmail(), userCreated.getEmail());
        assertEquals(userRequest.getPhone(), userCreated.getPhone());
        assertEquals(userRequest.getUserName(), userCreated.getUserName());
        assertEquals(userRequest.getPassword(), userCreated.getPassword());
        assertEquals(userRequest.getBirthdate(), userCreated.getBirthdate());

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findById(1L);
    }

    /*
     * CT002 - Validar e-mail informado já existente
     */
    @Test
    public void shouldNotCreateAUserGivenADuplicatedEmail() {
        UserRequest firstUserRequest = new UserRequest();
        firstUserRequest.setName("João Silva");
        firstUserRequest.setEmail("joao321@hotmail.com");
        firstUserRequest.setPhone("988552233");
        firstUserRequest.setUserName("joao_silva321");
        firstUserRequest.setPassword("ct002teste");
        firstUserRequest.setBirthdate(LocalDate.of(2000, 1, 10));

        UserRequest duplicatedUserRequest = new UserRequest();
        duplicatedUserRequest.setName("João Gomes");
        duplicatedUserRequest.setEmail("joao321@hotmail.com");
        duplicatedUserRequest.setPhone("983182572");
        duplicatedUserRequest.setUserName("gomezinhojoao");
        duplicatedUserRequest.setPassword("ct002teste");
        duplicatedUserRequest.setBirthdate(LocalDate.of(2002, 3, 30));

        var userId = authenticationService.register(firstUserRequest);
        User userCreated = userRepository.findById(userId).orElse(null);

        assertNull(userCreated);
    }

    /*
     * CT003 - Validar nome informado (nome inválido)
     */
    @Test
    public void shouldNotCreateAUserGivenAnInvalidName() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName(" ");
        userRequest.setEmail("joao_silva@hotmail.com");
        userRequest.setPhone("988552233");
        userRequest.setUserName("joaosilva321");
        userRequest.setPassword("ct003teste");
        userRequest.setBirthdate(LocalDate.of(2000, 1, 10));

        Long userId = authenticationService.register(userRequest);
        User userCreated = userRepository.findById(userId).orElse(null);

        assertNull(userCreated);
    }

    /*
     * CT004 - E-mail inválido (sem “@”)
     */
    @Test
    public void shouldNotCreateAUserGivenAnInvalidEmail() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("João da Silva");
        userRequest.setEmail("joao_silva");
        userRequest.setPhone("988552233");
        userRequest.setUserName("joaosilva000");
        userRequest.setPassword("ct004teste");
        userRequest.setBirthdate(LocalDate.of(2000, 1, 10));

        Long userId = authenticationService.register(userRequest);
        User userCreated = userRepository.findById(userId).orElse(null);

        assertNull(userCreated);
    }

    /*
    * CT005 - Nome de usuário inválido (contento espaços)
    */
    @Test
    public void shouldNotCreateAUserGivenAnInvalidUsername() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("João da Silva");
        userRequest.setEmail("joaosilva@gmail.com");
        userRequest.setPhone("988552233");
        userRequest.setUserName("joao silva");
        userRequest.setPassword("ct005teste");
        userRequest.setBirthdate(LocalDate.of(2000, 1, 10));

        Long userId = authenticationService.register(userRequest);
        User userCreated = userRepository.findById(userId).orElse(null);

        assertNull(userCreated);
    }

}
