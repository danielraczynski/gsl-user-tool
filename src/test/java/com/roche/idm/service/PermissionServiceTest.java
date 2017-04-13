package com.roche.idm.service;

import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.roche.idm.model.User;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PermissionServiceTest {

	private final Logger logger = LoggerFactory.getLogger(PermissionService.class);

	@Autowired
	private PermissionService service;

	@Test
	public void getAllUsers() throws Exception {
	}

	@Test
	public void getAffectedUsers() throws Exception {
		//TODO
	}

	@Test
	public void getUserDetail() throws Exception {
		//TODO
	}
}