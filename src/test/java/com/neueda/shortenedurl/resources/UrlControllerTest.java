package com.neueda.shortenedurl.resources;

import com.neueda.shortenedurl.controller.UserController;
import com.neueda.shortenedurl.dao.user.UserDao;
import com.neueda.shortenedurl.model.AppUser;
import com.neueda.shortenedurl.services.user.UserService;
import com.neueda.shortenedurl.utils.UrlUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    ServletRequest mockHttprequest;
    //MockHttpServletRequest mockHttprequest;

    @MockBean
    UserService userService;

    @Mock
    UserController controller;

    @Mock
    UserDao userDao;

    public static final int URL_CODE_SIZE = 6;

    String token = null;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        AppUser user = new AppUser();
        user.setFirstName("Adam");
        user.setLastName("Lee");
        user.setEmail("andrew@mail.com");
        user.setUserId(12345678L);
        user.setPassword("welcome123");
        String userjson = "{\"userId\":12345678,\"firstName\":\"Adam\",\"lastName\":\"Lee\",\"email\":\"andrew@mail.com\",\"password\":\"welcome123\"}";
        this.mockMvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON_VALUE).content(userjson)).andExpect(status().isOk());
     //   when(userDao.findByEmail(anyString())).thenReturn(user);
        when(userService.findByEmail(anyString())).thenReturn(user);
        MvcResult result = this.mockMvc.perform(post("/user/token").contentType(MediaType.APPLICATION_JSON_VALUE).content(userjson)).andExpect(status().isOk()).andReturn();
        token = result.getResponse().getContentAsString();
        System.out.println("print token "+token);
    }

    @Test
    public void testCreateUrl() throws Exception {
        String longUrl = "http://www.neueda.com/";
        MockFilterChain filterChain = new MockFilterChain();
        int startIndex = 0;
        int endIndex = startIndex + URL_CODE_SIZE - 1;
        String expectedCode = UrlUtils.generateShortURL(longUrl, startIndex, endIndex);
        System.out.println("print token$$ "+token);
         when(mockHttprequest.getAttribute(anyString())).thenReturn(token);
      //  mockHttprequest.setAttribute("authorization",token);
        this.mockMvc.perform(post("/url/").contentType(MediaType.APPLICATION_JSON_VALUE).content(longUrl))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/urls/" + expectedCode));
    }
}
