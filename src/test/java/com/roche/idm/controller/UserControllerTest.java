package com.roche.idm.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

	private final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

	@Autowired
	private WebApplicationContext webCtx;
	private MockMvc mockMvc;

	@Before
	public void init() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webCtx).build();
	}

	@Test
	public void getUser() throws Exception {
		mockMvc.perform(get("/users/get?q=antczakm")).andDo(result -> {
			String output = result.getResponse().getContentAsString();
			logger.info(output);
		}).andExpect(status().is(200));
	}

	@Test
	public void searchUsers() throws Exception {
		mockMvc.perform(get("/users/search?q=ant")).andDo(result -> {
			String output = result.getResponse().getContentAsString();
			logger.info(output);
		}).andExpect(status().is(200));
	}
}