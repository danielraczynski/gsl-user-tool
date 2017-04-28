const GROUPS = [
	{
		name: 'Internal',
		value: 'CN=GLOIDM_APPS_NP_USERS_INTERNAL,OU=Applications,OU=Groups,DC=nala,DC=roche,DC=com',
	},
	{
		name: 'Quality Home',
		value: 'CN=GLOIDM_APPS_NP_UDOM_QT_HOME,OU=Applications,OU=Groups,DC=nala,DC=roche,DC=com',
	},
	{
		name: 'D2 Trained',
		value: 'CN=GLOIDM_APPS_NP_D2_TRAINED,OU=Applications,OU=Groups,DC=nala,DC=roche,DC=com',
	},
];

const DOM = {
	// containers
	body: $('body'),
	usersList: $('.users-list__list'),
	autocompleteList: $('.autocomplete__list'),
	autocompleteWrapper: $('.autocomplete'),

	popup: $('.popup'),
	popupWrapper: $('.popup__wrapper'),
	editUserInfo: $('.edit-user-info'),
	editUserGroups: $('.user__groups'),

	// events hooks
	searchInput: $('.js-search'),
	editApplyChanges: $('.js-apply'),
	editCancelChanges: $('.js-cancel'),
	refreshList: $('.js-refresh-list'),
};

const TEMPLATES = {
	user: user => {
		return `
			<li class="users-list__element" data-username="${user.username}">
				<div class="users-list__name">${user.username}</div>
				<div class="users-list__buttons">
					<button class="users-list__button js-edit" data-username="${user.username}">Edit</button>
					<button class="users-list__button js-remove" data-username="${user.username}">Remove</button>
				</div>
			</li>
		`;
	},

	autocompleteItem: user => {
		return `
			<li class="autocomplete__item js-select-user">${user.firstName} ${user.lastName} (${user.username})</li>
		`;
	},

	editUserInfo: user => {
		return `
			<p class="user__username">login: ${user.username}</p>
			<p class="user__last-name">Last Name: ${user.firstName}</p>
			<p class="user__last-name">Last Name: ${user.lastName}</p>
			<p class="user__email">Email: ${user.email}</p>
		`;
	},

	userGroups: group => {
		const checked = group.hasGroup ? ' checked="checked"' : '';
		return `<li class="user__group"><input type="checkbox"${checked} value="${group.value}">${group.name}</li>`;
	}
};

const requestHandler = {
	endpointPrefix: '/users/',

	searchUser: (query) => {
		return fetch(`${requestHandler.endpointPrefix}search/q=${query}`);
	},

	loadUsers: () => {
		return fetch(`${requestHandler.endpointPrefix}load`);
	},

	getUser: (query) => {
		return fetch(`${requestHandler.endpointPrefix}get?q=${query}`);
	},

	setUser: user => {
		return fetch(`${requestHandler.endpointPrefix}apply`, {
			headers: {
				'Accept': 'application/json',
				'Content-Type': 'application/json'
			},
			method: "POST",
			body: JSON.stringify(user),
		});
	},

	removeUser: user => {
		return fetch(`${requestHandler.endpointPrefix}remove`, {
			headers: {
				'Accept': 'application/json',
				'Content-Type': 'application/json'
			},
			method: "POST",
			body: JSON.stringify(user),
		});
	},
};

const responseHandler = {
	toJSON: response => response.json(),

	makeAutocompleteList: responseObject => {
		AutocompleteModule.attachResults(responseObject);
		AutocompleteModule.show();
	}
};

const PopupModule = (() => {
	let currentUser = null;

	const show = (user) => {
		currentUser = user;
		DOM.popup.classList.remove('hidden');
		DOM.editApplyChanges.addEventListener('click', eventsHandlers.applyChanges, true);
		DOM.editCancelChanges.addEventListener('click', eventsHandlers.cancelChanges, true);
		DOM.popupWrapper.scrollTop =0;
		return user;
	};

	const hide = () => {
		currentUser = null;
		DOM.popup.classList.add('hidden');
		DOM.editApplyChanges.removeEventListener('click', eventsHandlers.applyChanges);
		DOM.editCancelChanges.removeEventListener('click', eventsHandlers.cancelChanges);
	};

	const fillUserInfo = user => {
		DOM.editUserInfo.innerHTML = TEMPLATES.editUserInfo(user);
		return user;
	};

	const fillUserGroups = user => {
		const groups = GROUPS.map(group => {
			const hasGroup = !!user.groups.find(usergroup => {
				return usergroup.name === group.value;
			});
			return Object.assign({}, group, {hasGroup});
		});

		const groupslist = groups.map(group => {
			return TEMPLATES.userGroups(group);
		});

		DOM.editUserGroups.innerHTML = groupslist.join('');
		return user;
	};

	const saveUser = () => {
		const selectedGroups = $$('input:checked', DOM.popup).map(checkbox => {
			return {
				name: checkbox.value,
			};
		});
		currentUser.groups = selectedGroups;
		return requestHandler.setUser(currentUser);
	};

	return {
		show,
		hide,
		fillUserInfo,
		fillUserGroups,
		saveUser,
	};
})();

