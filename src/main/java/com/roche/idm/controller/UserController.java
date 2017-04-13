package com.roche.idm.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.roche.idm.model.User;
import com.roche.idm.service.PermissionService;

@RestController
@RequestMapping("/users")
public class UserController {

	private PermissionService permissionService;

	@Autowired
	public UserController(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	@RequestMapping("search")
	public List<User> search(@RequestParam("q") String keyword) {
		return permissionService.searchUser(keyword);
	}

	@RequestMapping("get")
	public User get(@RequestParam("q") String username) {
		return permissionService.getUserDetail(username);
	}

	@RequestMapping("load")
	public List<User> load() {
		return permissionService.loadUsers();
	}

	@RequestMapping("apply")
	public void apply(@RequestBody User user) {
		//TODO add implementation
	}

	@RequestMapping("remove")
	public void remove(@RequestBody User user) {
		permissionService.remove(user);
	}
}
