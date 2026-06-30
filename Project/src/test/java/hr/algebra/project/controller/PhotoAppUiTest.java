package hr.algebra.project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PhotoAppUiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUiElements_AnonymousUser_SeesSearchButNotUpload() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Search")))
                .andExpect(content().string(not(containsString("Upload a Photo"))));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testUiElements_AuthenticatedUser_SeesUploadForm() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Search")))
                .andExpect(content().string(containsString("Upload a Photo")));
    }
}