const AutocompleteModule = (() => {

	const attachResults = (responseObject) => {
		const userslist = responseObject.map(user => {
			return TEMPLATES.autocompleteItem(user);
		});

		if (!userslist.length) {
			userslist.push('<li>No users found</li>');
		}

		DOM.autocompleteList.innerHTML = userslist.join('');
		attachElementEvents(DOM.autocompleteList);
	};

	const attachElementEvents = autocompleteListDOM => {
		const userElements = $$('.js-select-user', autocompleteListDOM);

		userElements.forEach(element => {
			element.addEventListener('click', eventsHandlers.selectUserAutocomplete);
		});
	};

	const hide = () => {
		DOM.body.removeEventListener('click', hide);
		DOM.autocompleteWrapper.classList.add('hidden');
		const userElements = $$('.js-select-user', DOM.autocompleteList);

		userElements.forEach(element => {
			element.removeEventListener('click', eventsHandlers.selectUserAutocomplete);
		});
		DOM.autocompleteList.innerHTML = '';
	};

	const show = () => {
		DOM.autocompleteWrapper.classList.remove('hidden');
		DOM.body.addEventListener('click', hide);
		DOM.autocompleteWrapper.addEventListener('click', (event) => {
			event.stopPropagation();
		});
	};

	return {
		attachResults,
		attachElementEvents,
		hide,
		show,
	}
})();

const UsersListModule = (() => {
	const show = () => {
		requestHandler.loadUsers()
			.then(responseHandler.toJSON)
			.then(attachResults);
	};

	const attachResults = users => {
		const userslist = users.map(user => {
			return TEMPLATES.user(user);
		});

		DOM.usersList.innerHTML = userslist.join('');

		attachElementEvents(DOM.usersList);
	};

	const attachElementEvents = usersListDOM => {
		const editUsers = $$('.js-edit', usersListDOM);
		const removeUsers = $$('.js-remove', usersListDOM);

		editUsers.forEach(element => {
			element.addEventListener('click', eventsHandlers.editUser);
		});

		removeUsers.forEach(element => {
			element.addEventListener('click', eventsHandlers.removeUser);
		});
	};

	return {
		show,
	}
})();

const eventsHandlers = {
	autocompleteSearch: (event) => {
		requestHandler.searchUser(event.target.value)
			.then(responseHandler.toJSON)
			.then(responseHandler.makeAutocompleteList);
	},

	selectUserAutocomplete: event => {
		requestHandler.getUser(event.target.dataset.username)
			.then(responseHandler.toJSON)
			.then(PopupModule.show)
			.then(PopupModule.fillUserInfo)
			.then(PopupModule.fillUserGroups)
			.then(AutocompleteModule.hide);

		DOM.searchInput.value = '';
	},

	applyChanges: event => {
		PopupModule.saveUser()
			.then(PopupModule.hide)
			.then(UsersListModule.show);
	},

	cancelChanges: event => {
		PopupModule.hide();
	},

	editUser: event => {
		eventsHandlers.selectUserAutocomplete(event);
	},

	removeUser: event => {
		requestHandler.removeUser({
			username: event.target.dataset.username
		})
			.then(UsersListModule.show);
	}

};
// bind initial events
DOM.searchInput.addEventListener('keyup', debounce(eventsHandlers.autocompleteSearch, 300), true);
DOM.refreshList.addEventListener('click', debounce(UsersListModule.show, 300), true);

// initial actions
UsersListModule.show();


// helpers
function debounce(func, wait, immediate) {
	let timeout;
	return function () {
		let context = this, args = arguments;
		const later = function () {
			timeout = null;
			if (!immediate) func.apply(context, args);
		};
		const callNow = immediate && !timeout;
		clearTimeout(timeout);
		timeout = setTimeout(later, wait);
		if (callNow) func.apply(context, args);
	};
}

function $(selector, el) {
	if (!el) {
		el = document;
	}
	return el.querySelector(selector);
}

function $$(selector, el) {
	if (!el) {
		el = document;
	}
	return Array.prototype.slice.call(el.querySelectorAll(selector));
}