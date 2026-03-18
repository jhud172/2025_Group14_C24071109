package uk.ac.cf._5.group14.One_To_One.Vault;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.cf._5.group14.One_To_One.Users.Role;
import uk.ac.cf._5.group14.One_To_One.Users.User;
import uk.ac.cf._5.group14.One_To_One.Users.UserRepository;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VaultSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VaultNoteRepository vaultNoteRepository;

    private User userA;
    private User userB;

    @BeforeEach
    void setup() {
        vaultNoteRepository.deleteAll();

        String suffix = UUID.randomUUID().toString().replace("-", "");

        userA = new User("vaultA+" + suffix + "@example.com", "Vault", "A", "vault_a_" + suffix, "password123");
        userA.setRole(Role.CLIENT);
        userA = userRepository.save(userA);

        userB = new User("vaultB+" + suffix + "@example.com", "Vault", "B", "vault_b_" + suffix, "password123");
        userB.setRole(Role.CLIENT);
        userB = userRepository.save(userB);
    }

    @Test
    void cannotViewAnotherUsersVaultNote() throws Exception {
        VaultNote note = new VaultNote(userA.getId(), VaultNoteType.TRAINING, "Private", "secret");
        note = vaultNoteRepository.save(note);

        mockMvc.perform(get("/vault/" + note.getId())
                .with(user(userB.getUsername()).roles("CLIENT")))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void cannotInvokeAiOnAnotherUsersNote() throws Exception {
        VaultNote note = new VaultNote(userA.getId(), VaultNoteType.REFLECTION, "Private", "secret");
        note = vaultNoteRepository.save(note);

        mockMvc.perform(post("/vault/ai/summarise-week")
                .with(user(userB.getUsername()).roles("CLIENT"))
                .with(csrf())
                .param("noteIds", String.valueOf(note.getId()))
                .param("returnTo", "/vault/" + note.getId()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void ownerCanViewAndInvokeAi() throws Exception {
        VaultNote note = new VaultNote(userA.getId(), VaultNoteType.TRAINING, "Week check-in", "Felt good.");
        note = vaultNoteRepository.save(note);

        mockMvc.perform(get("/vault/" + note.getId())
                .with(user(userA.getUsername()).roles("CLIENT")))
            .andExpect(status().isOk());

        mockMvc.perform(post("/vault/ai/rewrite-checkin")
                .with(user(userA.getUsername()).roles("CLIENT"))
                .with(csrf())
                .param("noteIds", String.valueOf(note.getId()))
                .param("returnTo", "/vault/" + note.getId()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/vault/" + note.getId()));
    }
}
