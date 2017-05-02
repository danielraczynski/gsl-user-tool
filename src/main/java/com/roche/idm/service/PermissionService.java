package com.roche.idm.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roche.emea.adrs.AdrsServiceSoap;
import com.roche.idm.model.Group;
import com.roche.idm.model.User;
import com.roche.idm.repository.CsvDataRepository;
import com.roche.idm.repository.UserRepository;

@Service
public class PermissionService {

	private final Logger logger = LoggerFactory.getLogger(PermissionService.class);

	private CsvDataRepository csvDataRepository;
	private UserRepository userRepository;
	private AdrsServiceSoap adrsServiceSoap;

	private String INTERNAL = "CN=GLOIDM_APPS_NP_USERS_INTERNAL,OU=Applications,OU=Groups,DC=nala,DC=roche,DC=com";
	private String QT_HOME = "CN=GLOIDM_APPS_NP_UDOM_QT_HOME,OU=Applications,OU=Groups,DC=nala,DC=roche,DC=com";
	private String D2_TRAINED = "CN=GLOIDM_APPS_NP_D2_TRAINED,OU=Applications,OU=Groups,DC=nala,DC=roche,DC=com";

	@Autowired
	public PermissionService(CsvDataRepository csvDataRepository, UserRepository userRepository, AdrsServiceSoap adrsServiceSoap) {
		this.adrsServiceSoap = adrsServiceSoap;
		this.csvDataRepository = csvDataRepository;
		this.userRepository = userRepository;
	}

	public User getUserDetail(String username) {
		User result = userRepository.getUser(username);
		Group internal = new Group(INTERNAL);
		Group qt = new Group(QT_HOME);
		Group trained = new Group(D2_TRAINED);
		if (userRepository.hasGroup(result, internal)) {
			result.getGroups().add(internal);
		}
		if (userRepository.hasGroup(result, qt)) {
			result.getGroups().add(qt);
		}
		if (userRepository.hasGroup(result, trained)) {
			result.getGroups().add(trained);
		}
		return result;
	}

	/**
	 * Search user in Roche AD
	 */
	public List<User> searchUser(String keyword) {
		return userRepository.searchUser(keyword);
	}

	public List<User> loadUsers() {
		return this.csvDataRepository.loadUsers();
	}

	public void apply(User user) {
		List<Group> allowable = getAllowableGroups();
		List<Group> requested = user.getGroups().stream().filter(allowable::contains).collect(Collectors.toList());
		List<Group> current = getUserDetail(user.getUsername()).getGroups();
		List<Group> toBeAdded = requested.stream().filter(item -> !current.contains(item)).collect(Collectors.toList());
		List<Group> toBeRemoved = current.stream().filter(item -> !requested.contains(item)).collect(Collectors.toList());

		toBeAdded.forEach(item -> {
			adrsServiceSoap
					.addMember(item.getName(), userRepository.getUserDistinguishedName(user.getUsername()));
			logger.info("User : {} added to Group {}.", user.getUsername(), item.getName());
		});
		toBeRemoved.forEach(item -> {
			adrsServiceSoap
					.removeMember(item.getName(), userRepository.getUserDistinguishedName(user.getUsername()));
			logger.info("User : {} removed from Group {}.", user.getUsername(), item.getName());
		});
	}

	public void tryRemove(User user) {
		List<User> oldUsers = csvDataRepository.loadUsers();
		Boolean wasRemoved = oldUsers.remove(user);
		if (wasRemoved) {
			logger.info("Removing user : {}", user.getUsername());
		}
		csvDataRepository.saveObjectList(User.class, oldUsers);
	}

	private List<Group> getAllowableGroups() {
		List<Group> allowableGroups = new ArrayList<>();
		allowableGroups.add(new Group(INTERNAL));
		allowableGroups.add(new Group(QT_HOME));
		allowableGroups.add(new Group(D2_TRAINED));
		return allowableGroups;
	}

	public void tryAddToFile(User user) {
		List<User> current = csvDataRepository.loadUsers();
		if (current.contains(user)) {
			return;
		}
		user.setGroups(new ArrayList<>());
		current.add(user);
		Collections.sort(current);
		csvDataRepository.saveObjectList(User.class, current);
		logger.info("Added user : {} to the file.", user.getUsername());
	}
}
