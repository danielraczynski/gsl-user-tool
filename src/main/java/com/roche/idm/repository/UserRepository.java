package com.roche.idm.repository;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.*;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.SingleContextSource;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.roche.idm.model.Group;
import com.roche.idm.model.User;

@Repository
public class UserRepository {

	private final Logger logger = LoggerFactory.getLogger(UserRepository.class);

	private LdapTemplate ldapTemplate;

	private String USERNAME = "sAMAccountName";
	private String MEMBER_OF = "memberOf";
	private String OBJECT_CLASS = "objectClass";
	private String USER = "user";
	private String PERSON = "person";
	private String COMPUTER = "computer";
	private String FIRST_NAME = "rocheLegalGivenName";
	private String LAST_NAME = "rocheLegalSurname";
	private String MAIL = "mail";
	private String DISTINGUISHED_NAME = "distinguishedName";
	private String IS_DISABLED = "ont-accessstatus";
	private String DISABLED = "Disabled";

	@Autowired
	public UserRepository(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	/**
	 * Checks whether given user belongs to the group
	 */
	public Boolean hasGroup(User user, Group group) {
		List<User> users = ldapTemplate.search(query()
				.where(USERNAME).is(user.getUsername())
				.and(MEMBER_OF).is(group.getName()), new UserAttributeMapper());
		if (users.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Searches for users in Roche AD given by keyword
	 *
	 * @param keyword search keyword
	 * @return matches found (max 20)
	 */
	public List<User> searchUser(String keyword) {
		final Integer countLimit = 20;
		List<User> users = ldapTemplate.search(query().countLimit(countLimit)
				.where(OBJECT_CLASS).is(PERSON)
				.and(OBJECT_CLASS).is(USER)
				.and(OBJECT_CLASS).not().is(COMPUTER)
				.and(IS_DISABLED).not().is(DISABLED)
				.and(query().where(USERNAME).like(appendWildcard(keyword))
						.or(DISTINGUISHED_NAME).like(appendWildcard(keyword))
						.or(FIRST_NAME).like(appendWildcard(keyword))
						.or(LAST_NAME).like(appendWildcard(keyword))
						.or(MAIL).like(appendWildcard(keyword))), new UserAttributeMapper());
		return users;
	}

	/**
	 * Returns user given by username
	 */
	public User getUser(String username) {
		List<User> result = ldapTemplate.search(query().where(USERNAME).is(username), new UserAttributeMapper());
		if (CollectionUtils.isEmpty(result)) {
			result = Collections.emptyList();
		}
		return result.get(0);
	}

	private String appendWildcard(String keyword) {
		StringBuilder sb = new StringBuilder(keyword);
		sb.append("*");
		return sb.toString();
	}

	class UserAttributeMapper implements AttributesMapper<User> {
		@Override
		public User mapFromAttributes(Attributes attributes) throws NamingException {
			User user = new User();
			Attribute firstName = attributes.get(FIRST_NAME);
			Objects.requireNonNull(firstName);
			user.setFirstName(firstName.get().toString());
			Attribute lastName = attributes.get(LAST_NAME);
			Objects.requireNonNull(lastName);
			user.setLastName(lastName.get().toString());
			Attribute username = attributes.get(USERNAME);
			Objects.requireNonNull(username);
			user.setUsername(username.get().toString());
			Attribute email = attributes.get(MAIL);
			if (email == null) {
				logger.error("User {} has no email address", username.get().toString());
				user.setEmail("");
			} else {
				user.setEmail(email.get().toString());
			}
			return user;
		}
	}
}
